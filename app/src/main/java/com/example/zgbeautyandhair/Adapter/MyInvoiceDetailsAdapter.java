package com.example.zgbeautyandhair.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Database.CartItem;

import com.example.zgbeautyandhair.R;

import java.util.List;

import butterknife.ButterKnife;

public class MyInvoiceDetailsAdapter extends RecyclerView.Adapter<MyInvoiceDetailsAdapter.MyViewHolder> {
    Context context;
    List<CartItem> cartItemList;

    public MyInvoiceDetailsAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.layout_invoice_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.txt_item_name.setText(new StringBuilder(cartItemList.get(position).getProductName()));
        holder.txt_item_price.setText(new StringBuilder("₪").append(cartItemList.get(position).getProductPrice()));
        holder.txt_item_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(position).getProductQuantity())));


        if (cartItemList.get(position).getProductLength() == null)
        {
            holder.txt_cart_length.findViewById(R.id.txt_cart_length).setVisibility(View.GONE);
           // holder.cart_length.findViewById(R.id.cart_length).setVisibility(View.GONE);


        } else if (cartItemList.get(position).getProductLength() != null){
            holder.txt_cart_length.setText(cartItemList.get(position).getProductLength());
        }

    }

    @Override
    public int getItemCount() {
        return cartItemList == null ? 0 :cartItemList.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_item_name;

        TextView txt_item_price;

        TextView txt_item_quantity;

        TextView txt_cart_length;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );

            ButterKnife.bind(this, itemView);

            txt_cart_length = itemView.findViewById(R.id.txt_cart_length);
            txt_item_name = itemView.findViewById( R.id.txt_item_name);
            txt_item_price = itemView.findViewById(R.id.txt_item_price);
            txt_item_quantity = itemView.findViewById(R.id.txt_item_quantity);

        }
    }
}
