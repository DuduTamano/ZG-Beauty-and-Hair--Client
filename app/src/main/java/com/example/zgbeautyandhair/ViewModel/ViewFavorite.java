package com.example.zgbeautyandhair.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.zgbeautyandhair.Model.ShoppingItem;

import java.util.List;

public class ViewFavorite extends ViewModel {
    private MutableLiveData<List<ShoppingItem>> mutableLiveDataFavoriteList;

    public ViewFavorite () {
        mutableLiveDataFavoriteList = new MutableLiveData<>();
    }

    public MutableLiveData<List<ShoppingItem>> getMutableLiveDataFavoriteList() {
        return mutableLiveDataFavoriteList;
    }

    public void setMutableLiveDataOrderList(List<ShoppingItem> shoppingItemList) {
        mutableLiveDataFavoriteList.setValue(shoppingItemList);
    }
}
