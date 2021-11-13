package com.example.zgbeautyandhair.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zgbeautyandhair.Adapter.MySalonAdapter;
import com.example.zgbeautyandhair.Common.Common;
import com.example.zgbeautyandhair.Common.SpaceItemDecoration;
import com.example.zgbeautyandhair.Interface.IAllSalonLoadListener;
import com.example.zgbeautyandhair.Interface.IBranchLoadListener;
import com.example.zgbeautyandhair.Model.Salon;
import com.example.zgbeautyandhair.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BookingSalonFragment extends Fragment implements IAllSalonLoadListener, IBranchLoadListener {

    CollectionReference allSalonRef;
    CollectionReference branchRef;

    IAllSalonLoadListener iAllSalonLoadListener;
    IBranchLoadListener iBranchLoadListener;

    @BindView(R.id.spinner)
    MaterialSpinner spinner;
    @BindView(R.id.recycler_salon)
    RecyclerView recycler_salon;

    Unbinder unbinder;

    AlertDialog dialog;

    static BookingSalonFragment instance;

    public static BookingSalonFragment getInstance() {
        if(instance == null)
            instance = new BookingSalonFragment();
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allSalonRef = FirebaseFirestore.getInstance().collection("AllSalon");

        iAllSalonLoadListener = this;
        iBranchLoadListener = this;

        dialog = new SpotsDialog.Builder().setContext(getActivity()).build();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View itemView = inflater.inflate(R.layout.fragment_booking_salon,container,false);

        unbinder = ButterKnife.bind(this,itemView);

        initView();
        loadAllSalon();

        return itemView;
    }

    private void initView() {
        recycler_salon.setHasFixedSize(true);
        recycler_salon.setLayoutManager(new GridLayoutManager(getActivity(),2));
        recycler_salon.addItemDecoration(new SpaceItemDecoration(4));
    }

    private void loadAllSalon() {
        allSalonRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        List<String> list = new ArrayList<>();
                        list.add("בחרי מיקום");

                        for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                            list.add(documentSnapshot.getId());

                        iAllSalonLoadListener.onAllSalonLoadSuccess(list);
                    }
                }).addOnFailureListener(e ->
                iAllSalonLoadListener.onAllSalonLoadFailed(e.getMessage()));
    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
        spinner.setItems(areaNameList);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position > 0)
            {
                loadBranchOfCity(item.toString());
            }
            else
                recycler_salon.setVisibility(View.GONE);
        });
    }

    private void loadBranchOfCity(String cityName) {
        dialog.show();

        Common.city = cityName;
        branchRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(cityName)
                .collection("Branch");

        branchRef.get().addOnCompleteListener(task -> {
            List<Salon> list = new ArrayList<>();
            if (task.isSuccessful())
            {
                for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                {
                    Salon salon = documentSnapshot.toObject(Salon.class);
                    salon.setSalonId(documentSnapshot.getId());
                    list.add(salon);

                }
                iBranchLoadListener.onBranchLoadSuccess(list);
            }
        }).addOnFailureListener(e ->
                iBranchLoadListener.onBranchLoadFailed(e.getMessage()));
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {
        MySalonAdapter adapter = new MySalonAdapter(getActivity(),salonList);
        recycler_salon.setAdapter(adapter);
        recycler_salon.setVisibility(View.VISIBLE);

        dialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    }
}

