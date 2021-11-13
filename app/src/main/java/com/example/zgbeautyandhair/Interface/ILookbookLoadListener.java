package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Banner;

import java.util.List;

public interface ILookbookLoadListener {
    void onLookbookSuccess(List<Banner> banners);
    void onLookbookFailed(String message);
}
