package com.example.sohoz_bazar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView viewAllProductRecyclerView;

    private GridView viewAllProductGridView;

    public static List<HorizontalNewProductModel> horizontalNewProductModelList;
    public static List<WishlistModel> wishlistModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewAllProductRecyclerView = findViewById(R.id.view_all_product_recycler_view);
        viewAllProductGridView = findViewById(R.id.view_all_product_grid_view);

        int layout_code = getIntent().getIntExtra("layout_code", -1);

        if (layout_code == 0) {
            viewAllProductGridView.setVisibility(View.GONE);
            viewAllProductRecyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            viewAllProductRecyclerView.setLayoutManager(layoutManager);
            WishlistAdapter wishlistAdapter = new WishlistAdapter(wishlistModelList, false);
            viewAllProductRecyclerView.setAdapter(wishlistAdapter);
            wishlistAdapter.notifyDataSetChanged();

        } else if (layout_code == 1) {
            viewAllProductRecyclerView.setVisibility(View.GONE);
            viewAllProductGridView.setVisibility(View.VISIBLE);
            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalNewProductModelList);
            viewAllProductGridView.setAdapter(gridProductLayoutAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
