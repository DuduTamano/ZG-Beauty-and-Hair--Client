package com.example.zgbeautyandhair.Interface;

import com.example.zgbeautyandhair.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
