package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Salon;

import java.util.List;

public interface SalonSalon {
    void onSalonLoadSuccess(List<Salon> salonList);
    void onSalonLoadFailed(String message);
}
