package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Order;

import java.util.List;

public interface ILoadOrderCallbackListener {
    void onLoadOrderSuccess(List<Order> orderList);
    void onLoadOrderFailed(String message);
}
