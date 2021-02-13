package com.example.sohoz_bazar;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RewardAdapter.ViewHolder> {

    private List<RewardModel> rewardModelList;
    private Boolean useMiniLayout = false;
    private RecyclerView couponsRecyclerView;
    private LinearLayout selectedCouponContainer;
    private String productOriginalPrice;
    private TextView selectedCouponTitle;
    private TextView selectedCouponValidity;
    private TextView selectedCouponBody;
    private TextView discountedPrice;
    private int cartItemPosition = -1;
    private List<CartItemModel> cartItemModelList;

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }

    public RewardAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponsRecyclerView, LinearLayout selectedCouponContainer, String productOriginalPrice, TextView selectedCouponTitle, TextView selectedCouponValidity, TextView selectedCouponBody, TextView discountedPrice) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponsRecyclerView = couponsRecyclerView;
        this.selectedCouponContainer = selectedCouponContainer;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = selectedCouponTitle;
        this.selectedCouponValidity = selectedCouponValidity;
        this.selectedCouponBody = selectedCouponBody;
        this.discountedPrice = discountedPrice;
    }

    public RewardAdapter(int cartItemPosition,List<RewardModel> rewardModelList, Boolean useMiniLayout, RecyclerView couponsRecyclerView, LinearLayout selectedCouponContainer, String productOriginalPrice, TextView selectedCouponTitle, TextView selectedCouponValidity, TextView selectedCouponBody, TextView discountedPrice, List<CartItemModel> cartItemModelList) {
        this.cartItemPosition = cartItemPosition;
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
        this.couponsRecyclerView = couponsRecyclerView;
        this.selectedCouponContainer = selectedCouponContainer;
        this.productOriginalPrice = productOriginalPrice;
        this.selectedCouponTitle = selectedCouponTitle;
        this.selectedCouponValidity = selectedCouponValidity;
        this.selectedCouponBody = selectedCouponBody;
        this.discountedPrice = discountedPrice;
        this.cartItemModelList = cartItemModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (useMiniLayout) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mini_reward_item_layout, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rewards_item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String couponId = rewardModelList.get(position).getCouponId();
        String type = rewardModelList.get(position).getType();
        Date validity = rewardModelList.get(position).getValidity();
        String body = rewardModelList.get(position).getBody();
        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discountOrAmount = rewardModelList.get(position).getDiscountOrAmount();
        Boolean alreadyUsed = rewardModelList.get(position).getAlreadyUsed();

        holder.setData(couponId, type, validity, body, lowerLimit, upperLimit, discountOrAmount, alreadyUsed);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView couponTitle;
        private TextView couponValidity;
        private TextView couponBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponValidity = itemView.findViewById(R.id.coupon_validity);
            couponBody = itemView.findViewById(R.id.coupon_body);
        }

        private void setData(final String couponId, final String type, final Date validity, final String body, final String lowerLimit, final String upperLimit, final String discountOrAmount, final boolean alreadyUsed) {

            if (type.equals("Discount")) {
                couponTitle.setText(type);
            } else {
                couponTitle.setText("Flat BDT. " + discountOrAmount + " OFF");
            }

            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");

            if (alreadyUsed) {
                couponValidity.setText("Already Used");
                couponValidity.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                couponBody.setTextColor(Color.parseColor("#50ffffff"));
                couponTitle.setTextColor(Color.parseColor("#50ffffff"));
            } else {
                couponValidity.setText("till " + simpleDateFormat.format(validity));
                couponValidity.setTextColor(itemView.getContext().getResources().getColor(R.color.rewardStartColor));
                couponBody.setTextColor(Color.parseColor("#ffffff"));
                couponTitle.setTextColor(Color.parseColor("#ffffff"));
            }
            couponBody.setText(body);

            if (useMiniLayout) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!alreadyUsed) {
                            selectedCouponTitle.setText(type);
                            selectedCouponValidity.setText(simpleDateFormat.format(validity));
                            selectedCouponBody.setText(body);

                            if (Long.valueOf(productOriginalPrice) > Long.valueOf(lowerLimit) && Long.valueOf(productOriginalPrice) < Long.valueOf(upperLimit)) {
                                if (type.equals("Discount")) {
                                    Long discountAmount = Long.valueOf(productOriginalPrice) * Long.valueOf(discountOrAmount) / 100;
                                    discountedPrice.setText("BDT. " + String.valueOf(Long.valueOf(productOriginalPrice) - discountAmount) + "/-");
                                    discountedPrice.setTextColor(Color.parseColor("#000000"));
                                } else {
                                    discountedPrice.setText("BDT. " + String.valueOf(Long.valueOf(productOriginalPrice) - Long.valueOf(discountOrAmount)) + "/-");
                                    discountedPrice.setTextColor(Color.parseColor("#000000"));
                                }
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(couponId);
                                }
                            } else {
                                if (cartItemPosition != -1) {
                                    cartItemModelList.get(cartItemPosition).setSelectedCouponId(null);
                                }
                                discountedPrice.setText("Invalid!");
                                discountedPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                                Toast.makeText(itemView.getContext(), "Sorry! Product doesn't matches the coupon terms.", Toast.LENGTH_SHORT).show();
                            }

                            if (couponsRecyclerView.getVisibility() == View.GONE) {
                                selectedCouponContainer.setVisibility(View.GONE);
                                couponsRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                couponsRecyclerView.setVisibility(View.GONE);
                                selectedCouponContainer.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }
    }
}
