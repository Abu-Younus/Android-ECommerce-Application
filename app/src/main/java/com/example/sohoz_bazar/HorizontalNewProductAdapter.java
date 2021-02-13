package com.example.sohoz_bazar;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HorizontalNewProductAdapter extends RecyclerView.Adapter<HorizontalNewProductAdapter.ViewHolder> {

    private List<HorizontalNewProductModel> horizontalNewProductModelList;

    public HorizontalNewProductAdapter(List<HorizontalNewProductModel> horizontalNewProductModelList) {
        this.horizontalNewProductModelList = horizontalNewProductModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_new_product_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String resource = horizontalNewProductModelList.get(position).getProductImage();
        String title = horizontalNewProductModelList.get(position).getProductTitle();
        String price = horizontalNewProductModelList.get(position).getProductPrice();
        String productId = horizontalNewProductModelList.get(position).getProductID();

        holder.setData(productId,resource, title, price);
    }

    @Override
    public int getItemCount() {
        if (horizontalNewProductModelList.size() > 8) {
            return 8;
        } else {
            return horizontalNewProductModelList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productTitle;
        private TextView productDescription;
        private TextView productPrice;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.h_n_product_image);
            productTitle = itemView.findViewById(R.id.h_n_product_title);
            productPrice = itemView.findViewById(R.id.h_n_product_price);
        }

        private void setData(final String productId, String resource, String title, String price) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(productImage);
            productTitle.setText(title);
            productPrice.setText("BDT. "+price+"/-");

            if (!title.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);
                        productDetailsIntent.putExtra("PRODUCT_ID", productId);
                        itemView.getContext().startActivity(productDetailsIntent);
                    }
                });
            }
        }
    }
}
