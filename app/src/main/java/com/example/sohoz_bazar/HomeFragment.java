package com.example.sohoz_bazar;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import static com.example.sohoz_bazar.DBqueries.categoryModelList;
import static com.example.sohoz_bazar.DBqueries.lists;
import static com.example.sohoz_bazar.DBqueries.loadCategories;
import static com.example.sohoz_bazar.DBqueries.loadedCategoriesNames;
import static com.example.sohoz_bazar.DBqueries.setFragmentData;

public class HomeFragment extends Fragment {


    public HomeFragment() {
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryModelFakeList = new ArrayList<>();

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView homeContainerRecyclerView;
    private List<HomeModel> homeModelFakeList = new ArrayList<>();
    private HomeAdapter homeAdapter;

    private ImageView noInternetConnection;
    private TextView tvNoInternetConnection;
    private Button btnRetry;

    ///////Banner Slider///////
    private List<BannerSliderModel> bannerSliderModelList;
    ///////Banner Slider///////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetConnection = view.findViewById(R.id.no_internet_connection);
        tvNoInternetConnection = view.findViewById(R.id.tv_no_internet_connection);
        btnRetry = view.findViewById(R.id.btn_retry);
        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary),
                getContext().getResources().getColor(R.color.colorPrimary),
                getContext().getResources().getColor(R.color.colorPrimary));

        categoryRecyclerView = view.findViewById(R.id.category_recyclerView);
        homeContainerRecyclerView = view.findViewById(R.id.home_container_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager homeLayoutManager = new LinearLayoutManager(getContext());
        homeLayoutManager.setOrientation(RecyclerView.VERTICAL);
        homeContainerRecyclerView.setLayoutManager(homeLayoutManager);


        ////////Category Fake List/////////
        categoryModelFakeList.add(new CategoryModel("null", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        categoryModelFakeList.add(new CategoryModel("", ""));
        ////////Category Fake List/////////

        ////////Home Fake List/////////
        List<BannerSliderModel> bannerSliderModelFakeList = new ArrayList<>();
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#dfdfdf"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#dfdfdf"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#dfdfdf"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#dfdfdf"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#dfdfdf"));

        List<HorizontalNewProductModel> horizontalNewProductModelFakeList = new ArrayList<>();
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));

        homeModelFakeList.add(new HomeModel(0, bannerSliderModelFakeList));
        homeModelFakeList.add(new HomeModel(1, "", "#dfdfdf"));
        homeModelFakeList.add(new HomeModel(2, "", "#dfdfdf", horizontalNewProductModelFakeList, new ArrayList<WishlistModel>()));
        homeModelFakeList.add(new HomeModel(3, "", "#dfdfdf", horizontalNewProductModelFakeList));
        ////////Home Fake List/////////

        categoryAdapter = new CategoryAdapter(categoryModelFakeList);

        homeAdapter = new HomeAdapter(homeModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            HomeActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            tvNoInternetConnection.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homeContainerRecyclerView.setVisibility(View.VISIBLE);

            if (categoryModelList.size() == 0) {
                loadCategories(categoryRecyclerView, getContext());
            } else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);
            ///////Home Container///////

            if (lists.size() == 0) {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomeModel>());
                setFragmentData(homeContainerRecyclerView, getContext(),0, "Home");
            } else {
                homeAdapter = new HomeAdapter(lists.get(0));
                homeAdapter.notifyDataSetChanged();
            }
            homeContainerRecyclerView.setAdapter(homeAdapter);
            ///////Home Container///////

        } else {
            HomeActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homeContainerRecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            tvNoInternetConnection.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
        }

        //////Swipe Refresh Layout///////

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });

        //////Swipe Refresh Layout///////

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return view;
    }

    private void reloadPage() {
        networkInfo = connectivityManager.getActiveNetworkInfo();

        DBqueries.clearData();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            HomeActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            tvNoInternetConnection.setVisibility(View.GONE);
            btnRetry.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homeContainerRecyclerView.setVisibility(View.VISIBLE);

            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            homeAdapter = new HomeAdapter(homeModelFakeList);
            categoryRecyclerView.setAdapter(categoryAdapter);
            homeContainerRecyclerView.setAdapter(homeAdapter);

            loadCategories(categoryRecyclerView, getContext());

            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomeModel>());
            setFragmentData(homeContainerRecyclerView, getContext(),0, "Home");

        } else {
            HomeActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(), "Internet Connection not found!", Toast.LENGTH_SHORT).show();
            categoryRecyclerView.setVisibility(View.GONE);
            homeContainerRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_internet_connection).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            tvNoInternetConnection.setVisibility(View.VISIBLE);
            btnRetry.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
