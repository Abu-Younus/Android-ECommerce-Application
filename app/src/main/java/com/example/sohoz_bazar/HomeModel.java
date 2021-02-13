package com.example.sohoz_bazar;

import java.util.List;

public class HomeModel {

    public static final int BANNER_SLIDER = 0;
    public static final int STRIP_ADD_BANNER = 1;
    public static final int HORIZONTAL_NEW_PRODUCT = 2;
    public static final int GRID_TOP_PRODUCT = 3;

    private int type;

    ///////Banner Slider////////

    private List<BannerSliderModel> bannerSliderModelList;

    public HomeModel(int type, List<BannerSliderModel> bannerSliderModelList) {
        this.type = type;
        this.bannerSliderModelList = bannerSliderModelList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<BannerSliderModel> getBannerSliderModelList() {
        return bannerSliderModelList;
    }

    public void setBannerSliderModelList(List<BannerSliderModel> bannerSliderModelList) {
        this.bannerSliderModelList = bannerSliderModelList;
    }

    //////Banner Slider////////

    ///////Strip Add////////

    private String resource;
    private String backgroundColor;

    public HomeModel(int type, String resource, String backgroundColor) {
        this.type = type;
        this.resource = resource;
        this.backgroundColor = backgroundColor;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    ///////Strip Add////////

    ///////Horizontal New Product////////

    private String horizontalLayouttitle;
    private List<HorizontalNewProductModel> horizontalNewProductModelList;
    private List<WishlistModel> viewAllProductList;

    public HomeModel(int type, String horizontalLayouttitle, String backgroundColor, List<HorizontalNewProductModel> horizontalNewProductModelList, List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.horizontalLayouttitle = horizontalLayouttitle;
        this.backgroundColor = backgroundColor;
        this.horizontalNewProductModelList = horizontalNewProductModelList;
        this.viewAllProductList = viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }

    public HomeModel(int type, String horizontalLayouttitle, String backgroundColor, List<HorizontalNewProductModel> horizontalNewProductModelList) {
        this.type = type;
        this.horizontalLayouttitle = horizontalLayouttitle;
        this.backgroundColor = backgroundColor;
        this.horizontalNewProductModelList = horizontalNewProductModelList;
    }

    public String getHorizontalLayouttitle() {
        return horizontalLayouttitle;
    }

    public void setHorizontalLayouttitle(String horizontalLayouttitle) {
        this.horizontalLayouttitle = horizontalLayouttitle;
    }

    public List<HorizontalNewProductModel> getHorizontalNewProductModelList() {
        return horizontalNewProductModelList;
    }

    public void setHorizontalNewProductModelList(List<HorizontalNewProductModel> horizontalNewProductModelList) {
        this.horizontalNewProductModelList = horizontalNewProductModelList;
    }

    ///////Horizontal New Product////////

    ///////Grid Top Product////////

    private List<HorizontalNewProductModel> gridProductLayoutModelList;
    private String gridLayoutTitle;

    public HomeModel(int type, List<HorizontalNewProductModel> gridProductLayoutModelList, String gridLayoutTitle) {
        this.type = type;
        this.gridLayoutTitle = gridLayoutTitle;
        this.gridProductLayoutModelList = gridProductLayoutModelList;
    }

    public List<HorizontalNewProductModel> getGridProductLayoutModelList() {
        return gridProductLayoutModelList;
    }

    public void setGridProductLayoutModelList(List<HorizontalNewProductModel> gridProductLayoutModelList) {
        this.gridProductLayoutModelList = gridProductLayoutModelList;
    }

    public String getGridLayoutTitle() {
        return gridLayoutTitle;
    }

    public void setGridLayoutTitle(String gridLayoutTitle) {
        this.gridLayoutTitle = gridLayoutTitle;
    }

    ///////Grid Top Product////////

}

