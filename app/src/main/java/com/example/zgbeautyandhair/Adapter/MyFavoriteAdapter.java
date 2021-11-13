package com.example.zgbeautyandhair.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Model.FavoriteItem;
import com.example.zgbeautyandhair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.ButterKnife;

public class MyFavoriteAdapter extends RecyclerView.Adapter<MyFavoriteAdapter.FavoriteViewHolder> {

    Context context;
    List<FavoriteItem> cartItemList;
    CardView favorite_layout;

    public MyFavoriteAdapter(Context context, List<FavoriteItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.favorite_layout = favorite_layout;
    }

    @NotNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate( R.layout.layout_fav_item, parent, false);

        favorite_layout = itemView.findViewById(R.id.favorite_layout);

        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FavoriteViewHolder myViewHolder, int position) {

        Picasso.get().load(cartItemList.get(position).getProductImage()).into(myViewHolder.img_shopping_item);

        myViewHolder.txt_name_shopping_item.setText(cartItemList.get(position).getProductName());

        myViewHolder.txt_price_shopping_item.setText(new StringBuilder("₪").append(cartItemList.get(position).getProductPrice()));

        myViewHolder.txt_quantity.setText(new StringBuilder("כמות המוצרים: ").append(cartItemList.get(position).getProductQuantity()));


        if (cartItemList.get(position).getProductLength() == null)
        {
            myViewHolder.txt_cart_length.findViewById(R.id.txt_cart_length).setVisibility(View.GONE);
            myViewHolder.cart_length.findViewById(R.id.cart_length).setVisibility(View.GONE);


        } else if (cartItemList.get(position).getProductLength() != null){
            myViewHolder.txt_cart_length.setText(cartItemList.get(position).getProductLength());

        }

        myViewHolder.btnDelete.setOnClickListener(v -> {
            deleteItems(position);

        });
    }

    private void deleteItems(int position) {
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Favorite")
                .get().addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot favoriteSnapshot = task.getResult().getDocuments().get(position);

                    String Id = favoriteSnapshot.getId();

                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(Common.currentUser.getPhoneNumber())
                            .collection("Favorite")
                            .document(Id)
                            .delete().addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast mToast = new Toast(context);
                                mToast.setDuration(Toast.LENGTH_LONG);
                                View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                                TextView textView = toastView.findViewById(R.id.txt_message);
                                mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                textView.setText("פריט הוסר");
                                mToast.setView(toastView);
                                mToast.show();

                                cartItemList.remove(cartItemList.get(position));
                                notifyDataSetChanged();

                            }
                        }
                    });

                }

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( context, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    public int getItemCount() {
        return cartItemList == null ? 0 :cartItemList.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name_shopping_item, txt_price_shopping_item, txt_quantity, txt_cart_length, cart_length;
        ImageView img_shopping_item, btnDelete;

        public FavoriteViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            txt_cart_length = itemView.findViewById(R.id.txt_cart_length);
            cart_length = itemView.findViewById(R.id.cart_length);
            img_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            txt_name_shopping_item = itemView.findViewById(R.id.txt_name_shopping_item);
            txt_price_shopping_item = itemView.findViewById(R.id.txt_price_shopping_item);
            txt_quantity = itemView.findViewById(R.id.txt_quantity);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
