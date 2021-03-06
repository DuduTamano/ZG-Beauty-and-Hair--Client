package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;

import com.example.zgbeautyandhair.Adapter.MyViewPagerAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Model.Barber;
import com.example.zgbeautyandhair.Model.EventBus.BarberDoneEvent;
import com.example.zgbeautyandhair.Model.EventBus.ConfirmBookingEvent;
import com.example.zgbeautyandhair.Model.EventBus.DisplayTimeSlotEvent;
import com.example.zgbeautyandhair.Model.EventBus.EnableNextButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.shuhart.stepview.StepView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {
    AlertDialog dialog;
    CollectionReference barberRef;

    @BindView (R.id.step_view)
    StepView step_view;
    @BindView (R.id.view_pager)
    ViewPager viewPager;
    @BindView (R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView (R.id.btn_next_step)
    Button btn_next_step;

    //Event
    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if (Common.step == 3 || Common.step > 0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if (Common.step < 3)
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick(){
        if (Common.step < 3 || Common.step == 0)
        {
            Common.step++;
            if (Common.step == 1)
            {
                if (Common.currentSalon != null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());
            }
            else if (Common.step == 2)
            {
                if (Common.currentBarber != null)
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());

            }
            else if (Common.step == 3)
            {
                if (Common.currentTimeSlot != -1)
                    confirmBooking();
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {

        EventBus.getDefault().postSticky(new ConfirmBookingEvent(true));
    }

    private void loadTimeSlotOfBarber(String barberId) {

        EventBus.getDefault().postSticky(new DisplayTimeSlotEvent(true));
    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();

        if (!TextUtils.isEmpty(Common.city))
        {
            barberRef = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.city)
                    .collection("Branch")
                    .document(salonId)
                    .collection("Barbers");

            barberRef.get()
                    .addOnCompleteListener(task -> {
                        ArrayList<Barber> barbers = new ArrayList<>();
                        for (QueryDocumentSnapshot barberSnapShot: Objects.requireNonNull (task.getResult ()))
                        {
                            Barber barber = barberSnapShot.toObject(Barber.class);
                            barber.setPassword("");
                            barber.setBarberId(barberSnapShot.getId());

                            barbers.add(barber);
                        }

                        EventBus.getDefault()
                                .postSticky(new BarberDoneEvent(barbers));

                        dialog.dismiss();
                    }).addOnFailureListener(e ->
                    dialog.dismiss());
        }
    }

    //EventBus Convert  ==================================
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void buttonNextReceiver(EnableNextButton event) {
        int step = event.getStep();
        if (step == 1)
            Common.currentSalon = event.getSalon();
        else if (step == 2)
            Common.currentBarber = event.getBarber();
        else if (step == 3)
            Common.currentTimeSlot = event.getTimeSlot();

        btn_next_step.setEnabled(true);
        setColorButton();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        setupStepView();
        setColorButton();

        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        //we have 4 fragments
        viewPager.setOffscreenPageLimit(4);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                step_view.go(i,true);
                if (i == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setColorButton() {
        if (btn_next_step.isEnabled())
        {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if (btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("??????????");
        stepList.add("??????????");
        stepList.add("?????????? ????????");
        stepList.add("??????????");
        step_view.setSteps(stepList);
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
}