package com.example.zgbeautyandhair.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zgbeautyandhair.Model.Order;

import java.util.List;

public class ViewOrders extends ViewModel {
    private MutableLiveData<List<Order>> mutableLiveDataOrderList;

    public ViewOrders () {
        mutableLiveDataOrderList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Order>> getMutableLiveDataOrderList() {
        return mutableLiveDataOrderList;
    }

    public void setMutableLiveDataOrderList(List<Order> orderList) {
        mutableLiveDataOrderList.setValue(orderList);
    }
}
