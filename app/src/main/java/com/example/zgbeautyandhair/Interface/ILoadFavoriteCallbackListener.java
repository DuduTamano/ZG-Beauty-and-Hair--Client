package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Database.CartItem;
import com.example.zgbeautyandhair.Model.FavoriteItem;

import java.util.List;

public interface ILoadFavoriteCallbackListener {
    void onLoadFavoriteSuccess(List<FavoriteItem> cartItemList);
    void onLoadFavoriteFailed(String message);
}
