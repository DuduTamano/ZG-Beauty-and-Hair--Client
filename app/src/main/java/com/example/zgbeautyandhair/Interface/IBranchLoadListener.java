package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);

}
