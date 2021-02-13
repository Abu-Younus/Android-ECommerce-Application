package com.example.sohoz_bazar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeAdapter extends RecyclerView.Adapter {

    private List<HomeModel> homeModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private int lastPosition = -1;

    public HomeAdapter(List<HomeModel> homeModelList) {
        this.homeModelList = homeModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homeModelList.get(position).getType()) {
            case 0:
                return HomeModel.BANNER_SLIDER;

            case 1:
                return HomeModel.STRIP_ADD_BANNER;

            case 2:
                return HomeModel.HORIZONTAL_NEW_PRODUCT;

            case 3:
                return HomeModel.GRID_TOP_PRODUCT;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HomeModel.BANNER_SLIDER:
                View bannerSliderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_slider_layout, parent, false);
                return new BannerSliderViewHolder(bannerSliderView);

            case HomeModel.STRIP_ADD_BANNER:
                View stripAddView = LayoutInflater.from(parent.getContext()).inflate(R.layout.strip_ad_layout, parent, false);
                return new StripAddViewHolder(stripAddView);

            case HomeModel.HORIZONTAL_NEW_PRODUCT:
                View horizontalProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_new_product_layout, parent, false);
                return new HorizontalNewProductViewHolder(horizontalProductView);

            case HomeModel.GRID_TOP_PRODUCT:
                View gridProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridTopProductViewHolder(gridProductView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homeModelList.get(position).getType()) {
            case HomeModel.BANNER_SLIDER:
                List<BannerSliderModel> bannerSliderModelList = homeModelList.get(position).getBannerSliderModelList();
                ((BannerSliderViewHolder) holder).setBannerSliderViewPager(bannerSliderModelList);
                break;

            case HomeModel.STRIP_ADD_BANNER:
                String resource = homeModelList.get(position).getResource();
                String color = homeModelList.get(position).getBackgroundColor();
                ((StripAddViewHolder)holder).setstripAd(resource, color);
                break;

            case HomeModel.HORIZONTAL_NEW_PRODUCT:
                String layoutColor = homeModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitle = homeModelList.get(position).getHorizontalLayouttitle();
                List<WishlistModel> viewAllProductList = homeModelList.get(position).getViewAllProductList();
                List<HorizontalNewProductModel> horizontalNewProductModelList = homeModelList.get(position).getHorizontalNewProductModelList();
                ((HorizontalNewProductViewHolder)holder).setHorizontalProductLayout(horizontalNewProductModelList, horizontalLayoutTitle, layoutColor, viewAllProductList);
                break;

            case HomeModel.GRID_TOP_PRODUCT:
                String gridLayoutColor = homeModelList.get(position).getBackgroundColor();
                String gridLayoutTitle = homeModelList.get(position).getHorizontalLayouttitle();
                List<HorizontalNewProductModel> gridProductLayoutModelList = homeModelList.get(position).getHorizontalNewProductModelList();
                ((GridTopProductViewHolder)holder).setGridProductLayout(gridProductLayoutModelList, gridLayoutTitle, gridLayoutColor);
                break;

            default:
                return;
        }

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return homeModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private int currentPage;
        private Timer timer;
        private final long DELAY_TIME = 3000;
        private final long PERIOD_TIME = 3000;

        private List<BannerSliderModel> arrangeList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);
        }

        private void setBannerSliderViewPager(final List<BannerSliderModel> bannerSliderModelList) {
            currentPage = 2;
            if (timer != null) {
                timer.cancel();
            }

            arrangeList = new ArrayList<>();
            for (int x = 0;x < bannerSliderModelList.size();x++) {
                arrangeList.add(x, bannerSliderModelList.get(x));
            }
            arrangeList.add(0, bannerSliderModelList.get(bannerSliderModelList.size() - 2));
            arrangeList.add(1, bannerSliderModelList.get(bannerSliderModelList.size() - 1));
            arrangeList.add(bannerSliderModelList.get(0));
            arrangeList.add(bannerSliderModelList.get(1));

            BannerSliderAdapter bannerSliderAdapter = new BannerSliderAdapter(arrangeList);
            bannerSliderViewPager.setAdapter(bannerSliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangeList);
                    }
                }
            };

            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangeList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    pageLooper(arrangeList);
                    stopBannerSlideShow();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        startBannerSlideShow(arrangeList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<BannerSliderModel> bannerSliderModelList) {
            if (currentPage == bannerSliderModelList.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
            if (currentPage == 1) {
                currentPage = bannerSliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void startBannerSlideShow(final List<BannerSliderModel> bannerSliderModelList) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= bannerSliderModelList.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);
        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }
    }

    public class StripAddViewHolder extends RecyclerView.ViewHolder {

        private ImageView stripAdImage;
        private LinearLayout stripAdContainer;

        public StripAddViewHolder(@NonNull View itemView) {
            super(itemView);

            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
            stripAdContainer = itemView.findViewById(R.id.strip_ad_container);
        }

        private void setstripAd(String resource, String color) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholder_img)).into(stripAdImage);
            stripAdContainer.setBackgroundColor(Color.parseColor(color));
        }
    }

    public class HorizontalNewProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout horizontalProductContainer;
        private TextView horizontalProductLayoutTitle;
        private Button btnViewAll;
        private RecyclerView horizontalProductRecyclerView;

        public HorizontalNewProductViewHolder(@NonNull View itemView) {
            super(itemView);

            horizontalProductContainer = itemView.findViewById(R.id.product_container);
            horizontalProductLayoutTitle = itemView.findViewById(R.id.tv_new_product_horizontal);
            btnViewAll = itemView.findViewById(R.id.btn_view_all_new_product_horizontal);
            horizontalProductRecyclerView = itemView.findViewById(R.id.horizontal_new_product_recycler_view);
            horizontalProductRecyclerView.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(final List<HorizontalNewProductModel> horizontalNewProductModelList, final String horizontalLayoutTitle, String color, final List<WishlistModel> viewAllProductList) {

            horizontalProductContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalProductLayoutTitle.setText(horizontalLayoutTitle);

            for(final HorizontalNewProductModel model : horizontalNewProductModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductPrice(task.getResult().getString("product_price"));

                                WishlistModel wishlistModel = viewAllProductList.get(horizontalNewProductModelList.indexOf(model));
                                wishlistModel.setTotalRatings(task.getResult().getLong("total_ratings"));
                                wishlistModel.setRating(task.getResult().getString("average_ratings"));
                                wishlistModel.setProductTitle(task.getResult().getString("product_title"));
                                wishlistModel.setProductPrice(task.getResult().getString("product_price"));
                                wishlistModel.setProductImage(task.getResult().getString("product_image_1"));
                                wishlistModel.setFreecoupons(task.getResult().getLong("free_coupons"));
                                wishlistModel.setCuttedPrice(task.getResult().getString("cutted_price"));
                                wishlistModel.setCOD(task.getResult().getBoolean("COD"));
                                wishlistModel.setInStock(task.getResult().getLong("stock_quantity") > 0);
                                if (horizontalNewProductModelList.indexOf(model) == horizontalNewProductModelList.size() - 1) {
                                    if (horizontalProductRecyclerView.getAdapter() != null) {
                                        horizontalProductRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            if(horizontalNewProductModelList.size() > 8) {
                btnViewAll.setVisibility(View.VISIBLE);
                btnViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewAllActivity.wishlistModelList = viewAllProductList;
                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 0);
                        viewAllIntent.putExtra("title", horizontalLayoutTitle);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });
            } else {
                btnViewAll.setVisibility(View.INVISIBLE);
            }

            HorizontalNewProductAdapter horizontalNewProductAdapter = new HorizontalNewProductAdapter(horizontalNewProductModelList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            horizontalProductRecyclerView.setLayoutManager(layoutManager);

            horizontalProductRecyclerView.setAdapter(horizontalNewProductAdapter);
            horizontalNewProductAdapter.notifyDataSetChanged();
        }
    }

    public class GridTopProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout gridProductContainer;
        private TextView gridProductLayoutTitle;
        private Button btnGridProductViewAll;
        private GridLayout gridProductLayout;

        public GridTopProductViewHolder(@NonNull View itemView) {
            super(itemView);

            gridProductContainer = itemView.findViewById(R.id.product_container);
            gridProductLayoutTitle = itemView.findViewById(R.id.tv_grid_product_layout_title);
            btnGridProductViewAll = itemView.findViewById(R.id.btn_grid_product_view_all);
            gridProductLayout = itemView.findViewById(R.id.grid_layout_product);
        }

        private void setGridProductLayout(final List<HorizontalNewProductModel> horizontalNewProductModelList, final String gridLayoutTitle, String color) {
            gridProductContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridProductLayoutTitle.setText(gridLayoutTitle);

            for(final HorizontalNewProductModel model : horizontalNewProductModelList) {
                if (!model.getProductID().isEmpty() && model.getProductTitle().isEmpty()) {
                    firebaseFirestore.collection("PRODUCTS").document(model.getProductID())
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                model.setProductTitle(task.getResult().getString("product_title"));
                                model.setProductImage(task.getResult().getString("product_image_1"));
                                model.setProductPrice(task.getResult().getString("product_price"));

                                if (horizontalNewProductModelList.indexOf(model) == horizontalNewProductModelList.size() - 1) {
                                    setGridData(gridLayoutTitle, horizontalNewProductModelList);
                                    if (!gridLayoutTitle.equals("")) {
                                        btnGridProductViewAll.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                ViewAllActivity.horizontalNewProductModelList = horizontalNewProductModelList;
                                                Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                                                viewAllIntent.putExtra("layout_code", 1);
                                                viewAllIntent.putExtra("title", gridLayoutTitle);
                                                itemView.getContext().startActivity(viewAllIntent);
                                            }
                                        });
                                    }
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
            setGridData(gridLayoutTitle, horizontalNewProductModelList);
        }

        private void setGridData(String title, final List<HorizontalNewProductModel> horizontalNewProductModelList) {
            for (int x = 0;x < 4;x++) {
                ImageView productImage = gridProductLayout.getChildAt(x).findViewById(R.id.h_n_product_image);
                TextView productTitle = gridProductLayout.getChildAt(x).findViewById(R.id.h_n_product_title);
                TextView productDescription = gridProductLayout.getChildAt(x).findViewById(R.id.h_n_product_description);
                TextView productPrice = gridProductLayout.getChildAt(x).findViewById(R.id.h_n_product_price);

                Glide.with(itemView.getContext()).load(horizontalNewProductModelList.get(x).getProductImage()).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(productImage);
                productTitle.setText(horizontalNewProductModelList.get(x).getProductTitle());
                productPrice.setText("BDT. "+horizontalNewProductModelList.get(x).getProductPrice()+"/-");

                gridProductLayout.getChildAt(x).setBackgroundColor(Color.parseColor("#FFFFFF"));

                if (!title.equals("")) {
                    final int finalX = x;
                    gridProductLayout.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                            productDetailsIntent.putExtra("PRODUCT_ID", horizontalNewProductModelList.get(finalX).getProductID());
                            itemView.getContext().startActivity(productDetailsIntent);
                        }
                    });
                }
            }
        }
    }
}
