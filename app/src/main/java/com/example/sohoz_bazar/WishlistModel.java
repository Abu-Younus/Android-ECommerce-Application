package com.example.sohoz_bazar;

import java.util.ArrayList;

public class WishlistModel {

    private String productID;
    private String productImage;
    private String productTitle;
    private long freecoupons;
    private String rating;
    private long totalRatings;
    private String productPrice;
    private String cuttedPrice;
    private boolean COD;
    private boolean inStock;
    private ArrayList<String> tags;

    public WishlistModel(String productID, String productImage, String productTitle, long freecoupons, String rating, long totalRatings, String productPrice, String cuttedPrice, boolean COD, boolean inStock) {
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.freecoupons = freecoupons;
        this.rating = rating;
        this.totalRatings = totalRatings;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.COD = COD;
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public long getFreecoupons() {
        return freecoupons;
    }

    public void setFreecoupons(long freecoupons) {
        this.freecoupons = freecoupons;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public boolean isCOD() {
        return COD;
    }

    public void setCOD(boolean COD) {
        this.COD = COD;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
