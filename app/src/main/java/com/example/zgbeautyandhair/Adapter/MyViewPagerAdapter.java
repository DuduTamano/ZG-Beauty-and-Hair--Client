package com.example.zgbeautyandhair.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.zgbeautyandhair.Fragments.BookingBarberFragment;
import com.example.zgbeautyandhair.Fragments.BookingConfirmFragment;
import com.example.zgbeautyandhair.Fragments.BookingSalonFragment;
import com.example.zgbeautyandhair.Fragments.BookingTimeSlotFragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                return BookingSalonFragment.getInstance();
            case 1:
                return BookingBarberFragment.getInstance();
            case 2:
                return BookingTimeSlotFragment.getInstance();
            case 3:
                return BookingConfirmFragment.getInstance();

        }


        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
