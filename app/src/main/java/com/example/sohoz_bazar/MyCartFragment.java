package com.example.sohoz_bazar;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.sohoz_bazar.DBqueries.cartItemModelList;
import static com.example.sohoz_bazar.DBqueries.cartList;
import static com.example.sohoz_bazar.DBqueries.loadAddresses;
import static com.example.sohoz_bazar.DBqueries.loadCartList;

public class MyCartFragment extends Fragment {


    public MyCartFragment() {
        // Required empty public constructor
    }

    private RecyclerView cartItemRecyclerView;

    private Button btnContinue;

    private Dialog loadingDialog;

    public static CartAdapter cartAdapter;

    private TextView totalCartAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_cart, container, false);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        cartItemRecyclerView = view.findViewById(R.id.cart_items_recycler_view);
        btnContinue = view.findViewById(R.id.btn_cart_continue);
        totalCartAmount = view.findViewById(R.id.cart_total_amount);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        cartItemRecyclerView.setLayoutManager(linearLayoutManager);

        cartAdapter = new CartAdapter(cartItemModelList,totalCartAmount,true);
        cartItemRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryActivity.cartItemModelList = new ArrayList<>();
                DeliveryActivity.fromCart = true;
                for (int x = 0;x < cartItemModelList.size();x++) {
                    CartItemModel cartItemModel = cartItemModelList.get(x);
                    if (cartItemModel.isInStock()) {
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                loadingDialog.show();
                if (DBqueries.addressesModelList.size() == 0) {
                    loadAddresses(getContext(), loadingDialog, true);
                } else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
        LinearLayout parent = (LinearLayout) totalCartAmount.getParent().getParent();

        if (DBqueries.rewardModelList.size() == 0) {
            loadingDialog.show();
            DBqueries.loadRewards(getContext(), loadingDialog, false);
        }

        if (cartItemModelList.size() == 0) {
            cartList.clear();
            loadCartList(getContext(), loadingDialog, true, new TextView(getContext()), totalCartAmount);
        } else {
            if (cartItemModelList.get(cartItemModelList.size()-1).getType() == CartItemModel.TOTAL_AMOUNT) {
                parent.setVisibility(View.VISIBLE);
            } else {
                parent.setVisibility(View.INVISIBLE);
            }
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCouponId())) {
                for (RewardModel rewardModel : DBqueries.rewardModelList) {
                    if (rewardModel.getCouponId().equals(cartItemModel.getSelectedCouponId())) {
                        rewardModel.setAlreadyUsed(false);
                    }
                }
                cartItemModel.setSelectedCouponId(null);
                if (MyRewardsFragment.rewardAdapter != null) {
                    MyRewardsFragment.rewardAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
