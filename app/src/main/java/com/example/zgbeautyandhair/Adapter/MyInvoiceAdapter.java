package com.example.zgbeautyandhair.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MyInvoiceAdapter extends RecyclerView.Adapter<MyInvoiceAdapter.MyViewHolder> {
    private Context context;
    private List<Order> orderList;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    public MyInvoiceAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.layout_invoice, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_invoice_number.setText(new StringBuilder(" ")
                .append(orderList.get(position).getOrderNumber()));

        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        holder.txt_invoice_time.setText(sfd.format(new Date(orderList.get(position).getTimestamp().toDate().toString())));

        holder.txt_order_price.setText(new StringBuilder(" ").append(orderList.get(position).getTotalPayment()));

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(orderList.get(position).getCartItemList(), orderList.get(position));
            }
        } );
    }

    private void showDialog(List<CartItem> cartItemList, Order order) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_invoice_detail, null);
        Dialog builder = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        builder.setContentView(layout_dialog);

        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm dd-MM-yyyy");

        //invoice details
        TextView txt_user_name = (TextView) layout_dialog.findViewById(R.id.txt_user_name);
        TextView txt_salon_name = (TextView) layout_dialog.findViewById(R.id.txt_salon_name);
        TextView txt_salon_address = (TextView) layout_dialog.findViewById(R.id.txt_salon_address);
        // TextView txt_user_address = (TextView) layout_dialog.findViewById(R.id.txt_user_address);
        //TextView txt_shipping_user_name = (TextView) layout_dialog.findViewById(R.id.txt_shipping_user_name);
        TextView txt_shipping_address = (TextView) layout_dialog.findViewById(R.id.txt_shipping_address);
        TextView text_order_date = (TextView) layout_dialog.findViewById(R.id.text_order_date);
        TextView txt_order_number = (TextView) layout_dialog.findViewById(R.id.txt_order_number);
        TextView txt_total_price = (TextView) layout_dialog.findViewById(R.id.txt_total_price);

        ImageView back_pressed = (ImageView) layout_dialog.findViewById(R.id.back_pressed);
        RecyclerView recycler_order_detail = (RecyclerView) layout_dialog.findViewById(R.id.recycler_invoice_item);
        recycler_order_detail.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_order_detail.setLayoutManager(layoutManager);

        MyInvoiceDetailsAdapter myOrderDetailAdapter = new MyInvoiceDetailsAdapter(context, cartItemList);
        recycler_order_detail.setAdapter(myOrderDetailAdapter);


        txt_salon_address.setText(order.getSalonAddress());
        txt_salon_name.setText(order.getSalonName());
        txt_user_name.setText(order.getUserName());
        txt_shipping_address.setText(order.getShippingAddress());
        text_order_date.setText(sfd.format(new Date(order.getTimestamp().toDate().toString())));
        txt_order_number.setText(order.getOrderNumber());

        txt_total_price.setText(order.getTotalPayment());

        //Show dialog
        builder.create();
        builder.show();

        back_pressed.setOnClickListener( v -> builder.dismiss() );
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.txt_invoice_time)
        TextView txt_invoice_time;

        @BindView(R.id.txt_invoice_number)
        TextView txt_invoice_number;

        @BindView(R.id.txt_order_price)
        TextView txt_order_price;

        Unbinder unbinder;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );

            ButterKnife.bind(this, itemView);

            txt_invoice_time = itemView.findViewById(R.id.txt_invoice_time);
            txt_invoice_number = itemView.findViewById(R.id.txt_invoice_number);

        }
    }
}
