package com.example.sohoz_bazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sohoz_bazar.DBqueries.cartItemModelList;
import static com.example.sohoz_bazar.DBqueries.cartList;
import static com.example.sohoz_bazar.DBqueries.loadAddresses;
import static com.example.sohoz_bazar.DBqueries.loadCartList;
import static com.example.sohoz_bazar.DBqueries.loadRatingList;
import static com.example.sohoz_bazar.DBqueries.loadWishlist;
import static com.example.sohoz_bazar.DBqueries.myRatedIds;
import static com.example.sohoz_bazar.DBqueries.myRating;
import static com.example.sohoz_bazar.DBqueries.removeFromWishlist;
import static com.example.sohoz_bazar.DBqueries.wishList;
import static com.example.sohoz_bazar.DBqueries.wishlistModelList;
import static com.example.sohoz_bazar.HomeActivity.showCart;
import static com.example.sohoz_bazar.RegisterActivity.setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_rating_query = false;
    public static boolean running_cart_query = false;

    public static Activity productDetailsActivity;

    public static boolean fromSearch = false;

    private ViewPager productImagesViewPager;
    private TabLayout productImagesIndicator;

    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;
    private ConstraintLayout productDetailsOnly;
    private ConstraintLayout productDetailsTabs;
    private TextView productOnlyDescriptionBody;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productOtherDetails;

    private TextView productTitle;
    private TextView averageRating;
    private TextView totalRatings;
    private TextView productPrice;
    private String productOriginalPrice;
    private TextView cuttedPrice;
    private ImageView codIndicator;
    private TextView tvCodIndicator;

    private LinearLayout couponRedemptionLayout;
    private Button btnCouponRedemption;

    private TextView rewardTitle;
    private TextView rewardBody;

    ////////coupon redemption dialog/////////
    private TextView couponTitle;
    private TextView couponValidity;
    private TextView couponBody;
    private RecyclerView couponsRecyclerView;
    private LinearLayout selectedCouponContainer;
    private TextView originalPrice;
    private TextView discountPrice;
    ////////coupon redemption dialog/////////

    ///////Sign in Dialog/////////
    private Dialog signInDialog;
    ///////Sign in Dialog/////////

    private Dialog loadingDialog;

    ////////Rating Layout////////
    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView tvTotalRatings;
    private LinearLayout ratingsNoContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView tvAverageRating;
    ///////Rating Layout////////

    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static FloatingActionButton btnAddToWishlist;

    public static boolean ALREADY_ADDED_TO_CART = false;

    private Button btnBuyNow;
    private LinearLayout btnAddToCart;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseUser currentUser;

    private DocumentSnapshot documentSnapshot;

    public static String productID;

    public static MenuItem cartItem;

    private TextView badgeCount;

    private boolean inStock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Product Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productImagesViewPager = findViewById(R.id.product_images_view_pager);
        productImagesIndicator = findViewById(R.id.product_images_indicator);
        btnAddToWishlist = findViewById(R.id.btnAddToWishlist);
        productTitle = findViewById(R.id.product_title);
        averageRating = findViewById(R.id.tv_product_rating_mini_view);
        totalRatings = findViewById(R.id.total_ratings_mini_view);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        codIndicator = findViewById(R.id.cod_indicator_img_view);
        tvCodIndicator = findViewById(R.id.tv_cod_indicator);
        btnCouponRedemption = findViewById(R.id.btn_coupen_redemption);
        couponRedemptionLayout = findViewById(R.id.coupen_redemption_container);

        productDetailsViewPager = findViewById(R.id.product_details_view_pager);
        productDetailsTabLayout = findViewById(R.id.product_details_tab_layout);
        productDetailsOnly = findViewById(R.id.product_details_container);
        productDetailsTabs = findViewById(R.id.product_details_tabs_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);

        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);

        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);

        tvTotalRatings = findViewById(R.id.total_ratings);
        ratingsNoContainer = findViewById(R.id.ratings_number_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        tvAverageRating = findViewById(R.id.average_rating);

        initialRating = -1;

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        ////////coupon redemption dialog/////////

        final Dialog couponRedemptionDialog = new Dialog(ProductDetailsActivity.this);
        couponRedemptionDialog.setContentView(R.layout.coupon_redemption_dialog_layout);
        couponRedemptionDialog.setCancelable(true);
        couponRedemptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView toggleRecyclerView = couponRedemptionDialog.findViewById(R.id.toggle_recycler_view);
        couponsRecyclerView = couponRedemptionDialog.findViewById(R.id.coupons_recycler_view);
        selectedCouponContainer = couponRedemptionDialog.findViewById(R.id.selected_coupon_container);
        couponTitle = couponRedemptionDialog.findViewById(R.id.coupon_title);
        couponValidity = couponRedemptionDialog.findViewById(R.id.coupon_validity);
        couponBody = couponRedemptionDialog.findViewById(R.id.coupon_body);
        originalPrice = couponRedemptionDialog.findViewById(R.id.original_price);
        discountPrice = couponRedemptionDialog.findViewById(R.id.discount_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        couponsRecyclerView.setLayoutManager(layoutManager);

        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogRecyclerView();
            }
        });

        ////////coupon redemption dialog/////////

        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();

        productID = getIntent().getStringExtra("PRODUCT_ID");

        firebaseFirestore.collection("PRODUCTS").document(productID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            documentSnapshot = task.getResult();

                            firebaseFirestore.collection("PRODUCTS").document(productID)
                                    .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {

                                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                                }
                                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                                productImagesViewPager.setAdapter(productImagesAdapter);

                                                productTitle.setText(documentSnapshot.get("product_title").toString());
                                                averageRating.setText(documentSnapshot.get("average_ratings").toString());
                                                totalRatings.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");
                                                productPrice.setText("BDT. " + documentSnapshot.get("product_price").toString() + "/-");

                                                ///////////////for coupon dialog//////////////
                                                originalPrice.setText(productPrice.getText());
                                                productOriginalPrice = documentSnapshot.get("product_price").toString();
                                                RewardAdapter rewardAdapter = new RewardAdapter(DBqueries.rewardModelList, true,couponsRecyclerView, selectedCouponContainer,productOriginalPrice,couponTitle,couponValidity,couponBody,discountPrice);
                                                couponsRecyclerView.setAdapter(rewardAdapter);
                                                rewardAdapter.notifyDataSetChanged();
                                                ///////////////for coupon dialog//////////////

                                                cuttedPrice.setText("BDT. " + documentSnapshot.get("cutted_price").toString() + "/-");

                                                if ((boolean) documentSnapshot.get("COD")) {
                                                    codIndicator.setVisibility(View.VISIBLE);
                                                    tvCodIndicator.setVisibility(View.VISIBLE);
                                                } else {
                                                    codIndicator.setVisibility(View.INVISIBLE);
                                                    tvCodIndicator.setVisibility(View.INVISIBLE);
                                                }

                                                rewardTitle.setText("(" + (long) documentSnapshot.get("free_coupons") + ") " + documentSnapshot.get("free_coupons_title").toString());
                                                rewardBody.setText(documentSnapshot.get("free_coupons_body").toString());

                                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                                    productDetailsOnly.setVisibility(View.GONE);
                                                    productDetailsTabs.setVisibility(View.VISIBLE);
                                                    productDescription = documentSnapshot.get("product_description").toString();
                                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();

                                                    for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {
                                                        productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                                                        for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                                            productSpecificationModelList.add(new ProductSpecificationModel(1,
                                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(),
                                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                                        }
                                                    }
                                                } else {
                                                    productDetailsTabs.setVisibility(View.GONE);
                                                    productDetailsOnly.setVisibility(View.VISIBLE);
                                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                                }

                                                tvTotalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");

                                                for (int x = 0; x < 5; x++) {
                                                    TextView rating = (TextView) ratingsNoContainer.getChildAt(x);
                                                    rating.setText(String.valueOf((long) documentSnapshot.get((5 - x) + "_star")));

                                                    int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                                    ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                    progressBar.setMax(maxProgress);
                                                    progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                                }

                                                totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                                tvAverageRating.setText(documentSnapshot.get("average_ratings").toString());

                                                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(),
                                                        productDetailsTabLayout.getTabCount(), productDescription,
                                                        productOtherDetails, productSpecificationModelList));

                                                if (currentUser != null) {
                                                    if (myRating.size() == 0) {
                                                        loadRatingList(ProductDetailsActivity.this);
                                                    }
                                                    if (cartList.size() == 0) {
                                                        loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
                                                    }
                                                    if (wishList.size() == 0) {
                                                        loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
                                                    }
                                                    if (DBqueries.rewardModelList.size() == 0) {
                                                        DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
                                                    }
                                                    if (cartList.size() != 0 && wishList.size() != 0 && DBqueries.rewardModelList.size() != 0) {
                                                        loadingDialog.dismiss();
                                                    }
                                                } else {
                                                    loadingDialog.dismiss();
                                                }

                                                if (myRatedIds.contains(productID)) {
                                                    int index = myRatedIds.indexOf(productID);
                                                    initialRating = Integer.parseInt(String.valueOf(myRating.get(index))) - 1;
                                                    setRating(initialRating);
                                                }

                                                if (cartList.contains(productID)) {
                                                    ALREADY_ADDED_TO_CART = true;
                                                } else {
                                                    ALREADY_ADDED_TO_CART = false;
                                                }

                                                if (wishList.contains(productID)) {
                                                    ALREADY_ADDED_TO_WISHLIST = true;
                                                    btnAddToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                                } else {
                                                    btnAddToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                                    ALREADY_ADDED_TO_WISHLIST = false;
                                                }

                                                if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")) {
                                                    inStock = true;
                                                    btnBuyNow.setVisibility(View.VISIBLE);
                                                    btnAddToCart.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (currentUser == null) {
                                                                signInDialog.show();
                                                            } else {
                                                                if (!running_cart_query) {
                                                                    running_cart_query = true;
                                                                    if (ALREADY_ADDED_TO_CART) {
                                                                        running_cart_query = false;
                                                                        Toast.makeText(ProductDetailsActivity.this, "Already added to cart!", Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        Map<String, Object> addProduct = new HashMap<>();
                                                                        addProduct.put("product_ID_" + String.valueOf(cartList.size()), productID);
                                                                        addProduct.put("list_size", (long) cartList.size() + 1);

                                                                        firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                                                .document("MY_CART").update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    if (cartItemModelList.size() != 0) {
                                                                                        cartItemModelList.add(0,new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_3").toString(),
                                                                                                documentSnapshot.get("product_title").toString(),
                                                                                                (long) documentSnapshot.get("free_coupons"),
                                                                                                documentSnapshot.get("product_price").toString(),
                                                                                                documentSnapshot.get("cutted_price").toString(),
                                                                                                (long) 1,
                                                                                                (long)documentSnapshot.get("max_quantity"),
                                                                                                (long)documentSnapshot.get("stock_quantity"),
                                                                                                (long) documentSnapshot.get("offers_applied"), (long) 0,
                                                                                                inStock,(boolean)documentSnapshot.get("COD")));
                                                                                    }

                                                                                    ALREADY_ADDED_TO_CART = true;
                                                                                    cartList.add(productID);
                                                                                    Toast.makeText(ProductDetailsActivity.this, "Added To Cart Successfully!", Toast.LENGTH_SHORT).show();
                                                                                    invalidateOptionsMenu();
                                                                                    running_cart_query = false;

                                                                                } else {
                                                                                    running_cart_query = false;
                                                                                    String error = task.getException().getMessage();
                                                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    inStock = false;
                                                    btnBuyNow.setVisibility(View.GONE);
                                                    TextView outOfStock = (TextView) btnAddToCart.getChildAt(0);
                                                    outOfStock.setText("Out Of Stock");
                                                    outOfStock.setTextColor(getResources().getColor(R.color.red));
                                                    outOfStock.setCompoundDrawables(null,null,null,null);
                                                }
                                            } else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        productImagesIndicator.setupWithViewPager(productImagesViewPager, true);

        btnAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        if (ALREADY_ADDED_TO_WISHLIST) {
                            int index = wishList.indexOf(productID);
                            removeFromWishlist(index, ProductDetailsActivity.this);
                            btnAddToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                        } else {
                            btnAddToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(wishList.size()), productID);
                            addProduct.put("list_size", (long) wishList.size() + 1);

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                    .document("MY_WISHLIST").update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (wishlistModelList.size() != 0) {
                                            wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_3").toString(),
                                                    documentSnapshot.get("product_title").toString(),
                                                    (long) documentSnapshot.get("free_coupons"),
                                                    documentSnapshot.get("average_ratings").toString(),
                                                    (long) documentSnapshot.get("total_ratings"),
                                                    documentSnapshot.get("product_price").toString(),
                                                    documentSnapshot.get("cutted_price").toString(),
                                                    (boolean) documentSnapshot.get("COD"),
                                                    inStock));
                                        }

                                        ALREADY_ADDED_TO_WISHLIST = true;
                                        btnAddToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.red));
                                        wishList.add(productID);
                                        Toast.makeText(ProductDetailsActivity.this, "Added To Wishlist Successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        btnAddToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }

                                    running_wishlist_query = false;
                                }
                            });
                        }
                    }
                }
            }
        });

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));

        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ////////Rating Layout////////

        rateNowContainer = findViewById(R.id.rate_now_container);

        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;

            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != initialRating) {
                            if (!running_rating_query) {
                                running_rating_query = true;
                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();

                                if (myRatedIds.contains(productID)) {
                                    TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                    TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);

                                    updateRating.put(initialRating + 1 + "_star", Long.parseLong(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Long.parseLong(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_ratings", calculateAverageRating((long) starPosition - initialRating, true));

                                } else {
                                    updateRating.put(starPosition + 1 + "_star", (long) documentSnapshot.get(starPosition + 1 + "_star") + 1);
                                    updateRating.put("average_ratings", calculateAverageRating((long) starPosition + 1, false));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);

                                }

                                firebaseFirestore.collection("PRODUCTS").document(productID).update(updateRating)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Map<String, Object> rating = new HashMap<>();
                                                    if (myRatedIds.contains(productID)) {
                                                        rating.put("rating_" + myRatedIds.indexOf(productID), (long) starPosition + 1);
                                                    } else {
                                                        rating.put("list_size", (long) myRatedIds.size() + 1);
                                                        rating.put("product_ID_" + myRatedIds.size(), productID);
                                                        rating.put("rating_" + myRatedIds.size(), (long) starPosition + 1);
                                                    }

                                                    firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA")
                                                            .document("MY_RATINGS").update(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                if (myRatedIds.contains(productID)) {

                                                                    myRating.set(myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                                    TextView oldRating = (TextView) ratingsNoContainer.getChildAt(5 - initialRating - 1);
                                                                    TextView finalRating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                                    oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                                    finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));

                                                                } else {
                                                                    myRatedIds.add(productID);
                                                                    myRating.add((long) starPosition + 1);

                                                                    TextView rating = (TextView) ratingsNoContainer.getChildAt(5 - starPosition - 1);
                                                                    rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));

                                                                    totalRatings.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                                    tvTotalRatings.setText((long) documentSnapshot.get("total_ratings") + 1 + " ratings");
                                                                    totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));

                                                                    Toast.makeText(ProductDetailsActivity.this, "Thank you for Rating!", Toast.LENGTH_SHORT).show();
                                                                }

                                                                for (int x = 0; x < 5; x++) {
                                                                    TextView ratingFigures = (TextView) ratingsNoContainer.getChildAt(x);
                                                                    ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                                    int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                                    progressBar.setMax(maxProgress);
                                                                    progressBar.setProgress(Integer.parseInt(ratingFigures.getText().toString()));
                                                                }

                                                                initialRating = starPosition;

                                                                averageRating.setText(calculateAverageRating(0, true));
                                                                tvAverageRating.setText(calculateAverageRating(0, true));

                                                                if (wishList.contains(productID) && wishlistModelList.size() != 0) {
                                                                    int index = wishList.indexOf(productID);
                                                                    wishlistModelList.get(index).setRating(averageRating.getText().toString());
                                                                    wishlistModelList.get(index).setTotalRatings(Long.parseLong(totalRatingsFigure.getText().toString()));
                                                                }

                                                            } else {
                                                                setRating(initialRating);
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            running_rating_query = false;
                                                        }
                                                    });
                                                } else {
                                                    running_rating_query = false;
                                                    setRating(initialRating);
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                }
            });
        }

        ////////Rating Layout////////

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();
                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.CART_ITEM, productID, documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            (long) documentSnapshot.get("free_coupons"),
                            documentSnapshot.get("product_price").toString(),
                            documentSnapshot.get("cutted_price").toString(),
                            (long) 1,
                            (long)documentSnapshot.get("max_quantity"),
                            (long)documentSnapshot.get("stock_quantity"),
                            (long) documentSnapshot.get("offers_applied"), (long) 0,
                            inStock,(boolean)documentSnapshot.get("COD")));

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBqueries.addressesModelList.size() == 0) {
                        loadAddresses(ProductDetailsActivity.this, loadingDialog, true);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }
                }
            }
        });

        btnCouponRedemption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponRedemptionDialog.show();
            }
        });

        ////////Sign In Dialog///////

        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog_layout);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btnCartSignIn = signInDialog.findViewById(R.id.btn_cart_sign_in);
        Button btnCartSignUp = signInDialog.findViewById(R.id.btn_cart_sign_up);

        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        btnCartSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disabledCloseBtn = true;
                SignUpFragment.disabledCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        btnCartSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disabledCloseBtn = true;
                SignUpFragment.disabledCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });

        ///////Sign In Dialog///////
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            couponRedemptionLayout.setVisibility(View.GONE);
        } else {
            couponRedemptionLayout.setVisibility(View.VISIBLE);
        }

        if (currentUser != null) {
            if (myRating.size() == 0) {
                loadRatingList(ProductDetailsActivity.this);
            }
            if (wishList.size() == 0) {
                loadWishlist(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (DBqueries.rewardModelList.size() == 0) {
                DBqueries.loadRewards(ProductDetailsActivity.this, loadingDialog, false);
            }
            if (cartList.size() != 0 && wishList.size() != 0 && DBqueries.rewardModelList.size() != 0) {
                loadingDialog.dismiss();
            }
        } else {
            loadingDialog.dismiss();
        }

        if (myRatedIds.contains(productID)) {
            int index = myRatedIds.indexOf(productID);
            initialRating = Integer.parseInt(String.valueOf(myRating.get(index))) - 1;
            setRating(initialRating);
        }

        if (cartList.contains(productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_CART = false;
        }

        if (wishList.contains(productID)) {
            ALREADY_ADDED_TO_WISHLIST = true;
            btnAddToWishlist.setSupportImageTintList(getResources().getColorStateList(R.color.red));
        } else {
            btnAddToWishlist.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISHLIST = false;
        }
        invalidateOptionsMenu();
    }

    private void showDialogRecyclerView() {
        if (couponsRecyclerView.getVisibility() == View.GONE) {
            selectedCouponContainer.setVisibility(View.GONE);
            couponsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            couponsRecyclerView.setVisibility(View.GONE);
            selectedCouponContainer.setVisibility(View.VISIBLE);
        }
    }

    ////////Rating Layout////////

    public static void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    private String calculateAverageRating(long currentUserRatings, boolean update) {
        Double totalStars = Double.valueOf(0);
        for (int x = 1; x < 6; x++) {
            TextView ratingNo = (TextView) ratingsNoContainer.getChildAt(5 - x);
            totalStars = totalStars + (Long.parseLong(ratingNo.getText().toString()) * x);
        }
        totalStars = totalStars + currentUserRatings;
        if (update) {
            return String.valueOf(totalStars / Long.parseLong(totalRatingsFigure.getText().toString())).substring(0, 3);
        } else {
            return String.valueOf(totalStars / (Long.parseLong(totalRatingsFigure.getText().toString()) + 1)).substring(0, 3);
        }
    }

    ////////Rating Layout////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_details_search_and_cart_icon, menu);

        cartItem = menu.findItem(R.id.home_cart_icon);
        cartItem.setActionView(R.layout.badge_layout);
        ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.ic_shopping_cart_black_24dp);
        badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {
            if (cartList.size() == 0) {
                loadCartList(ProductDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductDetailsActivity.this));
            }else {
                badgeCount.setVisibility(View.VISIBLE);
                if (cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(cartList.size()));
                } else {
                    badgeCount.setText("99");
                }
            }
        }

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.home_search_icon) {
            if (fromSearch) {
                finish();
            } else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (id == R.id.home_cart_icon) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch = false;
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}
