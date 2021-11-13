package com.example.zgbeautyandhair.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Database.CartItem;
import com.example.zgbeautyandhair.Model.Order;
import com.example.zgbeautyandhair.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {
    private Context context;
    private List<Order> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public MyOrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_orders_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //load date pick
        calendar.setTimeInMillis(orderList.get(position).getCreateDate());
        Date date = new Date(orderList.get(position).getCreateDate());

        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        holder.txt_order_time.setText(sfd.format(new Date(orderList.get(position).getTimestamp().toDate().toString())));

        holder.txt_order_price.setText(orderList.get(position).getTotalPayment());

        holder.txt_order_number.setText(orderList.get(position).getOrderNumber());
        holder.txt_order_comment.setText(orderList.get(position).getComment());
        holder.txt_order_status.setText(Common.convertStatusToText(orderList.get(position).getOrderStatus()));


        holder.btn_details.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(orderList.get(position).getCartItemList());
            }
        } );

    }

    private void showDialog(List<CartItem> cartItemList) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_order_detail, null);
        //  AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Dialog builder = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        builder.setContentView(layout_dialog);

        Button btn_ok = (Button) layout_dialog.findViewById(R.id.btn_ok);
        RecyclerView recycler_order_detail = (RecyclerView) layout_dialog.findViewById(R.id.recycler_order_detail);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);

        MyOrderDetailAdapter myOrderDetailAdapter = new MyOrderDetailAdapter(context, cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);


        //Show dialog
        builder.create();
        builder.show();

        btn_ok.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;

        @BindView(R.id.txt_order_comment)
        TextView txt_order_comment;

        @BindView(R.id.txt_order_number)
        TextView txt_order_number;

        @BindView(R.id.txt_order_time)
        TextView txt_order_time;

        @BindView(R.id.txt_order_price)
        TextView txt_order_price;

        @BindView(R.id.btn_details)
        Button btn_details;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);

        }
    }
}
