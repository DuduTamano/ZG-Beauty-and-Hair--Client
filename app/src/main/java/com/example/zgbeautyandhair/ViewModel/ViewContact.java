package com.example.zgbeautyandhair.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zgbeautyandhair.Model.Salon;

import java.util.List;

public class ViewContact extends ViewModel {
    private MutableLiveData<List<Salon>> mutableLiveDataContact;

    public ViewContact() {
        mutableLiveDataContact = new MutableLiveData<>();
    }

    public MutableLiveData<List<Salon>> getMutableLiveDataContact() {
        return mutableLiveDataContact;
    }

    public void setMutableLiveDataContact(List<Salon> salonList) {
        mutableLiveDataContact.setValue(salonList);
    }
}
