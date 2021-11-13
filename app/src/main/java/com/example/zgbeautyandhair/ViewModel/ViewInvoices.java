package com.example.zgbeautyandhair.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zgbeautyandhair.Model.Order;

import java.util.List;

public class ViewInvoices extends ViewModel {
    private MutableLiveData<List<Order>> mutableLiveDataInvoiceList;

    public ViewInvoices() {
        mutableLiveDataInvoiceList = new MutableLiveData<>();
    }

    public MutableLiveData<List<Order>> getMutableLiveDataInvoiceList() {
        return mutableLiveDataInvoiceList;
    }

    public void setMutableLiveDataInvoiceList(List<Order> invoiceList) {
        mutableLiveDataInvoiceList.setValue(invoiceList);
    }
}
