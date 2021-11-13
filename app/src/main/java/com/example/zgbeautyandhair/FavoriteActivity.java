package com.example.zgbeautyandhair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.MyFavoriteAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Interface.ILoadFavoriteCallbackListener;
import com.example.zgbeautyandhair.Model.FavoriteItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;

public class FavoriteActivity extends AppCompatActivity implements ILoadFavoriteCallbackListener {

    RecyclerView recycler_fav;

    ILoadFavoriteCallbackListener iLoadFavoriteCallbackListener;

    AlertDialog dialog;

    MyFavoriteAdapter adapter;

    @BindView(R.id.txt_empty)
    TextView txt_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ButterKnife.bind(this);

        txt_empty = findViewById(R.id.txt_empty);

        loadFavorite();

        initView();
    }
    private void initView() {
        iLoadFavoriteCallbackListener = this;

        recycler_fav = findViewById(R.id.recycler_fav);

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();

        recycler_fav.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_fav.setLayoutManager(linearLayoutManager);
    }

    private void loadFavorite() {
        List<FavoriteItem> FavItems = new ArrayList<>();

        CollectionReference favoriteRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document( Common.currentUser.getPhoneNumber())
                .collection("Favorite");

        favoriteRef.get()
                .addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot itemSnapShot : task.getResult()) {
                            FavoriteItem shoppingItem = itemSnapShot.toObject( FavoriteItem.class );
                            //Add to don't get null reference
                            shoppingItem.setProductId( itemSnapShot.getId() );
                            FavItems.add( shoppingItem );

                        }
                        iLoadFavoriteCallbackListener.onLoadFavoriteSuccess(FavItems);
                    }
                });
    }

    @Override
    public void onLoadFavoriteSuccess(List<FavoriteItem> cartItemList) {
        adapter = new MyFavoriteAdapter( this, cartItemList );
        recycler_fav.setAdapter( adapter );

        if (adapter.getItemCount() == 0)
        {
            txt_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoadFavoriteFailed(String message) {
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