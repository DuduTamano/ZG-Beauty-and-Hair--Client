package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {

        void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
        void onTimeSlotLoadFailed(String message);
        void onTimeSlotLoadEmpty();
}