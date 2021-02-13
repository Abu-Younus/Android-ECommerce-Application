package com.example.sohoz_bazar;


import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import static com.example.sohoz_bazar.DBqueries.loadWishlist;
import static com.example.sohoz_bazar.DBqueries.wishList;
import static com.example.sohoz_bazar.DBqueries.wishlistModelList;

public class MyWishlistFragment extends Fragment {


    public MyWishlistFragment() {

    }

    private RecyclerView myWishlistRecyclerView;

    private Dialog loadingDialog;

    public static WishlistAdapter wishlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        myWishlistRecyclerView = view.findViewById(R.id.my_wishlist_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myWishlistRecyclerView.setLayoutManager(linearLayoutManager);

        if (wishlistModelList.size() == 0) {
            wishList.clear();
            loadWishlist(getContext(), loadingDialog, true);
        } else {
            loadingDialog.dismiss();
        }

        wishlistAdapter = new WishlistAdapter(wishlistModelList, true);
        myWishlistRecyclerView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();

        return view;
    }

}
