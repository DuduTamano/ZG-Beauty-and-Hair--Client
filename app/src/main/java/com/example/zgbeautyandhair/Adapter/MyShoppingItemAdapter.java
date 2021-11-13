package com.example.zgbeautyandhair.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Database.CartDataSource;
import com.example.zgbeautyandhair.Database.CartDatabase;
import com.example.zgbeautyandhair.Database.CartItem;
import com.example.zgbeautyandhair.Database.LocalCartDataSource;
import com.example.zgbeautyandhair.Interface.IRecyclerItemSelectedListener;
import com.example.zgbeautyandhair.Model.FavoriteItem;
import com.example.zgbeautyandhair.Model.ProductItem;
import com.example.zgbeautyandhair.Model.ShoppingItem;
import com.example.zgbeautyandhair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class MyShoppingItemAdapter extends RecyclerView.Adapter<MyShoppingItemAdapter.MyViewHolder> {
    Context context;
    List<ShoppingItem> shoppingItemList;
    CompositeDisposable compositeDisposable;
    CartDataSource cartDataSource;

    String[] itemList;
    ArrayAdapter<String> adapter;
    FirebaseFirestore firebaseFirestore;
    ProductItem productItem = new ProductItem();
    CardView card_shopping;

    public void onDestroy() {
        compositeDisposable.clear();
    }

    public MyShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
        compositeDisposable = new CompositeDisposable();
        this.cartDataSource = new LocalCartDataSource( CartDatabase.getInstance(context).cartDAO());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_shopping_item,viewGroup, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(shoppingItemList.get(i).getImage()).into(myViewHolder.img_shopping_item);
        myViewHolder.txt_item_shopping_name.setText(Common.formatItemShoppingName(shoppingItemList.get(i).getName()));
        myViewHolder.txt_price_shopping_item.setText(new StringBuilder("₪ ").append(shoppingItemList.get(i).getPrice()));


        myViewHolder.itemView.setOnClickListener( v -> {

            showDialog(shoppingItemList.get(i), i);
        } );
    }

    private void showDialog(ShoppingItem shoppingItem, int position) {
        View layout_dialog = LayoutInflater.from(context).inflate(R.layout.layout_dialog_product_details, null);
        Dialog builder = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        builder.setContentView(layout_dialog);

        //product details
        HorizontalScrollView group_view = (HorizontalScrollView) layout_dialog.findViewById(R.id.group_view);
        Spinner spinner = (Spinner) layout_dialog.findViewById(R.id.spinner);
        LinearLayout spinnerLinear = (LinearLayout) layout_dialog.findViewById(R.id.spinnerLinear);
        Button txt_add_to_cart = (Button) layout_dialog.findViewById(R.id.txt_add_to_cart);
        Button txt_add_to_cart2 = (Button) layout_dialog.findViewById(R.id.txt_add_to_cart2);
        TextView txt_name_shopping_item = (TextView) layout_dialog.findViewById(R.id.txt_name_shopping_item);
        TextView txt_price_shopping_item = (TextView) layout_dialog.findViewById(R.id.txt_price_shopping_item);
        ImageView img_shopping_item = (ImageView) layout_dialog.findViewById(R.id.img_shopping_item);
        TextView description = (TextView) layout_dialog.findViewById(R.id.description);
        ImageView back_pressed = (ImageView) layout_dialog.findViewById(R.id.back_pressed);
        ImageView img_fav = (ImageView) layout_dialog.findViewById(R.id.img_fav);

        MotionLayout ddd = (MotionLayout) layout_dialog.findViewById(R.id.ddd);

        txt_name_shopping_item.setText(shoppingItem.getName());
        txt_price_shopping_item.setText(new StringBuilder("₪").append(shoppingItem.getPrice()));
        description.setText(shoppingItem.getDec());
        Glide.with(context).load(shoppingItem.getImage()).into(img_shopping_item);

        //Check if favorite selected
        FavoriteItem favorite = new FavoriteItem();

        SharedPreferences mSharedPref ;

        if (favorite.isFavStatus() ) {
            img_fav.setBackgroundResource(R.drawable.ic_favorite);
        } else {
            img_fav.setBackgroundResource(R.drawable.ic_favorite_border);
        }

        mSharedPref = context.getSharedPreferences("SharedPref", MODE_PRIVATE);
        Boolean isFavorite = mSharedPref.getBoolean("fav", true);

        loadId(txt_add_to_cart, txt_add_to_cart2, txt_price_shopping_item, spinner, spinnerLinear, shoppingItem, img_fav, mSharedPref, isFavorite, ddd, group_view);

        //Show dialog
        builder.create();
        builder.show();

        back_pressed.setOnClickListener( v -> builder.dismiss() );
    }


    private void loadId(Button txt_add_to_cart, Button txt_add_to_cart2, TextView txt_price_shopping_item, Spinner spinner, LinearLayout spinnerLinear, ShoppingItem shoppingItem, ImageView img_fav, SharedPreferences mSharedPref, Boolean isFavorite, MotionLayout ddd, HorizontalScrollView group_view) {
        FirebaseFirestore.getInstance().collection("Shopping")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String shoppingID = documentSnapshot.getId();

                        String[] HairProduct = {"ווקס", "BIOTOP","שמן אורגני", "סרום לשיער", " LA BEAUTE מסכת", "ספריי La Beaute", "SOVONCARE"};

                        String[] Hair = {"גלי סגור", "גלי פתוח", "קלוז'ר", "פרונטל 360"};

                        firebaseFirestore.collection("Shopping").document(shoppingID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {

                                    check(img_fav, true, shoppingItem, ddd);

                                    if (shoppingID.equals(documentSnapshot.getId())) {

                                        for (int i=0; i<= HairProduct.length-1; i++) {
                                            if (shoppingItem.getName().equals(HairProduct[i])) {

                                                CheckFavoriteHairProduct(isFavorite, shoppingItem, img_fav, mSharedPref, ddd);

                                                group_view.setVisibility(View.GONE);
                                                spinnerLinear.setVisibility(View.GONE);
                                                txt_price_shopping_item.setText(new StringBuilder("₪").append(shoppingItem.getPrice()));
                                                txt_add_to_cart.setVisibility(View.GONE);
                                                addToCart2(txt_add_to_cart2, shoppingItem);

                                                break;
                                            }
                                        }

                                        for (int i=0; i<= Hair.length-1; i++) {
                                            if (shoppingItem.getName().equals(Hair[i])) {

                                                spinnerLinear.setVisibility(View.VISIBLE);
                                                txt_add_to_cart2.setVisibility(View.GONE);
                                                checkHairLength(txt_price_shopping_item, spinner, txt_add_to_cart, shoppingItem, img_fav, isFavorite, mSharedPref);
                                                break;
                                            }
                                        }

                                    }
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText( context, "Error : " + e.toString(), Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                }
            }
        });
    }

    private void CheckFavoriteHairProduct(boolean isFavorite, ShoppingItem shoppingItem, ImageView img_fav, SharedPreferences mSharedPref, MotionLayout ddd) {
        img_fav.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFavorite) {
                    img_fav.setBackgroundResource(R.drawable.ic_favorite_border);

                } else {
                    //  AddFavorite(true, ddd, shoppingItem, timestamp);
                    AddFavoriteHairProduct(true, shoppingItem);
                    Toast.makeText(context, ""+shoppingItem.getName(), Toast.LENGTH_SHORT).show();
                    img_fav.setBackgroundResource(R.drawable.ic_favorite);
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    editor.putBoolean("fav", false);
                    editor.apply();
                }

            }
        });
    }

    private void checkHairLength(TextView txt_price_shopping_item, Spinner spinner, Button txt_add_to_cart, ShoppingItem shoppingItem, ImageView img_fav, Boolean isFavorite, SharedPreferences mSharedPref) {
        itemList = new String[] {"12", "14", "16", "18", "20", "22", "24"};

        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, itemList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0)
                {
                    productItem.setSize_price(200);
                }
                else if (position == 1)
                {
                    productItem.setSize_price(300);
                }
                else if (position == 2)
                {
                    productItem.setSize_price(400);
                }
                else if (position == 3)
                {
                    productItem.setSize_price(500);
                }
                else if (position == 4)
                {
                    productItem.setSize_price(600);
                }
                else if (position == 5)
                {
                    productItem.setSize_price(700);
                }
                else if (position == 6)
                {
                    productItem.setSize_price(800);
                }

                txt_price_shopping_item.setText(new StringBuilder("₪").append(productItem.getSize_price()));

                img_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFavorite) {
                            img_fav.setBackgroundResource(R.drawable.ic_favorite_border);

                        } else {
                            AddFavoriteHair(true, shoppingItem, itemList[position]);
                            Toast.makeText(context, ""+shoppingItem.getName(), Toast.LENGTH_SHORT).show();
                            img_fav.setBackgroundResource(R.drawable.ic_favorite);
                            SharedPreferences.Editor editor = mSharedPref.edit();
                            editor.putBoolean("fav", false);
                            editor.apply();
                        }
                    }
                });

                txt_add_to_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Create cart Item
                        addToCart(shoppingItem, productItem, itemList[position]);
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void check(ImageView img_fav, boolean favStatus, ShoppingItem shoppingItem, MotionLayout ddd) {
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Favorite")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot documentSnapshot : task.getResult()){

                        FavoriteItem item = documentSnapshot.toObject(FavoriteItem.class);

                        if (shoppingItem.getId().equals(item.getProductId())) {
                            if (favStatus) {
                                img_fav.setBackgroundResource(R.drawable.ic_favorite);

                                img_fav.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DeleteFav(img_fav, ddd, shoppingItem);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    private void AddFavoriteHairProduct(boolean status, ShoppingItem shoppingItem) {
        CollectionReference favoriteRef = FirebaseFirestore.getInstance().collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Favorite");

        FavoriteItem favorite = new FavoriteItem ();
        favorite.setProductId(shoppingItem.getId());
        favorite.setProductName(shoppingItem.getName());
        favorite.setProductImage(shoppingItem.getImage());
        favorite.setProductQuantity (1);
        favorite.setFavStatus(status);
        favorite.setProductPrice(shoppingItem.getPrice());

        favoriteRef.get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //Set data
                            favoriteRef.document()
                                    .set(favorite)
                                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast mToast = new Toast(context);
                                                mToast.setDuration(Toast.LENGTH_LONG);
                                                View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                                                TextView textView = toastView.findViewById(R.id.txt_message);
                                                mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                                textView.setText("פריט נוסף למועדפים");
                                                mToast.setView(toastView);
                                                mToast.show();
                                            }
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText( context, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        });
    }

    private void AddFavoriteHair(boolean status, ShoppingItem shoppingItem, String HairLength) {
        CollectionReference favoriteRef = FirebaseFirestore.getInstance().collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Favorite");

        FavoriteItem favorite = new FavoriteItem ();
        favorite.setProductId(shoppingItem.getId());
        favorite.setProductName(shoppingItem.getName());
        favorite.setProductImage(shoppingItem.getImage());
        favorite.setProductQuantity (1);
        favorite.setFavStatus(status);
        favorite.setProductPrice(this.productItem.getSize_price());
        favorite.setProductLength(HairLength);


        favoriteRef.get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //Set data
                            favoriteRef.document()
                                    .set(favorite)
                                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast mToast = new Toast(context);
                                                mToast.setDuration(Toast.LENGTH_LONG);
                                                View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                                                TextView textView = toastView.findViewById(R.id.txt_message);
                                                mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                                textView.setText("פריט נוסף למועדפים");
                                                mToast.setView(toastView);
                                                mToast.show();
                                            }
                                        }
                                    });
                        }

                    }
                }).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText( context, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        });
    }

    private void DeleteFav(ImageView img_fav, MotionLayout ddd, ShoppingItem shoppingItem) {

        CollectionReference favoriteRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Favorite");

        Query query = favoriteRef.whereEqualTo("productName", shoppingItem.getName());

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : list) {
                                FavoriteItem item = snapshot.toObject(FavoriteItem.class);

                                item.setProductId(snapshot.getId());

                                FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(Common.currentUser.getPhoneNumber())
                                        .collection("Favorite")
                                        .document(snapshot.getId())
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            img_fav.setBackgroundResource(R.drawable.ic_favorite_border);

                                            Toast mToast = new Toast(context);
                                            mToast.setDuration(Toast.LENGTH_LONG);
                                            View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                                            TextView textView = toastView.findViewById(R.id.txt_message);
                                            mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                            textView.setText("פריט הוסר ממועדפים");
                                            mToast.setView(toastView);
                                            mToast.show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private void addToCart(ShoppingItem shoppingItem, ProductItem productItem, String HairLength) {

        //Create cart Item
        CartItem cartItem = new CartItem ();
        cartItem.setProductId(shoppingItem.getId());
        cartItem.setProductName(shoppingItem.getName());
        cartItem.setProductImage(shoppingItem.getImage());
        cartItem.setProductQuantity (1);
        cartItem.setProductPrice(productItem.getSize_price());
        cartItem.setUserPhone (Common.currentUser.getPhoneNumber ());
        cartItem.setProductLength(HairLength);

        // Insert to db

        compositeDisposable.add(cartDataSource.insert(cartItem)
                .subscribeOn( Schedulers.io() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe(() -> {
                            Toast mToast = new Toast(context);
                            mToast.setDuration(Toast.LENGTH_LONG);
                            View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) ((Activity)context).findViewById(R.id.root_layout));
                            TextView textView = toastView.findViewById(R.id.txt_message);
                            mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                            textView.setText("פריט נוסף לסל הקניות!");
                            mToast.setView(toastView);
                            mToast.show();
                        }));

    }

    private void addToCart2(Button txt_add_to_cart2, ShoppingItem shoppingItem) {
        txt_add_to_cart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create cart Item
                CartItem cartItem = new CartItem ();
                cartItem.setProductId(shoppingItem.getId());
                cartItem.setProductName(shoppingItem.getName());
                cartItem.setProductImage(shoppingItem.getImage());
                cartItem.setProductQuantity (1);
                cartItem.setProductPrice(shoppingItem.getPrice());
                cartItem.setUserPhone (Common.currentUser.getPhoneNumber ());

                // Insert to db
                compositeDisposable.add(cartDataSource.insert(cartItem)
                        .subscribeOn( Schedulers.io() )
                        .observeOn( AndroidSchedulers.mainThread() )
                        .subscribe(
                                () -> {
                                    Toast mToast = new Toast(context);
                                    mToast.setDuration(Toast.LENGTH_LONG);
                                    View toastView = ((Activity)context).getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) v.findViewById(R.id.root_layout));
                                    TextView textView = toastView.findViewById(R.id.txt_message);
                                    mToast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
                                    textView.setText("פריט נוסף לסל הקניות!");
                                    mToast.setView(toastView);
                                    mToast.show();
                                },
                                throwable ->
                                        Snackbar.make(v, ""+throwable.getMessage(), Snackbar.LENGTH_SHORT).show()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_item_shopping_name, txt_price_shopping_item, description;
        ImageView img_shopping_item;

        IRecyclerItemSelectedListener listener;

        public void setListener(IRecyclerItemSelectedListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_shopping_item = itemView.findViewById(R.id.img_shopping_item);
            txt_item_shopping_name = itemView.findViewById(R.id.txt_name_shopping_item);
            txt_price_shopping_item = itemView.findViewById(R.id.txt_price_shopping_item);
            description = itemView.findViewById(R.id.description);
            card_shopping = (CardView) itemView.findViewById(R.id.card_shopping);

            firebaseFirestore = FirebaseFirestore.getInstance();

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            listener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}