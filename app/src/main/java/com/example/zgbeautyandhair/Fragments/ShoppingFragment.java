package com.example.zgbeautyandhair.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zgbeautyandhair.Adapter.MyShoppingItemAdapter;
import com.example.zgbeautyandhair.Common.SpacesItemDecorationGrid;
import com.example.zgbeautyandhair.Interface.IShoppingDataLoadListener;
import com.example.zgbeautyandhair.Model.ShoppingItem;
import com.example.zgbeautyandhair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ShoppingFragment extends Fragment implements IShoppingDataLoadListener, View.OnClickListener {
    MyShoppingItemAdapter adapter;
    CollectionReference shoppingItemRef;
    IShoppingDataLoadListener iShoppingDataLoadListener;
    Unbinder unbinder;

    ImageView backBtn;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.chip_brazilian_hair)
    Chip chip_brazilian_hair;

    @OnClick(R.id.chip_brazilian_hair)
    void waveCareChipClick(){
        setSelectedChip(chip_brazilian_hair);
        loadShoppingItem("Brazilian Hair");
    }

    @BindView(R.id.chip_wax)
    Chip chip_wax;

    @OnClick(R.id.chip_wax)
    void waxChipClick(){
        setSelectedChip(chip_wax);
        loadShoppingItem("Wax");
    }

    @BindView(R.id.chip_spray)
    Chip chip_spray;

    @OnClick(R.id.chip_spray)
    void sprayChipClick(){
        setSelectedChip(chip_spray);
        loadShoppingItem("Spray");
    }

    @BindView(R.id.chip_hair_cream)
    Chip chip_hair_cream;

    @OnClick(R.id.chip_hair_cream)
    void hairCareChipClick(){
        setSelectedChip(chip_hair_cream);
        loadShoppingItem("Hair Cream");
    }

    @BindView(R.id.chip_wave)
    Chip chip_wave;

    @OnClick(R.id.chip_wave)
    void bodyCareChipClick(){
        setSelectedChip(chip_wave);
        loadShoppingItem("פאות");
    }

    @BindView(R.id.recycler_items)
    RecyclerView recycler_items;

    private void loadShoppingItem(String itemMenu) {
        List<ShoppingItem> shoppingItems = new ArrayList<>();

        shoppingItemRef = FirebaseFirestore.getInstance().collection("Shopping")
                .document(itemMenu)
                .collection("items");

        //get data
        shoppingItemRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iShoppingDataLoadListener.onShoppingDataLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {

                    for (DocumentSnapshot itemSnapShot:task.getResult())
                    {
                        ShoppingItem shoppingItem = itemSnapShot.toObject(ShoppingItem.class);
                        //Add to don't get null reference
                        shoppingItem.setId(itemSnapShot.getId());
                        shoppingItems.add(shoppingItem);

                    }
                    iShoppingDataLoadListener.onShoppingDataLoadSuccess(shoppingItems);
                }
            }
        });
    }

    private void setSelectedChip(Chip chip) {
        //Set color
        for (int i=0; i<chipGroup.getChildCount(); i++)
        {
            Chip chipItem = (Chip)chipGroup.getChildAt(i);

            //if not selected
            if (chipItem.getId() != chip.getId())
            {
                chipItem.setChipBackgroundColorResource(android.R.color.white);
                chipItem.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            //if selected
            else
            {
                chipItem.setChipBackgroundColorResource(android.R.color.white);
                chipItem.setTextColor(getResources().getColor(android.R.color.black));
            }

        }
    }

    public ShoppingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_shopping, container, false);

        unbinder = ButterKnife.bind(this, itemView);
        iShoppingDataLoadListener = this;

        backBtn = itemView.findViewById(R.id.back_pressed);

        //go back to home fragment
        backBtn.setOnClickListener(this);

        //Default load
        loadShoppingItem("Brazilian Hair");

        initView();

        return itemView;
    }

    @Override
    public void onClick(View view) {
        Fragment someFragment = new HomeFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

    //הצגת התמונות בתוך SHOPPING FRAGMENT
    private void initView() {
        recycler_items.setHasFixedSize(true);
        recycler_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler_items.addItemDecoration(new SpacesItemDecorationGrid(2));

    }

    @Override
    public void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList) {
        adapter = new MyShoppingItemAdapter(getContext(), shoppingItemList);
        recycler_items.setAdapter(adapter);
    }

    @Override
    public void onShoppingDataLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (adapter != null)
            adapter.onDestroy();
        super.onDestroy();
    }
}
