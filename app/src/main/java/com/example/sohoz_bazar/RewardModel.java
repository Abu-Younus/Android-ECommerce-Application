package com.example.sohoz_bazar;

import java.util.Date;

public class RewardModel {

    private String type;
    private String lowerLimit;
    private String upperLimit;
    private String discountOrAmount;
    private String body;
    private Date validity;
    private Boolean alreadyUsed;
    private String couponId;

    public RewardModel(String couponId,String type, String lowerLimit, String upperLimit, String discountOrAmount, String body, Date validity, Boolean alreadyUsed) {
        this.couponId = couponId;
        this.type = type;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.discountOrAmount = discountOrAmount;
        this.body = body;
        this.validity = validity;
        this.alreadyUsed = alreadyUsed;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(String lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public String getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(String upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getDiscountOrAmount() {
        return discountOrAmount;
    }

    public void setDiscountOrAmount(String discountOrAmount) {
        this.discountOrAmount = discountOrAmount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getValidity() {
        return validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public Boolean getAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(Boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }
}
