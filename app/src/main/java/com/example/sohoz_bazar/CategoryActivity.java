package com.example.sohoz_bazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sohoz_bazar.DBqueries.lists;
import static com.example.sohoz_bazar.DBqueries.loadedCategoriesNames;
import static com.example.sohoz_bazar.DBqueries.setFragmentData;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private List<HomeModel> homeModelFakeList = new ArrayList<>();
    private HomeAdapter homeAdapter;

    ///////Banner Slider///////
    private List<BannerSliderModel> bannerSliderModelList;
    ///////Banner Slider///////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String title = getIntent().getStringExtra("CategoryName");
        getSupportActionBar().setTitle(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ////////Home Fake List/////////
        List<BannerSliderModel> bannerSliderModelFakeList = new ArrayList<>();
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#d4d4d4"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#d4d4d4"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#d4d4d4"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#d4d4d4"));
        bannerSliderModelFakeList.add(new BannerSliderModel("null", "#d4d4d4"));

        List<HorizontalNewProductModel> horizontalNewProductModelFakeList = new ArrayList<>();
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));
        horizontalNewProductModelFakeList.add(new HorizontalNewProductModel("", "", "", ""));

        homeModelFakeList.add(new HomeModel(0, bannerSliderModelFakeList));
        homeModelFakeList.add(new HomeModel(1, "", "#d6d6d6"));
        homeModelFakeList.add(new HomeModel(2, "", "#9e9e9e", horizontalNewProductModelFakeList, new ArrayList<WishlistModel>()));
        homeModelFakeList.add(new HomeModel(3, "", "#e7e7e7", horizontalNewProductModelFakeList));
        ////////Home Fake List/////////

        ///////Home Container///////

        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        LinearLayoutManager homeLayoutManager = new LinearLayoutManager(this);
        homeLayoutManager.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclerView.setLayoutManager(homeLayoutManager);
        homeAdapter = new HomeAdapter(homeModelFakeList);

        int listPosition = 0;
        for (int x = 0;x < loadedCategoriesNames.size();x++) {
            if (loadedCategoriesNames.get(x).equals(title.toUpperCase())) {
                listPosition = x;
            }
        }

        if (listPosition == 0) {
            loadedCategoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomeModel>());
            setFragmentData(categoryRecyclerView, this,loadedCategoriesNames.size() - 1, title);
        } else {
            homeAdapter = new HomeAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(homeAdapter);
        homeAdapter.notifyDataSetChanged();

        ///////Home Container///////

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.category_search_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home_search_icon) {
            Intent searchIntent = new Intent(this, SearchActivity.class);
            startActivity(searchIntent);
            return true;
        }
        else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
