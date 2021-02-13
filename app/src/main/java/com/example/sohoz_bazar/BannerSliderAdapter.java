package com.example.sohoz_bazar;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class BannerSliderAdapter extends PagerAdapter {

    List<BannerSliderModel> bannerSliderModelList;

    public BannerSliderAdapter(List<BannerSliderModel> bannerSliderModelList) {
        this.bannerSliderModelList = bannerSliderModelList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_slider_item_layout, container, false);
        LinearLayout bannerContainer = view.findViewById(R.id.banner_container);
        bannerContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(bannerSliderModelList.get(position).getBackgroundColor())));
        ImageView bannerImage = view.findViewById(R.id.banner_slide);
        Glide.with(container.getContext()).load(bannerSliderModelList.get(position).getBanner()).apply(new RequestOptions().placeholder(R.drawable.placeholder_img)).into(bannerImage);
        container.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return bannerSliderModelList.size();
    }
}
