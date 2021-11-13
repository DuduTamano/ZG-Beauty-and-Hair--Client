package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Order;

public interface ILoadTimeFromFirebaseListener {
    void onLoadTimeSuccess(Order order, long estimateTimeInMs);
    void onLoadTimeFailed(String message);
}
