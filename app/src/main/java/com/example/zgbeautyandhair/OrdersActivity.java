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

import com.example.zgbeautyandhair.Adapter.MyOrdersAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Interface.ILoadOrderCallbackListener;
import com.example.zgbeautyandhair.Model.Order;
import com.example.zgbeautyandhair.ViewModel.ViewOrders;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class OrdersActivity extends AppCompatActivity implements ILoadOrderCallbackListener {

    @BindView(R.id.recycler_view_orders)
    RecyclerView recycler_view_orders;

    @BindView(R.id.txt_empty)
    TextView txt_empty;

    private Unbinder unbinder;

    private ViewOrders viewOrders;

    AlertDialog dialog;

    private ILoadOrderCallbackListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        viewOrders = new ViewModelProvider(this).get(ViewOrders.class);

        unbinder = ButterKnife.bind(this);

        txt_empty = findViewById(R.id.txt_empty);

        initView();
        
        loadOrdersFromFirebase();

        viewOrders.getMutableLiveDataOrderList().observe(this, orderList -> {
            MyOrdersAdapter adapter = new MyOrdersAdapter(this, orderList);
            recycler_view_orders.setAdapter(adapter);

            if (adapter.getItemCount() == 0)
            {
                txt_empty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadOrdersFromFirebase() {
        List<Order> orderList = new ArrayList<>();
        CollectionReference ordersRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Orders");

                ordersRef.get()
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
                        listener.onLoadOrderSuccess(orderList);
                    }
                } ).addOnFailureListener( e ->
                        listener.onLoadOrderFailed(e.getMessage()) );
    }

    private void initView() {
        listener = this;

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        recycler_view_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view_orders.setLayoutManager(layoutManager);
    }

    @Override
    public void onLoadOrderSuccess(List<Order> orderList) {
        dialog.dismiss();
        viewOrders.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void BackToOrders(View view) {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }
}