package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.MyInvoiceAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Interface.ILoadInvoiceCallbackListener;
import com.example.zgbeautyandhair.Model.Order;
import com.example.zgbeautyandhair.ViewModel.ViewInvoices;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class InvoiceActivity extends AppCompatActivity implements ILoadInvoiceCallbackListener {
    @BindView(R.id.recycler_view_invoice)
    RecyclerView recycler_view_invoice;

    @BindView(R.id.txt_empty)
    TextView txt_empty;

    private Unbinder unbinder;

    private ViewInvoices viewInvoices;

    AlertDialog dialog;

    private ILoadInvoiceCallbackListener listener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orderRef = db.collection("Users")
            .document(Common.currentUser.getPhoneNumber()).collection("Orders");

    MyInvoiceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        viewInvoices = new ViewModelProvider(this).get(ViewInvoices.class);

        unbinder = ButterKnife.bind(this);

        txt_empty = findViewById(R.id.txt_empty);

        loadInvoiceFromFirebase();

        initView();

        viewInvoices.getMutableLiveDataInvoiceList().observe(this, orderList -> {
            MyInvoiceAdapter adapter = new MyInvoiceAdapter(this, orderList);
            recycler_view_invoice.setAdapter(adapter);

            if (adapter.getItemCount() == 0)
            {
                txt_empty.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loadInvoiceFromFirebase() {
        List<Order> orderList = new ArrayList<>();

        CollectionReference invoiceRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Orders");

        invoiceRef.get()
                .addOnCompleteListener( task -> {

                    if (task.isSuccessful())
                    {
                        for (DocumentSnapshot orderSnapshot : task.getResult())
                        {
                            Order order = orderSnapshot.toObject(Order.class);
                            order.setOrderId(order.getOrderId());
                            orderList.add(order);

                            Common.currentOrder = order;
                        }
                        listener.onLoadInvoiceSuccess(orderList);
                    }
                } ).addOnFailureListener( e ->
                listener.onLoadInvoiceFailed(e.getMessage()));
    }

    private void initView() {
        listener = this;

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        recycler_view_invoice.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view_invoice.setLayoutManager(layoutManager);
        recycler_view_invoice.setAdapter(adapter);
    }

    @Override
    public void onLoadInvoiceSuccess(List<Order> invoiceList) {
        dialog.dismiss();
        viewInvoices.setMutableLiveDataInvoiceList(invoiceList);
    }

    @Override
    public void onLoadInvoiceFailed(String message) {
        dialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void BackToHome(View view) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }

}