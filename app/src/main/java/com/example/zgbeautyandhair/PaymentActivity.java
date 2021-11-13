package com.example.zgbeautyandhair;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.CreditViewPagerAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.CreditCardFragment.CreditNameFragment;
import com.example.zgbeautyandhair.CreditCardFragment.CreditNumberFragment;
import com.example.zgbeautyandhair.CreditCardFragment.CreditSecureCodeFragment;
import com.example.zgbeautyandhair.CreditCardFragment.CreditValidityFragment;
import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;
import com.example.zgbeautyandhair.Fragments.HomeFragment;
import com.example.zgbeautyandhair.Interface.ILoadTimeFromFirebaseListener;
import com.example.zgbeautyandhair.Interface.SalonSalon;
import com.example.zgbeautyandhair.Model.FCMSendData;
import com.example.zgbeautyandhair.Model.MyNotification;
import com.example.zgbeautyandhair.Model.MyToken;
import com.example.zgbeautyandhair.Model.Order;
import com.example.zgbeautyandhair.Model.ProductItem;
import com.example.zgbeautyandhair.Model.Salon;
import com.example.zgbeautyandhair.Retrofit.IFCMService;
import com.example.zgbeautyandhair.Retrofit.RetrofitClient;
import com.example.zgbeautyandhair.Utils.CreditCardUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.zgbeautyandhair.Common.Common.bookingDate;
import static com.example.zgbeautyandhair.Common.Common.createOrderNumber;
import static com.example.zgbeautyandhair.Common.Common.currentUser;

public class PaymentActivity extends FragmentActivity implements FragmentManager.OnBackStackChangedListener, ILoadTimeFromFirebaseListener, SalonSalon {
    @BindView(R.id.btnNext)
    Button btnNext;

    @BindView(R.id.txt_total_price)
    TextView txt_total_price;

    AlertDialog dialog;

    ILoadTimeFromFirebaseListener listener;

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    CartDataSource cartDataSource;

    public CardFrontFragment cardFrontFragment;
    public CardBackFragment cardBackFragment;

    //This is our viewPager
    private ViewPager viewPager;

    CreditNumberFragment numberFragment;
    CreditNameFragment nameFragment;
    CreditValidityFragment validityFragment;
    CreditSecureCodeFragment secureCodeFragment;

    int total_item;
    boolean backTrack = false;

    private boolean mShowingBack = false;

    String cardNumber, cardCVV, cardValidity, cardName;

    SalonSalon iAllSalonLoadListener;

    ProductItem productItem;

    ImageView back_to_cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        back_to_cart = findViewById(R.id.back_to_cart);

        back_to_cart.setOnClickListener( v ->
                goBackToCart() );

        iAllSalonLoadListener = this;

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        ifcmService = RetrofitClient.getInstance().create( IFCMService.class);

        listener = this;


        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false)
                .build();

        String price = getIntent().getStringExtra("price");

        txt_total_price.setText(price);

        productItem = new ProductItem();

        cardFrontFragment = new CardFrontFragment();
        cardBackFragment = new CardBackFragment();

        if (savedInstanceState == null) {
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, cardFrontFragment).commit();

        } else {
            mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
        }

        getFragmentManager().addOnBackStackChangedListener(this);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == total_item) {
                    btnNext.setText("סיום");
                }

                else
                    btnNext.setText("הבא");

                Log.d("track", "onPageSelected: " + position);

                if (position == total_item) {
                    flipCard();
                    backTrack = true;
                } else if (position == total_item - 1 && backTrack) {
                    flipCard();
                    backTrack = false;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewPager.getCurrentItem();
                if (pos < total_item) {
                    viewPager.setCurrentItem(pos + 1);
                } else {
                    checkEntries();

                }
            }
        });
        loadSalonId();
    }

    private void goBackToCart() {
        startActivity(new Intent(PaymentActivity.this, CartActivity.class));
    }

    private void loadSalonId() {
        List<Salon> salonList = new ArrayList<>();
        CollectionReference salonRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document("ראשון לציון")
                .collection("Branch");


        salonRef.get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Salon salon = documentSnapshot.toObject(Salon.class);
                                salon.setSalonId(documentSnapshot.getId());
                                salonList.add(salon);

                                Common.currentSalon = salon;
                            }
                            iAllSalonLoadListener.onSalonLoadSuccess(salonList);
                        }
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( PaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });
    }

    public void checkEntries() {
        cardName = nameFragment.getName();
        cardNumber = numberFragment.getCardNumber();
        cardValidity = validityFragment.getValidity();
        cardCVV = secureCodeFragment.getValue();

        if (TextUtils.isEmpty(cardName)) {
            Toast.makeText(PaymentActivity.this, "מחזיק הכרטיס", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cardNumber) || !CreditCardUtils.isValid(cardNumber.replace(" ", ""))) {
            Toast.makeText(PaymentActivity.this, "מספר הכרטיס", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cardValidity) || !CreditCardUtils.isValidDate(cardValidity)) {
            Toast.makeText(PaymentActivity.this, "תוקף הכרטיס", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cardCVV) || cardCVV.length() < 3) {
            Toast.makeText(PaymentActivity.this, "3 ספרות בגב הכרטיס", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(cardName) == TextUtils.isEmpty(cardNumber) == TextUtils.isEmpty(cardValidity) == TextUtils.isEmpty(cardCVV))
        {
            getAllCart();
        }

    }

    private void getAllCart() {

        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( cartItems -> {

                    cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe( new SingleObserver<Long>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Long aLong) {
                                    String price = getIntent().getStringExtra("price");
                                    String comment = getIntent().getStringExtra("comment");
                                    String other_address = getIntent().getStringExtra("other_address");

                                    txt_total_price.setText(price);

                                    String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot)[0];
                                    String[] convertTime = startTime.split("-"); //Split ex : 10:00 - 11:00

                                    Calendar bookingDateWithoutHouse = Calendar.getInstance();
                                    bookingDateWithoutHouse.setTimeInMillis(bookingDate.getTimeInMillis());

                                    //Create Timestamp object and apply to BookingInformation
                                    Timestamp timestamp = new Timestamp(bookingDateWithoutHouse.getTime());

                                    Order order = new Order();

                                    order.setCartItemList(cartItems);
                                    order.setTimestamp(timestamp);

                                    order.setUserPhone(currentUser.getPhoneNumber());
                                    order.setUserAddress( currentUser.getAddress());
                                    order.setUserName(Common.currentUser.getFirstName() + "  " + Common.currentUser.getLastName());
                                    order.setComment(comment);
                                    order.setShippingOtherAddress(other_address);
                                    order.setShippingAddress(currentUser.getAddress());
                                    order.setTotalPayment(price);
                                    order.setOrderNumber(createOrderNumber());
                                    order.setCardNumber(cardNumber);
                                    order.setCardHoldName(cardName);
                                    order.setCardValidity(cardValidity);
                                    order.setOrderId(createOrderNumber());

                                    order.setSalonId(Common.currentSalon.getSalonId());
                                    order.setSalonAddress(Common.currentSalon.getAddress());
                                    order.setSalonPhone(Common.currentSalon.getPhone());
                                    order.setSalonName(Common.currentSalon.getName());

                                    syncLocalTimeWithGlobalTime(order);


                                    //After, Add Success Booking, Just Clear Cart
                                    cartDataSource.clearCart( currentUser.getPhoneNumber() )
                                            .subscribeOn( Schedulers.io() )
                                            .observeOn( AndroidSchedulers.mainThread() )
                                            .subscribe( new SingleObserver<Integer>() {
                                                @Override
                                                public void onSubscribe(Disposable d) {

                                                }

                                                @Override
                                                public void onSuccess(Integer integer) {
                                                   // txt_total_price.setText(new StringBuilder("₪").append(aLong));
                                                    sendNotificationUpdateToBarber(order);
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    if (e.getMessage().contains("Query returned empty"))
                                                        txt_total_price.setText("");
                                                    else
                                                        Toast.makeText( PaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                                                    finish();
                                                }
                                            } );
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    if (e.getMessage().contains("Query returned empty"))
                                        txt_total_price.setText("");
                                    else
                                        Toast.makeText( PaymentActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                                    finish();
                                }
                            } );
                }, throwable ->
                        Toast.makeText( PaymentActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT ).show() ));
    }

    @Override
    public void onBackStackChanged() {
        mShowingBack = (getFragmentManager().getBackStackEntryCount() > 0);
    }

    private void setupViewPager(ViewPager viewPager) {
        CreditViewPagerAdapter adapter = new CreditViewPagerAdapter(getSupportFragmentManager());
        numberFragment = new CreditNumberFragment();
        nameFragment = new CreditNameFragment();
        validityFragment = new CreditValidityFragment();
        secureCodeFragment = new CreditSecureCodeFragment();

        adapter.addFragment(numberFragment);
        adapter.addFragment(nameFragment);
        adapter.addFragment(validityFragment);
        adapter.addFragment(secureCodeFragment);

        total_item = adapter.getCount() - 1;
        viewPager.setAdapter(adapter);

    }

    private void flipCard() {
        if (mShowingBack) {
            getFragmentManager().popBackStack();
            return;
        }
        // Flip to the back.
        mShowingBack = true;
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardBackFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int pos = viewPager.getCurrentItem();
        if (pos > 0) {
            viewPager.setCurrentItem(pos - 1);
        } else
            super.onBackPressed();
    }

    public void nextClick() {
        btnNext.performClick();
    }

    private void syncLocalTimeWithGlobalTime(Order order) {
        DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");

        offsetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot orderSnapshot) {
                long offset = orderSnapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Toast.makeText(PaymentActivity.this, "Order Date" + "" + simpleDateFormat.format(resultDate), Toast.LENGTH_SHORT).show();

                listener.onLoadTimeSuccess(order, estimatedServerTimeMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onLoadTimeFailed(error.getMessage());
            }
        });
    }

    private void sendNotificationUpdateToBarber(Order OrderInformation) {
        //First, create new collection
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Orders");

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
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().isEmpty()) {
                        //Set data
                        userBooking.document()
                                .set(OrderInformation)
                                .addOnSuccessListener(aVoid1 -> {
                                    //create notification
                                    MyNotification myNotification = new MyNotification();
                                    myNotification.setUid(UUID.randomUUID().toString());
                                    myNotification.setTitle("הזמנה חדשה");
                                    myNotification.setContent("בוצע הזמנה על ידי " + Common.currentUser.getFirstName() + "  " + Common.currentUser.getLastName() + "  " + Common.currentUser.getPhoneNumber().replaceAll("[+]972", "0"));
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
                                            .addOnSuccessListener(aVoid -> {
                                                // First, get Token base on Barber id
                                                FirebaseFirestore.getInstance()
                                                        .collection("Z&G Tokens")
                                                        .whereEqualTo("userPhone", "זהבה וגלית")
                                                        .limit(1)
                                                        .get()
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful() && task1.getResult().size() > 0) {
                                                                MyToken myToken = new MyToken();
                                                                for (DocumentSnapshot tokenSnapshot : task1.getResult()) {
                                                                    myToken = tokenSnapshot.toObject(MyToken.class);
                                                                }


                                                                Map<String, String> dataSend = new HashMap<>();
                                                                dataSend.put(Common.TITLE_KEY, "תשלום");
                                                                dataSend.put(Common.CONTENT_KEY, "בוצע תשלום על ידי " + Common.currentUser.getFirstName() + "  " + Common.currentUser.getLastName() + "  " + Common.currentUser.getPhoneNumber());

                                                                // Create data to send
                                                                FCMSendData sendRequest = new FCMSendData(Common.createTopicOrder(), dataSend);
                                                                sendRequest.setTo(myToken.getToken());
                                                                sendRequest.setData(dataSend);

                                                                compositeDisposable.add( ifcmService.sendNotification(sendRequest)
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread())
                                                                        .subscribe(fcmResponse -> {
                                                                            if (dialog.isShowing())
                                                                                dialog.dismiss();

                                                                            resetStaticData();
                                                                            finish();
                                                                         //  Toast.makeText(this, "הזמנה בוצעה בהצלחה!", Toast.LENGTH_SHORT).show();

                                                                            Toast mToast = new Toast(this);
                                                                            mToast.setDuration(Toast.LENGTH_LONG);
                                                                            View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                                                            TextView textView = toastView.findViewById(R.id.txt_message);
                                                                            mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                                                            textView.setText("הזמנה בוצעה בהצלחה!");
                                                                            mToast.setView(toastView);
                                                                            mToast.show();


                                                                        }, throwable -> {
                                                                            Log.d("NOTIFICATION_ERROR", throwable.getMessage());
                                                                            dialog.dismiss();

                                                                           // this.resetStaticData();
                                                                            finish();

                                                                            Toast mToast = new Toast(this);
                                                                            mToast.setDuration(Toast.LENGTH_LONG);
                                                                            View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                                                                            TextView textView = toastView.findViewById(R.id.txt_message);
                                                                            mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                                                            textView.setText("הזמנה בוצעה בהצלחה!");
                                                                            mToast.setView(toastView);
                                                                            mToast.show();
                                                                        }));
                                                            }
                                                        });
                                            });
                                }).addOnFailureListener(e -> {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        finish();

                        Toast mToast = new Toast(this);
                        mToast.setDuration(Toast.LENGTH_LONG);
                        View toastView = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.root_layout));
                        TextView textView = toastView.findViewById(R.id.txt_message);
                        mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                        textView.setText("הזמנה בוצעה בהצלחה!");
                        mToast.setView(toastView);
                        mToast.show();

                    }
         });
    }

    private void resetStaticData() {
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.payment_container, homeFragment).commit();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onLoadTimeSuccess(Order order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
    }

    @Override
    public void onLoadTimeFailed(String message) {
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSalonLoadSuccess(List<Salon> salonList) {

    }

    @Override
    public void onSalonLoadFailed(String message) {

    }
}