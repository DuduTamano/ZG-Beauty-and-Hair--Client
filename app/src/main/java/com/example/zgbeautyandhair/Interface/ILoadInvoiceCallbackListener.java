package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Order;

import java.util.List;

public interface ILoadInvoiceCallbackListener {
    void onLoadInvoiceSuccess(List<Order> invoiceList);
    void onLoadInvoiceFailed(String message);

}
