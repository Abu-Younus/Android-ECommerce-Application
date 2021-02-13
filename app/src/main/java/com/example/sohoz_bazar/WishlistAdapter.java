package com.example.sohoz_bazar;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import static com.example.sohoz_bazar.DBqueries.removeFromWishlist;
import static com.example.sohoz_bazar.ProductDetailsActivity.running_wishlist_query;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private boolean fromSearch;
    private List<WishlistModel> wishlistModelList;
    private Boolean wishlist;
    private int lastPosition = -1;

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productID = wishlistModelList.get(position).getProductID();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        long freeCoupons = wishlistModelList.get(position).getFreecoupons();
        String rating = wishlistModelList.get(position).getRating();
        long totalRatings = wishlistModelList.get(position).getTotalRatings();
        String productPrice = wishlistModelList.get(position).getProductPrice();
        String cuttedPrice = wishlistModelList.get(position).getCuttedPrice();
        boolean paymentMethod = wishlistModelList.get(position).isCOD();
        boolean inStock = wishlistModelList.get(position).isInStock();

        holder.setData(productID,resource,title,freeCoupons,rating,totalRatings,productPrice,cuttedPrice,paymentMethod, position, inStock);

        if (lastPosition < position) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupons;
        private ImageView couponIcon;
        private TextView rating;
        private TextView totalRatings;
        private TextView productPrice;
        private TextView cuttedPrice;
        private View priceCut;
        private TextView paymentMethod;
        private ImageButton btnDeleteWishlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            couponIcon = itemView.findViewById(R.id.free_coupon_icon);
            rating = itemView.findViewById(R.id.tv_product_rating_mini_view);
            totalRatings = itemView.findViewById(R.id.total_ratings);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            priceCut = itemView.findViewById(R.id.price_cut_divider);
            paymentMethod = itemView.findViewById(R.id.payment_method);
            btnDeleteWishlist = itemView.findViewById(R.id.btn_delete_wishlist);
        }

        private void setData(final String productID, String resource, String title, long freeCouponNo, String ratingAverage, long totalRatingNo, String productPriceText, String cuttedPriceText, boolean COD, final int index, boolean inStock) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(productImage);
            productTitle.setText(title);

            if (freeCouponNo > 0 && inStock) {
                couponIcon.setVisibility(View.VISIBLE);
                freeCoupons.setVisibility(View.VISIBLE);
                if (freeCouponNo == 1) {
                    freeCoupons.setText("free "+freeCouponNo+" coupon");
                } else {
                    freeCoupons.setText("free "+freeCouponNo+" coupons");
                }
            } else {
                couponIcon.setVisibility(View.INVISIBLE);
                freeCoupons.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout = (LinearLayout) rating.getParent();
            if (inStock) {
                linearLayout.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                cuttedPrice.setVisibility(View.VISIBLE);

                rating.setText(ratingAverage);
                totalRatings.setText("(" + totalRatingNo + ")ratings");
                productPrice.setText("BDT. " + productPriceText + "/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("BDT. " + cuttedPriceText + "/-");

                if (COD) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            } else {
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }

            if (wishlist) {
                btnDeleteWishlist.setVisibility(View.VISIBLE);
            } else {
                btnDeleteWishlist.setVisibility(View.GONE);
            }

            btnDeleteWishlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!running_wishlist_query) {
                        running_wishlist_query = true;
                        removeFromWishlist(index, itemView.getContext());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fromSearch) {
                        ProductDetailsActivity.fromSearch = true;
                    }
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID", productID);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}
