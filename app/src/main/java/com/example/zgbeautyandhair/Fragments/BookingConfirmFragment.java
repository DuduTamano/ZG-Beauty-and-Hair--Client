package com.example.zgbeautyandhair.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;
import com.example.zgbeautyandhair.Model.BookingInformation;
import com.example.zgbeautyandhair.Model.EventBus.ConfirmBookingEvent;
import com.example.zgbeautyandhair.Model.FCMSendData;
import com.example.zgbeautyandhair.Model.MyNotification;
import com.example.zgbeautyandhair.Model.MyToken;
import com.example.zgbeautyandhair.R;
import com.example.zgbeautyandhair.Retrofit.IFCMService;
import com.example.zgbeautyandhair.Retrofit.RetrofitClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.zgbeautyandhair.Common.Common.bookingDate;
import static com.example.zgbeautyandhair.Common.Common.currentBarber;

public class BookingConfirmFragment extends Fragment {
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    SimpleDateFormat simpleDateFormat;

    CartDataSource cartDataSource;

    AlertDialog dialog;
    Unbinder unbinder;
    IFCMService ifcmService;

    @BindView(R.id.txt_booking_barber_text)
    TextView txt_booking_barber_text;
    @BindView(R.id.txt_booking_time_text)
    TextView txt_booking_time_text;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_open_hours)
    TextView txt_salon_open_hours;
    @BindView(R.id.txt_salon_phone)
    TextView txt_salon_phone;

    @OnClick(R.id.btn_confirm)
    void confirmBooking() {
        dialog.show ();

        compositeDisposable.addAll(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {

                    //Process Timestamp
                    //We will use Timestamp to filter all booking with date is greater today
                    //For only display all future booking
                    String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot)[0];
                    String[] convertTime = startTime.split("-"); //Split ex : 10:00 - 11:00

                    //Get start time : get 10:00
                    String[] startTimeConvert = convertTime[0].split(":");
                    int startHourInt = Integer.parseInt(startTimeConvert[0].trim()); //we get 10
                    int startMinInt = Integer.parseInt(startTimeConvert[1].trim()); //we get 00

                    Calendar bookingDateWithoutHouse = Calendar.getInstance();
                    bookingDateWithoutHouse.setTimeInMillis(bookingDate.getTimeInMillis());
                    bookingDateWithoutHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
                    bookingDateWithoutHouse.set(Calendar.MINUTE, startMinInt);


                    //Create booking information
                    final BookingInformation bookingInformation = new BookingInformation();

                    bookingInformation.setCityBook(Common.city);

                    //Always False, because we will use this field to filter for display on user
                    bookingInformation.setDone(false);
                    bookingInformation.setBarberId(currentBarber.getBarberId());
                    bookingInformation.setBarberName(currentBarber.getName());
                    bookingInformation.setCustomerName(Common.currentUser.getFirstName() + " " + Common.currentUser.getLastName());
                    bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());
                    bookingInformation.setSalonId(Common.currentSalon.getSalonId());
                    bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
                    bookingInformation.setSalonName(Common.currentSalon.getName());
                    bookingInformation.setBookingTime(Common.convertTimeSlotToString(Common.currentTimeSlot)[0]);
                        //    .append(" בתאריך: ")
                    bookingInformation.setSlot(Long.valueOf(Common.currentTimeSlot));

                    bookingInformation.setBookingDate(simpleDateFormat.format(bookingDateWithoutHouse.getTime()));

                    //ADD Cart Item List to Booking Information
                    bookingInformation.setCartItemList(cartItems);
                    bookingInformation.setCustomerImage(Common.currentUser.getImage());

                    bookingInformation.setBookingStatus(0);

                    Calendar bookingCurrentDate = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = simpleDateFormat.format(bookingCurrentDate.getTime());

                    //Create Timestamp object and apply to BookingInformation
                    Timestamp timestamp = new Timestamp(bookingCurrentDate.getTime());

                    bookingInformation.setTimestamp(timestamp);


                    final DocumentReference bookingDate = FirebaseFirestore.getInstance()
                            .collection("AllSalon")
                            .document(Common.city)
                            .collection("Branch")
                            .document(Common.currentSalon.getSalonId())
                            .collection("Barbers")
                            .document(currentBarber.getBarberId())
                            .collection(simpleDateFormat.format(bookingDateWithoutHouse.getTime()))
                            .document(String.valueOf(Common.currentTimeSlot));

                    //Write date
                    bookingDate.set(bookingInformation)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    if (dialog.isShowing())
                                        dialog.dismiss();

                                    Toast mToast = new Toast(getContext());
                                    mToast.setDuration(Toast.LENGTH_LONG);
                                    View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) getActivity().findViewById(R.id.root_layout));
                                    TextView textView = toastView.findViewById(R.id.txt_message);
                                    mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                    textView.setText("תור נשלח למנהל! אנא המתיני לאישור");
                                    mToast.setView(toastView);
                                    mToast.show();


                                    //After, Add Success Booking, Just Clear Cart
                                    cartDataSource.clearCart(Common.currentUser.getPhoneNumber())
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new SingleObserver<Integer>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onSuccess(Integer integer) {
                                                    addToUserBooking(bookingInformation, currentBarber.getUsername());
                                                    getActivity().finish();
                                                    resetStaticData();
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    getActivity().finish();
                                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }).addOnFailureListener(e ->
                            Toast.makeText(BookingConfirmFragment.this.getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show());

                }, throwable -> {
                    Toast.makeText(getContext(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void addToUserBooking(BookingInformation bookingInformation, String barberName) {
        //First, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Check if Document exist in this collection
        // Get current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //Check if exist document in this collection
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("ObsoleteSdkInt")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //Set data
                if (task.getResult().isEmpty()) userBooking.document()
                        .set(bookingInformation)
                        .addOnSuccessListener(aVoid -> {
                            //create notification
                            MyNotification myNotification = new MyNotification();
                            myNotification.setUid(UUID.randomUUID().toString());
                            myNotification.setTitle("תור חדש");
                            myNotification.setContent("נקבע תור חדש על ידי " + Common.currentUser.getFirstName() + "  " + Common.currentUser.getLastName() + "  " + Common.currentUser.getPhoneNumber().replaceAll("[+]972", "0"));
                            //we will only filter notification with 'read' is false on barber staff app
                            myNotification.setRead(false);
                            myNotification.setServerTimestamp(FieldValue.serverTimestamp());
                            myNotification.setImage(Common.currentUser.getImage());

                            // Submit Notification to 'Notifications' collection of Barber
                            FirebaseFirestore.getInstance()
                                    // If  it not available, it will be create automatically
                                    .collection("Notifications")
                                    // Create unique key
                                    .document(myNotification.getUid())
                                    .set(myNotification)
                                    .addOnSuccessListener(aVoid1 ->

                                            FirebaseFirestore.getInstance()
                                                    .collection("Z&G Tokens")
                                                    .whereEqualTo("userPhone", barberName)
                                                    .limit(1)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful() && task1.getResult().size() > 0) {

                                                            MyToken myToken = new MyToken();
                                                            for (DocumentSnapshot tokenSnapshot : task1.getResult())
                                                                myToken = tokenSnapshot.toObject(MyToken.class);

                                                            // Create data to send
                                                            Map<String, String> dataSend = new HashMap<>();
                                                            dataSend.put(Common.TITLE_KEY, "תור חדש");
                                                            dataSend.put(Common.CONTENT_KEY, "נקבע תור חדש על ידי " + Common.currentUser.getFirstName() + "  " + Common.currentUser.getLastName() + "  " + Common.currentUser.getPhoneNumber().replaceAll("[+]972", "0"));

                                                            FCMSendData sendRequest = new FCMSendData();
                                                            sendRequest.setTo(myToken.getToken());
                                                            sendRequest.setData(dataSend);

                                                            compositeDisposable.add(ifcmService.sendNotification(sendRequest)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribe(fcmResponse -> {
                                                                        if (dialog.isShowing())
                                                                            dialog.dismiss();


                                                                        resetStaticData();
                                                                        getActivity().finish();

                                                                    }, throwable -> {
                                                                        Log.d("NOTIFICATION_ERROR", throwable.getMessage());
                                                                        dialog.dismiss();

                                                                        resetStaticData();

                                                                    }));
                                                        }
                                                    }));
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (dialog.isShowing())
                                    dialog.dismiss();
                            }
                        });
                else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    resetStaticData();
                    getActivity().finish();

                }
            }
        });
    }

    private void resetStaticData() {
        Common.step = 0;
        Common.currentTimeSlot = -1;
        Common.currentSalon = null;
        currentBarber = null;
        Common.bookingDate.add(Calendar.DATE,0);
    }

    //=======================================================
    //Event Buss
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void setDataBooking(ConfirmBookingEvent event)
    {
        if (event.isConfirm())
            setData();
    }

    private void setData() {
        //step 4 confirm information
        txt_booking_barber_text.setText(currentBarber.getName());

        txt_booking_time_text.setText(new StringBuilder().append("בשעה: ").append(Common.convertTimeSlotToString(Common.currentTimeSlot)[0])
                .append(" בתאריך: ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())));

        txt_salon_address.setText(Common.currentSalon.getAddress());
        txt_salon_open_hours.setText(Common.currentSalon.getOpenHours());
        txt_salon_phone.setText(Common.currentSalon.getPhone().replaceAll("[+]972", "0"));

    }

    private static BookingConfirmFragment instance;

    public static BookingConfirmFragment getInstance() {
        if (instance == null) instance = new BookingConfirmFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ifcmService = RetrofitClient.getInstance().create( IFCMService.class);

        //Apply format for date display on confirm
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false)
                .build();

    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View itemView = inflater.inflate(R.layout.fragment_booking_confirm,container,false);
        unbinder = ButterKnife.bind(this, itemView);

        //Remember init cartDataSource here, if i don't want to get null reference
        //because getContext return null
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        return itemView;
    }
}