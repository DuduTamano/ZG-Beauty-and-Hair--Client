package com.example.zgbeautyandhair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.MyBookingHistoryAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Model.BookingInformation;
import com.example.zgbeautyandhair.Model.EventBus.UserBookingLoadEvent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class BookingHistoryActivity extends AppCompatActivity {

    @BindView(R.id.recycler_history)
    RecyclerView recycler_history;

    @BindView(R.id.txt_history)
    TextView txt_history;

    AlertDialog dialog;

    @BindView(R.id.txt_empty)
    TextView txt_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history );

        ButterKnife.bind(this);

        txt_empty = findViewById(R.id.txt_empty);

        init();
        initView();

        loadUserBookingInformation();

    }

    private void loadUserBookingInformation() {
        dialog.show();

        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        userBooking.whereEqualTo("done", true)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().post(new UserBookingLoadEvent(false,e.getMessage()));
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    List<BookingInformation> bookingInformationList = new ArrayList<>();
                    for(DocumentSnapshot userBookingSnapshot:task.getResult())
                    {
                        BookingInformation bookingInformation = userBookingSnapshot.toObject(BookingInformation.class);
                        bookingInformationList.add(bookingInformation);
                    }
                    //use EventBus to send
                    EventBus.getDefault().post(new UserBookingLoadEvent(true, bookingInformationList));
                }
            }
        });
    }

    private void initView() {
        recycler_history.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_history.setLayoutManager(layoutManager);
    }

    private void init() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayDate(UserBookingLoadEvent event)
    {
        if(event.isSuccess())
        {
            MyBookingHistoryAdapter adapter = new MyBookingHistoryAdapter(this, event.getBookingInformationList());
            recycler_history.setAdapter(adapter);

            if (adapter.getItemCount() == 0)
            {
                txt_empty.setVisibility(View.VISIBLE);
            }
        }
        else
        {
            Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
    }

    public void BackToHome(View view) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }
}