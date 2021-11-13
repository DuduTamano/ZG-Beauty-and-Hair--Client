package com.example.zgbeautyandhair.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.CartItem;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;
import com.example.zgbeautyandhair.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder>{
    Context context;
    List<CartItem> cartItemList;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable;


    public void onDestroy() {
        compositeDisposable.clear();
    }

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        this.compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item, viewGroup, false
        );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(cartItemList.get(i).getProductImage()).into(myViewHolder.img_product);
        myViewHolder.txt_cart_name.setText(new StringBuilder(cartItemList.get(i).getProductName()));
        myViewHolder.txt_cart_price.setText(new StringBuilder("₪").append(cartItemList.get(i).getProductPrice()));
        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

        if (cartItemList.get(i).getProductLength() == null)
        {
            myViewHolder.txt_cart_length.findViewById(R.id.txt_cart_length).setVisibility(View.GONE);
            myViewHolder.cart_length.findViewById(R.id.cart_length).setVisibility(View.GONE);


        } else if (cartItemList.get(i).getProductLength() != null){
            myViewHolder.txt_cart_length.setText(cartItemList.get(i).getProductLength());

        }

        //Event
        myViewHolder.img_minus.setOnClickListener(v -> {
            minusCartItem(myViewHolder, cartItemList.get(i));
        });

        myViewHolder.img_plus.setOnClickListener(v -> {
            plusCartItem(myViewHolder, cartItemList.get(i));
        });

        myViewHolder.btnDelete.setOnClickListener(v -> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("מחיקה")
                    .setMessage("האם את רוצה למחוק את המוצר?")
                    .setNegativeButton("ביטול", (dialog1, which) ->
                            dialog1.dismiss())
                    .setPositiveButton("מאשרת", (dialog12, which) -> {
                        deleteItems(cartItemList.get(i));

                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });

    }

    private void plusCartItem(MyViewHolder myViewHolder, CartItem cartItem) {
        cartItem.setProductQuantity(cartItem.getProductQuantity() +1);
        cartItem.setProductTotalPrice(cartItem.getProductQuantity() * Float.parseFloat(String.valueOf(cartItem.getProductPrice())));

        updateItems(myViewHolder, cartItem);
    }

    private void minusCartItem(MyViewHolder myViewHolder, CartItem cartItem) {
        if (cartItem.getProductQuantity() > 1)
        {
            cartItem.setProductQuantity(cartItem.getProductQuantity() - 1);
            cartItem.setProductTotalPrice(cartItem.getProductQuantity() * Float.parseFloat(String.valueOf(cartItem.getProductPrice())));

            updateItems(myViewHolder, cartItem);
        }
        else if (cartItem.getProductQuantity() == 1)
        {
            deleteItems(cartItem);
        }
    }

    private void deleteItems(CartItem cartItem) {
        cartDataSource.delete(cartItem)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        cartItemList.remove(integer);
                        notifyItemRemoved(integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText( context, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                });
    }

    private void updateItems(MyViewHolder myViewHolder, CartItem cartItem) {
        cartDataSource.update(cartItem)
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        //When upload success, just set quantity
                        myViewHolder.txt_quantity.setText(
                                new StringBuilder().append(cartItem.getProductQuantity()));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText( context, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    interface IImageButtonListener{
        void onImageButtonClick(View view, int pos, boolean isDecrease);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_cart_name, txt_cart_price, txt_quantity, txt_cart_length, cart_length;
        ImageView img_minus, img_plus, img_product, btnDelete;

        IImageButtonListener listener;

        public void setListener(IImageButtonListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_length = itemView.findViewById(R.id.cart_length);
            txt_cart_length = itemView.findViewById(R.id.txt_cart_length);
            txt_cart_name = itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price = itemView.findViewById(R.id.txt_cart_price);
            txt_quantity = itemView.findViewById(R.id.txt_cart_quantity);

            img_minus = itemView.findViewById(R.id.img_minus);
            img_plus = itemView.findViewById(R.id.img_plus);
            img_product = itemView.findViewById(R.id.cart_img);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            img_minus.setOnClickListener(view ->
                    listener.onImageButtonClick(view, getAdapterPosition(), true));

            img_plus.setOnClickListener(view ->
                    listener.onImageButtonClick(view, getAdapterPosition(), false));
        }
    }
}
