package com.example.sohoz_bazar;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.sohoz_bazar.ProductDetailsActivity.running_cart_query;

public class CartAdapter extends RecyclerView.Adapter {

    private List<CartItemModel> cartItemModelList;

    private int lastPosition = -1;

    private TextView cartTotalAmount;

    private boolean showDeleteBtn;

    public CartAdapter(List<CartItemModel> cartItemModelList, TextView cartTotalAmount, boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount = cartTotalAmount;
        this.showDeleteBtn = showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch (cartItemModelList.get(position).getType()) {
            case 0:
                return CartItemModel.CART_ITEM;

            case 1:
                return CartItemModel.TOTAL_AMOUNT;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
                return new CartItemViewHolder(cartItemView);

            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalAmountView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout, parent, false);
                return  new CartTotalAmountViewHolder(cartTotalAmountView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (cartItemModelList.get(position).getType()) {
            case CartItemModel.CART_ITEM:
                String productID = cartItemModelList.get(position).getProductID();
                String productImage = cartItemModelList.get(position).getProductImage();
                String title = cartItemModelList.get(position).getProductTitle();
                Long freecoupons = cartItemModelList.get(position).getFreeCoupons();
                String productPrice = cartItemModelList.get(position).getProductPrice();
                String cuttedPrice = cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied = cartItemModelList.get(position).getOffersApplied();
                boolean inStock = cartItemModelList.get(position).isInStock();
                Long productQuantity = cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity = cartItemModelList.get(position).getMaxQuantity();
                boolean quantityError = cartItemModelList.get(position).isQtyError();
                List<String> qtyIds = cartItemModelList.get(position).getQtyIDs();
                long stockQty = cartItemModelList.get(position).getStockQuantity();
                boolean COD = cartItemModelList.get(position).isCOD();
                ((CartItemViewHolder)holder).setItemDetails(productID,productImage,title,freecoupons,productPrice,cuttedPrice,offersApplied,position,inStock, String.valueOf(productQuantity),maxQuantity,quantityError,qtyIds,stockQty,COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems  = 0;
                int totalItemPrice = 0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount = 0;
                for (int x = 0;x < cartItemModelList.size();x++) {
                    if (cartItemModelList.get(x).getType() == CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()) {
                        int quantity = Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems = totalItems + quantity;
                        if (TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        } else {
                            totalItemPrice = totalItemPrice + Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }
                        if (!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())) {
                            savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getCuttedPrice()) - Integer.parseInt(cartItemModelList.get(x).getProductPrice())) * quantity;
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        } else {
                            if (!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCouponId())) {
                                savedAmount = savedAmount + (Integer.parseInt(cartItemModelList.get(x).getProductPrice()) - Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())) * quantity;
                            }
                        }
                    }
                }
                if (totalItemPrice > 500) {
                    deliveryPrice = "FREE";
                    totalAmount = totalItemPrice;
                } else {
                    deliveryPrice = "60";
                    totalAmount = totalItemPrice + 60;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemsPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewHolder)holder).setCartTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);
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
        return cartItemModelList.size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private ImageView couponsIcon;
        private TextView freeCoupons;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView offersApplied;
        private TextView couponsApplied;
        private TextView productQuantity;
        private LinearLayout btnRemoveCartItem;
        private LinearLayout couponRedemptionConatiner;
        private TextView tvCouponRedemption;
        private Button btnCouponRedemption;
        private ImageView codIndicator;

        ////////coupon redemption dialog/////////
        private TextView couponTitle;
        private TextView couponValidity;
        private TextView couponBody;
        private RecyclerView couponsRecyclerView;
        private LinearLayout selectedCouponContainer;
        private TextView originalPrice;
        private TextView discountPrice;
        private LinearLayout applyOrRemoveBtnContainer;
        private TextView couponFooterText;
        private Button btnCouponRemove, btnCouponApply;
        private String productOriginalPrice;
        ////////coupon redemption dialog/////////

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            couponsIcon = itemView.findViewById(R.id.free_coupon_icon);
            freeCoupons = itemView.findViewById(R.id.tv_free_coupon);
            productPrice = itemView.findViewById(R.id.product_price);
            cuttedPrice = itemView.findViewById(R.id.cutted_price);
            offersApplied = itemView.findViewById(R.id.offers_applied);
            couponsApplied = itemView.findViewById(R.id.coupons_applied);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            btnRemoveCartItem = itemView.findViewById(R.id.btn_remove_cart_item);
            couponRedemptionConatiner = itemView.findViewById(R.id.coupen_redemption_container);
            tvCouponRedemption = itemView.findViewById(R.id.tv_coupen_redemption);
            btnCouponRedemption = itemView.findViewById(R.id.btn_coupen_redemption);
            codIndicator = itemView.findViewById(R.id.cod_indicator_img_view);
        }

        private void setItemDetails(final String productID, String resource, String title, Long freeCouponsNo, final String productPriceText, String cuttedPriceText, Long offersAppliedNo, final int position, boolean inStock, final String productQuantityText, final Long maxQuantity, boolean quantityError, final List<String> qtyIds, final long stockQty, boolean COD) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(productImage);
            productTitle.setText(title);

            final Dialog couponRedemptionDialog = new Dialog(itemView.getContext());
            couponRedemptionDialog.setContentView(R.layout.coupon_redemption_dialog_layout);
            couponRedemptionDialog.setCancelable(false);
            couponRedemptionDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if (COD) {
                codIndicator.setVisibility(View.VISIBLE);
            } else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if (inStock) {
                if (freeCouponsNo > 0) {
                    couponsIcon.setVisibility(View.VISIBLE);
                    freeCoupons.setVisibility(View.VISIBLE);
                    if(freeCouponsNo == 1) {
                        freeCoupons.setText("free "+freeCouponsNo+" coupon");
                    } else {
                        freeCoupons.setText("free "+freeCouponsNo+" coupons");
                    }
                } else {
                    couponsIcon.setVisibility(View.INVISIBLE);
                    freeCoupons.setVisibility(View.INVISIBLE);
                }

                productPrice.setText("BDT. "+productPriceText+"/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("BDT. "+cuttedPriceText+"/-");
                couponRedemptionConatiner.setVisibility(View.VISIBLE);

                ////////coupon redemption dialog/////////

                ImageView toggleRecyclerView = couponRedemptionDialog.findViewById(R.id.toggle_recycler_view);
                couponsRecyclerView = couponRedemptionDialog.findViewById(R.id.coupons_recycler_view);
                selectedCouponContainer = couponRedemptionDialog.findViewById(R.id.selected_coupon_container);
                couponTitle = couponRedemptionDialog.findViewById(R.id.coupon_title);
                couponValidity = couponRedemptionDialog.findViewById(R.id.coupon_validity);
                couponBody = couponRedemptionDialog.findViewById(R.id.coupon_body);
                originalPrice = couponRedemptionDialog.findViewById(R.id.original_price);
                discountPrice = couponRedemptionDialog.findViewById(R.id.discount_price);
                applyOrRemoveBtnContainer = couponRedemptionDialog.findViewById(R.id.apply_or_remove_btn_container);
                couponFooterText = couponRedemptionDialog.findViewById(R.id.coupon_footer_text);
                btnCouponRemove = couponRedemptionDialog.findViewById(R.id.btn_coupon_remove);
                btnCouponApply = couponRedemptionDialog.findViewById(R.id.btn_coupon_apply);

                couponFooterText.setVisibility(View.GONE);
                applyOrRemoveBtnContainer.setVisibility(View.VISIBLE);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                couponsRecyclerView.setLayoutManager(layoutManager);

                ///////////////for coupon dialog//////////////
                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productPriceText;
                RewardAdapter rewardAdapter = new RewardAdapter(position, DBqueries.rewardModelList, true,couponsRecyclerView, selectedCouponContainer,productOriginalPrice,couponTitle,couponValidity,couponBody,discountPrice,cartItemModelList);
                couponsRecyclerView.setAdapter(rewardAdapter);
                rewardAdapter.notifyDataSetChanged();
                ///////////////for coupon dialog//////////////

                btnCouponApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    couponRedemptionConatiner.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                                    tvCouponRedemption.setText(rewardModel.getBody());
                                    tvCouponRedemption.setTextSize(9f);
                                    btnCouponRedemption.setText("Coupon");
                                }
                            }
                            couponsApplied.setVisibility(View.VISIBLE);
                            cartItemModelList.get(position).setDiscountedPrice(discountPrice.getText().toString().substring(5,discountPrice.getText().length() - 2));
                            productPrice.setText(discountPrice.getText());
                            String couponDiscountedAmount = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(discountPrice.getText().toString().substring(5,discountPrice.getText().length() - 2)));
                            couponsApplied.setText("Coupon applied - BDT. " + couponDiscountedAmount + "/-");
                            notifyItemChanged(cartItemModelList.size() - 1);
                            couponRedemptionDialog.dismiss();
                        }
                    }
                });

                btnCouponRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        couponTitle.setText("Coupon");
                        couponValidity.setText("Validity");
                        couponBody.setText("Tap the icon on the top right corner to select your coupon.");
                        couponsApplied.setVisibility(View.INVISIBLE);
                        couponRedemptionConatiner.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.couponContainer));
                        tvCouponRedemption.setText("Apply your coupon here.");
                        btnCouponRedemption.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCouponId(null);
                        productPrice.setText("BDT. "+productPriceText+"/-");
                        notifyItemChanged(cartItemModelList.size() - 1);
                        couponRedemptionDialog.dismiss();
                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialogRecyclerView();
                    }
                });

                if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            couponRedemptionConatiner.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                            tvCouponRedemption.setText(rewardModel.getBody());
                            btnCouponRedemption.setText("Coupon");
                            if (rewardModel.getType().equals("Discount")) {
                                couponTitle.setText(rewardModel.getType());
                            } else {
                                couponTitle.setText("Flat BDT. " + rewardModel.getDiscountOrAmount() + " OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            couponValidity.setText("till " + simpleDateFormat.format(rewardModel.getValidity()));
                            couponBody.setText(rewardModel.getBody());
                        }
                    }
                    discountPrice.setText("BDT. "+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    couponsApplied.setVisibility(View.VISIBLE);
                    productPrice.setText("BDT. "+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    String couponDiscountedAmount = String.valueOf(Long.valueOf(productPriceText) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    couponsApplied.setText("Coupon applied - BDT. " + couponDiscountedAmount + "/-");
                } else {
                    couponsApplied.setVisibility(View.INVISIBLE);
                    couponRedemptionConatiner.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.couponContainer));
                    tvCouponRedemption.setText("Apply your coupon here.");
                    btnCouponRedemption.setText("Redeem");
                }

                ////////coupon redemption dialog/////////

                productQuantity.setText("Qty: "+productQuantityText);
                if (!showDeleteBtn) {
                    if (quantityError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(R.color.red)));
                        }
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            productQuantity.setCompoundDrawableTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                        }
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setCancelable(false);
                        quantityDialog.setContentView(R.layout.quantity_dialog_layout);

                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        final EditText txtQuantityNo = quantityDialog.findViewById(R.id.txt_quantity_no);
                        Button btnCancel = quantityDialog.findViewById(R.id.btn_cancel);
                        Button btnOk = quantityDialog.findViewById(R.id.btn_ok);

                        txtQuantityNo.setHint("Max "+String.valueOf(maxQuantity));

                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!TextUtils.isEmpty(txtQuantityNo.getText())) {
                                    if (Long.parseLong(txtQuantityNo.getText().toString()) <= maxQuantity && Long.parseLong(txtQuantityNo.getText().toString()) != 0) {
                                        if (itemView.getContext() instanceof HomeActivity) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(txtQuantityNo.getText().toString()));
                                        } else {
                                            if (DeliveryActivity.fromCart) {
                                                cartItemModelList.get(position).setProductQuantity(Long.valueOf(txtQuantityNo.getText().toString()));
                                            } else {
                                                DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(txtQuantityNo.getText().toString()));
                                            }
                                        }
                                        productQuantity.setText("Qty: " + txtQuantityNo.getText());
                                        notifyItemChanged(cartItemModelList.size() - 1);
                                        if (!showDeleteBtn) {
                                            DeliveryActivity.loadingDialog.show();
                                            DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                            final int initialQty = Integer.parseInt(productQuantityText);
                                            final int finalQty = Integer.parseInt(txtQuantityNo.getText().toString());

                                            final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                            if (finalQty > initialQty) {

                                                for (int y = 0; y < finalQty - initialQty; y++) {
                                                    final String quantityDocumentName = UUID.randomUUID().toString().substring(0, 20);

                                                    Map<String, Object> timeStamp = new HashMap<>();
                                                    timeStamp.put("time", FieldValue.serverTimestamp());
                                                    final int finalY = y;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(quantityDocumentName).set(timeStamp)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.add(quantityDocumentName);
                                                                    if (finalY + 1 == finalQty - initialQty) {
                                                                        firebaseFirestore.collection("PRODUCTS").document(productID)
                                                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                                                .limit(stockQty).get()
                                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            List<String> serverQuantity = new ArrayList<>();
                                                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                                                serverQuantity.add(documentSnapshot.getId());
                                                                                            }
                                                                                            long availableQty = 0;
                                                                                            for (String qtyId : qtyIds) {
                                                                                                if (!serverQuantity.contains(qtyId)) {
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                                    DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQty);
                                                                                                    Toast.makeText(itemView.getContext(), "Sorry! all products may not be available to required quantity.", Toast.LENGTH_SHORT).show();
                                                                                                } else {
                                                                                                    availableQty++;
                                                                                                }
                                                                                            }
                                                                                            DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                                        } else {
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });
                                                }
                                            } else if (initialQty > finalQty){
                                                for (int x = 0;x < initialQty - finalQty;x++) {
                                                    final String qtyId = qtyIds.get(qtyIds.size() - 1 - x);
                                                    final int finalX = x;
                                                    firebaseFirestore.collection("PRODUCTS").document(productID)
                                                            .collection("QUANTITY").document(qtyId).delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    qtyIds.remove(qtyId);
                                                                    DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    if (finalX + 1 == initialQty - finalQty) {
                                                                        DeliveryActivity.loadingDialog.dismiss();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        }

                                    } else {
                                        Toast.makeText(itemView.getContext(), "Max Quantity "+String.valueOf(maxQuantity), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(itemView.getContext(), "Quantity field is empty!", Toast.LENGTH_SHORT).show();
                                }
                                quantityDialog.dismiss();
                            }
                        });

                        quantityDialog.show();
                    }
                });

                if (offersAppliedNo > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmount = String.valueOf(Long.valueOf(cuttedPriceText) - Long.valueOf(productPriceText));
                    offersApplied.setText("Offer applied - BDT. "+offerDiscountedAmount+"/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            } else {
                productPrice.setText("Out Of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.red));
                cuttedPrice.setText("");
                couponRedemptionConatiner.setVisibility(View.GONE);
                freeCoupons.setVisibility(View.INVISIBLE);
                couponsIcon.setVisibility(View.INVISIBLE);
                couponsApplied.setVisibility(View.INVISIBLE);
                offersApplied.setVisibility(View.INVISIBLE);
                productQuantity.setText("Qty: " + 0);
                productQuantity.setTextColor(Color.parseColor("#70000000"));
                productQuantity.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#70000000")));
            }

            if (showDeleteBtn) {
                btnRemoveCartItem.setVisibility(View.VISIBLE);
            } else {
                btnRemoveCartItem.setVisibility(View.GONE);
            }

            btnCouponRedemption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    couponRedemptionDialog.show();
                }
            });

            btnRemoveCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCouponId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCouponId().equals(cartItemModelList.get(position).getSelectedCouponId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                    if (!running_cart_query) {
                        running_cart_query = true;
                        DBqueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });
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
    }

    public class CartTotalAmountViewHolder extends RecyclerView.ViewHolder {

        private TextView totalItems;
        private TextView totalItemsPrice;
        private TextView deliveryPrice;
        private TextView totalAmount;
        private TextView savedAmount;

        public CartTotalAmountViewHolder(@NonNull View itemView) {
            super(itemView);

            totalItems = itemView.findViewById(R.id.total_items);
            totalItemsPrice = itemView.findViewById(R.id.total_items_price);
            deliveryPrice = itemView.findViewById(R.id.delivery_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            savedAmount = itemView.findViewById(R.id.saved_amount);
        }

        private void setCartTotalAmount(int totalItemsText, int totalItemsPriceText, String deliveryPriceText, int totalAmountText, int savedAmountText) {
            totalItems.setText("Price("+totalItemsText+" items)");
            totalItemsPrice.setText("BDT. "+totalItemsPriceText+"/-");
            if (deliveryPriceText.equals("FREE")) {
                deliveryPrice.setText(deliveryPriceText);
            } else {
                deliveryPrice.setText("BDT. "+deliveryPriceText+"/-");
            }
            totalAmount.setText("BDT. "+totalAmountText+"/-");
            cartTotalAmount.setText("BDT. "+totalAmountText+"/-");
            savedAmount.setText("You will saved BDT. "+savedAmountText+"/- on this order.");

            LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
            if (totalItemsPriceText == 0) {
                if (DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size() - 1);
                }
                if (showDeleteBtn) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                parent.setVisibility(View.GONE);
            } else {
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}
