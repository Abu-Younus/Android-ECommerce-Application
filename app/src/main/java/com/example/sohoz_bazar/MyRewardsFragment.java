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

public class MyRewardsFragment extends Fragment {


    public MyRewardsFragment() {
        // Required empty public constructor
    }

    private RecyclerView myRewardsRecyclerView;
    public static RewardAdapter rewardAdapter;

    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_rewards, container, false);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        myRewardsRecyclerView = view.findViewById(R.id.my_rewards_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myRewardsRecyclerView.setLayoutManager(linearLayoutManager);

        rewardAdapter = new RewardAdapter(DBqueries.rewardModelList, false);
        myRewardsRecyclerView.setAdapter(rewardAdapter);

        if (DBqueries.rewardModelList.size() == 0) {
            DBqueries.loadRewards(getContext(), loadingDialog, true);
        } else {
            loadingDialog.dismiss();
        }

        rewardAdapter.notifyDataSetChanged();

        return view;
    }

}
