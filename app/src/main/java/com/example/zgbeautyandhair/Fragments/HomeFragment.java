package com.example.zgbeautyandhair.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Config;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zgbeautyandhair.Adapter.HomeSliderAdapter;
import com.example.zgbeautyandhair.Adapter.LookbookAdapter;
import com.example.zgbeautyandhair.BookingActivity;
import com.example.zgbeautyandhair.CartActivity;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.ContactActivity;
import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;
import com.example.zgbeautyandhair.FavoriteActivity;
import com.example.zgbeautyandhair.Interface.IBannerLoadListener;
import com.example.zgbeautyandhair.Interface.IBookingInfoLoadListener;
import com.example.zgbeautyandhair.Interface.IBookingInformationChangeListener;
import com.example.zgbeautyandhair.Interface.ILookbookLoadListener;
import com.example.zgbeautyandhair.InvoiceActivity;
import com.example.zgbeautyandhair.MainActivity;
import com.example.zgbeautyandhair.Model.Banner;
import com.example.zgbeautyandhair.Model.BookingInformation;
import com.example.zgbeautyandhair.Model.EventBus.MyUpdateCartEvent;
import com.example.zgbeautyandhair.OrdersActivity;
import com.example.zgbeautyandhair.R;
import com.example.zgbeautyandhair.Retrofit.IFCMService;
import com.example.zgbeautyandhair.ReturnPolicy;
import com.example.zgbeautyandhair.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment implements IBannerLoadListener,
        NavigationView.OnNavigationItemSelectedListener, IBookingInfoLoadListener, IBookingInformationChangeListener, ILookbookLoadListener {

    private static final int PICK_CAMERA_REQUEST = 7234;
    private static final int STORAGE_REQUEST = 7235;
    String cameraPermission[];
    String[] storagePermission;

    private boolean allowRefresh = false;

    BottomSheetDialog bottomSheetDialog;

    private Uri imgUri;

    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    private Unbinder unbinder;

    //Firebase
    CollectionReference bannerRef,lookbookRef;

    //Interface
    IBannerLoadListener iBannerLoadListener;
     ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    ListenerRegistration userBookingListener = null;
    com.google.firebase.firestore.EventListener<QuerySnapshot> userBookingEvent = null;

    CartDataSource cartDataSource;

    static final float END_SCALE = 0.7f;

    AlertDialog dialog;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigationView)
    NavigationView navigationView;

    @BindView(R.id.menu_icon)
    ImageView menuIcon;

    @BindView(R.id.content)
    LinearLayout contentView;

    ImageView logo_user;

    TextView txt_user_name;

    TextView txt_user_phone;

    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;

    @BindView(R.id.banner_slider)
    Slider banner_slider;

    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;

    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;

    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;

    @BindView(R.id.txt_time)
    TextView txt_time;

    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    @BindView(R.id.txt_order_status)
    TextView txt_order_status;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking()
    {
        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking()
    {
        changeBookingFromUser();
    }

    private void changeBookingFromUser() {
        //Show dialog confirm
        androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("היי!")
                .setMessage("האם את רוצה לשנות את התור שלך?\n אם כן אנו נמחק את התור הקיים\n האם את מאשרת")
                .setNegativeButton("ביטול", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                }).setPositiveButton("מאשרת", (dialogInterface, i) -> {
                    deleteBookingFromBarber(true);
                });
        confirmDialog.show();
    }

    private void deleteBookingFromBarber(boolean isChange) {
        //we need load Common.currentBooking because we need some data from BookingInformation
        if (Common.currentBooking != null)
        {
            dialog.show();

            //get booking information in barber project
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barbers")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.currentBooking.getBookingDate())
                    .document(String.valueOf(Common.currentBooking.getSlot()));

            //When we document, just delete it
            barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //After delete on barber done
                    //we will start delete from User
                    deleteBookingFromUser(isChange, Common.currentBooking.getBookingTime());
                }
            });

        }
        else
        {
          //  Toast.makeText(getContext(), "Current Booking must be null", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(getView(), "תור לא נמחק", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void deleteBookingFromUser(boolean isChange, String bookingTime) {
        //First, we need get information from user object
        if (!TextUtils.isEmpty(Common.currentBookingId)) {

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking")
                .whereEqualTo("bookingTime", bookingTime)
                .get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot deleteSnapshot = task.getResult().getDocuments().get(0);

                    String Id = deleteSnapshot.getId();

                    DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(Common.currentUser.getPhoneNumber())
                            .collection("Booking")
                            .document(Id);

                    userBookingInfo.delete().addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText( getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }).addOnSuccessListener(aVoid -> {

                        Paper.init(getActivity());
                        if(Paper.book().read(Common.EVENT_URI_CACHE) != null)
                        {
                            String eventString = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                            Uri eventUri = null;
                            if (eventString != null && !TextUtils.isEmpty(eventString))
                                eventUri = Uri.parse(eventString);
                            if (eventUri != null)
                                getActivity().getContentResolver().delete(eventUri, null, null);
                        }
                        Snackbar snackbar = Snackbar.make(getView(), "תור נמחק", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                        loadUserBooking();

                        //Check if isChange -> call from change button, we will fired interface
                        if (isChange)
                            iBookingInformationChangeListener.onBookingInformationChange();

                        dialog.dismiss();
                    });

                }
            }
        });
    }
        else
        {
            dialog.dismiss();
            Snackbar snackbar = Snackbar.make(getView(), "תור לא נמחק", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @OnClick(R.id.card_view_cart)
    void openCartActivity(){
        startActivity(new Intent(getActivity(), CartActivity.class));
    }

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef = FirebaseFirestore.getInstance().collection("Lookbook");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Get Current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //Select booking information from Firebase with done=false and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1) //only take 1
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());

                                //Exit loop as soon as
                                break;
                            }
                        } else
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                    }
                }).addOnFailureListener(e ->
                iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage()

                ));

        //Here, after userBooking has been assign data (collections)
        //we will make realtime listen here
        //if userBookingEvent already init
        if (userBookingEvent != null)
        {
            if (userBookingListener == null) //only add if userBookingListener ==null
            {
                //That mean we just add 1 time
                userBookingListener = userBooking
                        .addSnapshotListener(userBookingEvent);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,view);


        navigationView = (NavigationView) view.findViewById(R.id.navigationView);

        firebaseFirestore = FirebaseFirestore.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        initView();

        init();

        return view;
    }

    private void init() {
        CartDatabase.getInstance(getContext());

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        // Init
        Slider.init(new PicassoImageLoadingService());
        iLookbookLoadListener = this;
        iBannerLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            loadBanner();
            loadLookBook();
         //   setUserInformation();
            loadUserBooking();
            initRealtimeUserBooking();  //Need declare above loadUserbooking
            countCartItem();
        }
    }

    private void initRealtimeUserBooking() {
        if(userBookingEvent == null) //we only init event is null
        {
            userBookingEvent = new EventListener<QuerySnapshot>() {

                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    //In this event, when it fired, we will call loadUserBooking again
                    //to reload all booking information
                    loadUserBooking();
                }
            };
        }
    }

    private void countCartItem() {
        cartDataSource.countItemInCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                         notificationBadge.setText(String.valueOf( integer ));
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });
    }

    private void loadBanner(){
        bannerRef.get()
                .addOnCompleteListener(task -> {
                    List<Banner> banners = new ArrayList<>();
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot bannerSnapShot:task.getResult())
                        {
                            Banner banner = bannerSnapShot.toObject(Banner.class);
                            banners.add(banner);
                        }
                        iBannerLoadListener.onBannerLoadSuccess(banners);
                    }
                }).addOnFailureListener(e -> iBannerLoadListener.onBannerLoadFailed(e.getMessage()));
    }

    private void loadLookBook() {
        lookbookRef.get()
                .addOnCompleteListener(task -> {
                    List<Banner> lookbooks = new ArrayList<>();
                    if (task.isSuccessful())
                    {
                        for (QueryDocumentSnapshot bannerSnapShot:task.getResult())
                        {
                            Banner banner = bannerSnapShot.toObject(Banner.class);
                            lookbooks.add(banner);
                        }
                        iLookbookLoadListener.onLookbookSuccess(lookbooks);
                    }
                }).addOnFailureListener(e ->
                iLookbookLoadListener.onLookbookFailed(e.getMessage()));
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLookbookSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_look_book.setAdapter(new LookbookAdapter(getContext(), banners));
    }

    @Override
    public void onLookbookFailed(String message) {
        Toast.makeText(HomeFragment.this.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent( MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event)
    {
        countCartItem();
    }

    private void initView() {
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.navigationView);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.END))
                    drawerLayout.closeDrawer(GravityCompat.END);

                else drawerLayout.openDrawer(GravityCompat.END);

                loadUser();
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                //Scale the based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                //Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);

            }

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id==R.id.nav_orders){
            Intent intent = new Intent(getActivity(), OrdersActivity.class);
            startActivity(intent);

        }
        else if (id==R.id.nav_news){
            showSubscribeNews();
        }
        else if (id==R.id.nav_may_account){
            Intent intent = new Intent(getActivity(), InvoiceActivity.class);
            startActivity(intent);
        }
        else if (id==R.id.nav_return_policy)
        {
            Intent intent = new Intent(getActivity(), ReturnPolicy.class);
            startActivity(intent);
        }

        else if (id==R.id.nav_contact)
        {
            Intent intent = new Intent(getContext(), ContactActivity.class);
            startActivity(intent);
        }

        else if (id==R.id.nav_fav)
        {
            Intent intent = new Intent(getContext(), FavoriteActivity.class);
            startActivity(intent);
        }

        else if (id==R.id.logOut)
        {
            Logout();
        }

        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private void Logout() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("יציאה?");
        builder.setMessage("האם את רוצה להתנתק?");

        builder.setNegativeButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Common.currentBarber = null;
                Common.currentBooking = null;
                Common.currentSalon = null;
                Common.currentTimeSlot = -1;
                Common.currentBookingId = "";
                Common.currentUser = null;
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        builder.setPositiveButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSubscribeNews() {
        Paper.init(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("News System");
        builder.setMessage("האם את מעוניינת לקבל התראות לגבי מבצעים?");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_news, null);

        CheckBox check_news = (CheckBox) itemView.findViewById(R.id.check_news);
        boolean isSuccess = Paper.book().read(Common.IS_NEWS, false);

        if (isSuccess)
            check_news.setChecked(true);
        builder.setNegativeButton("ביטול", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        }).setPositiveButton("שליחה", (dialogInterface, i) -> {
            if (check_news.isChecked())
            {
                Paper.book().write(Common.IS_NEWS, true);
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(Common.NEWS_TOPIC)
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(getView(), "המנוי שלך נרשם בהצלחה", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        });

            }
            else
            {
                Paper.book().delete(Common.IS_NEWS);
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(Common.NEWS_TOPIC)
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(getView(), "הרישום בוטל", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        });
            }
        });

        builder.setView(itemView);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void loadUser() {
        txt_user_name = (TextView) navigationView.findViewById(R.id.txt_user_name);
        txt_user_phone = (TextView) navigationView.findViewById(R.id.txt_user_phone);

        logo_user = (ImageView) navigationView.findViewById(R.id.logo_user);

        txt_user_phone.setText(Common.currentUser.getPhoneNumber().replaceAll("[+]972", "0"));
        txt_user_name.setText(Common.currentUser.getFirstName() + " " +Common.currentUser.getLastName());

        if(Common.currentUser != null && Common.currentUser.getImage() !=null &&
                !TextUtils.isEmpty(Common.currentUser.getImage()))
        {
            Picasso.get().load(Common.currentUser.getImage()).into(logo_user);
        }

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        logo_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    void takeImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .start(getActivity(), this);
    }

    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestCameraPermission() {
        requestPermissions(cameraPermission, PICK_CAMERA_REQUEST);
    }

    public boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {
        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getBookingTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();

        txt_time_remain.setText(dateRemain);

        txt_order_status.setText(new StringBuilder(" ").append(Common.convertBookingStatusToText(bookingInformation.getBookingStatus())));

        card_booking_info.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        //Here we will just start activity Booking
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("שינוי תמונת פרופיל")
              //  .setMessage("היי את עומדת לשנות את תמונת הפרופיל שלך")
                .setNegativeButton("ביטול", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("עדכון", (dialogInterface, i) -> {

                    showDialogUpdate();

                }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.grey));

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        });

        dialog.show();
    }

    private void showDialogUpdate(){
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_user_image, null);

        Button btnUpdate = (Button)sheetView.findViewById(R.id.btnUpdate);
        Button btnCancel = (Button)sheetView.findViewById(R.id.btnCancel);
        ImageView img_folder = (ImageView)sheetView.findViewById(R.id.img_folder);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        img_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int picd=0;
                if (picd == 0){
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        takeImage();
                    }
                }else if (picd==1){
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }else {
                        takeImage();
                    }
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imgUri != null)
                {
                    //we will firestore to upload image
                    dialog.setMessage("מעדכן...");
                    dialog.show();

                   // String unique_name = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String unique_name = Common.currentUser.getFirstName() + " " + Common.currentUser.getLastName();
                    StorageReference imageFolder = storageReference.child("UserProfile/" + unique_name);

                    imageFolder.putFile(imgUri)
                            .addOnFailureListener(e -> {
                                dialog.dismiss();
                                Snackbar.make(drawerLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }).addOnCompleteListener(task -> {

                        if (task.isSuccessful())
                        {
                            dialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                Map<String, Object> updateDate = new HashMap<>();

                                updateDate.put("image", uri.toString());

                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(Common.currentUser.getPhoneNumber())
                                        .update(updateDate);

                            });
                        }
                        dialog.dismiss();
                    }).addOnProgressListener(taskSnapshot -> {
                        double progress = Math.round(100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                        dialog.setMessage(new StringBuilder("מעדכן: ").append(progress).append("%"));

                        if (taskSnapshot.getTask().isSuccessful()){


                            Toast mToast = new Toast(getContext());
                            mToast.setDuration(Toast.LENGTH_LONG);
                            View toastView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity) getContext()).findViewById(R.id.root_layout));
                            TextView textView = toastView.findViewById(R.id.txt_message);
                            mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                            textView.setText("תמונת פרופיל עודכנה בהצלחה");
                            mToast.setView(toastView);
                            mToast.show();

                            Intent intent = getActivity().getIntent();
                            getActivity().finish();
                            startActivity(intent);

                        }
                    });
                }
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
        bottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                imgUri = result.getUri();
               // Glide.with(HomeFragment.this).load(imgUri).into(logo_user);
                logo_user.setImageURI(imgUri);
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PICK_CAMERA_REQUEST:{
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    boolean storage_accepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED);
                    if (camera_accepted && storage_accepted){
                        takeImage();
                    }else if (Common.currentUser != null && Common.currentUser.getImage() !=null &&
                            !TextUtils.isEmpty(Common.currentUser.getImage()) || Common.currentUser.getImage() == null){
                        takeImage();
                    }
                }
            }
            break;

            case STORAGE_REQUEST:{
                if (grantResults.length > 0) {
                    boolean storage_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    if (storage_accepted){
                        takeImage();
                    } else if (Common.currentUser != null && Common.currentUser.getImage() !=null &&
                            !TextUtils.isEmpty(Common.currentUser.getImage()) || Common.currentUser.getImage() == null){
                        takeImage();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onDestroy() {
        if(userBookingListener != null)
            userBookingListener.remove();

        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
            bottomSheetDialog = null;
        }
        super.onDestroy();
    }

}