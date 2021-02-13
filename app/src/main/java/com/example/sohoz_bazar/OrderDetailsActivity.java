package com.example.sohoz_bazar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static com.example.sohoz_bazar.DBqueries.myRatedIds;

public class OrderDetailsActivity extends AppCompatActivity {

    private LinearLayout happyShoppingLayout;
    private int position;

    private TextView title,price,quantity;
    private ImageView productImage,orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar o_to_p_progressBar,p_to_s_progressBar,s_to_d_progressBar;
    private TextView orderedTitle,packedTitle,shippedTitle,delveredTitle,orderedDate,packedDate,shippedDate,deliveredDate,orderedBody,packedBody,shippedBody,deliveredBody;
    private LinearLayout rateNowContainer;
    private TextView fullName,address,pincode;
    private TextView totalItems,totalItemsPrice,deliveryPrice,totalPrice,savedAmount;

    private int rating;

    private Dialog loadingDialog, cancelDialog;

    private SimpleDateFormat simpleDateFormat;

    private Button btnCancelOrder, btnChangeOrNewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        happyShoppingLayout = findViewById(R.id.happy_shopping_container);
        title = findViewById(R.id.product_title);
        price = findViewById(R.id.product_price);
        quantity = findViewById(R.id.product_quantity);
        productImage = findViewById(R.id.product_image);
        orderedIndicator = findViewById(R.id.ordered_indicator);
        packedIndicator = findViewById(R.id.packed_indicator);
        shippedIndicator = findViewById(R.id.shipped_indicator);
        deliveredIndicator = findViewById(R.id.delivered_indicator);
        o_to_p_progressBar = findViewById(R.id.ordered_packed_progress);
        p_to_s_progressBar = findViewById(R.id.packed_shipped_progress);
        s_to_d_progressBar = findViewById(R.id.shipped_delivered_progress);
        orderedTitle = findViewById(R.id.ordered_title);
        orderedDate = findViewById(R.id.ordered_date);
        orderedBody = findViewById(R.id.ordered_body);
        packedTitle = findViewById(R.id.packed_title);
        packedDate = findViewById(R.id.packed_date);
        packedBody = findViewById(R.id.packed_body);
        shippedTitle = findViewById(R.id.shipped_title);
        shippedDate = findViewById(R.id.shipped_date);
        shippedBody = findViewById(R.id.shipped_body);
        delveredTitle = findViewById(R.id.delivered_title);
        deliveredDate = findViewById(R.id.delivered_date);
        deliveredBody = findViewById(R.id.delivered_body);
        rateNowContainer = findViewById(R.id.rate_now_container);
        fullName = findViewById(R.id.full_name);
        address = findViewById(R.id.address);
        pincode = findViewById(R.id.pin_code);
        totalItems = findViewById(R.id.total_items);
        totalItemsPrice = findViewById(R.id.total_items_price);
        deliveryPrice = findViewById(R.id.delivery_price);
        totalPrice = findViewById(R.id.total_price);
        savedAmount = findViewById(R.id.saved_amount);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);
        btnChangeOrNewAddress = findViewById(R.id.btn_change_or_add_address);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        /////////Cancel Dialog///////////

        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(false);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /////////Cancel Dialog///////////

        position = getIntent().getIntExtra("Position", -1);
        final MyOrderItemModel model = DBqueries.myOrderItemModelList.get(position);

        title.setText(model.getProductTitle());
        if (model.getDiscountedPrice().equals("")) {
            price.setText("BDT. "+model.getProductPrice()+"/-");
        } else {
            price.setText("BDT. " + model.getDiscountedPrice() + "/-");
        }
        quantity.setText("Qty: "+String.valueOf(model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);

        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM YYYY hh:mm aa");
        switch (model.getOrderStatus()) {
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                o_to_p_progressBar.setVisibility(View.GONE);
                p_to_s_progressBar.setVisibility(View.GONE);
                s_to_d_progressBar.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                delveredTitle.setVisibility(View.GONE);

                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                o_to_p_progressBar.setProgress(100);

                p_to_s_progressBar.setVisibility(View.GONE);
                s_to_d_progressBar.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                delveredTitle.setVisibility(View.GONE);

                break;

            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                o_to_p_progressBar.setProgress(100);
                p_to_s_progressBar.setProgress(100);

                s_to_d_progressBar.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                delveredTitle.setVisibility(View.GONE);

                break;

            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));
                delveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery.");

                o_to_p_progressBar.setProgress(100);
                p_to_s_progressBar.setProgress(100);
                s_to_d_progressBar.setProgress(100);
                break;

            case "Delivered":

                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_to_p_progressBar.setProgress(100);
                p_to_s_progressBar.setProgress(100);
                s_to_d_progressBar.setProgress(100);

                break;

            case "Cancelled":

                if (model.getPackedDate().after(model.getOrderedDate())) {
                    if (model.getShippedDate().after(model.getPackedDate())) {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        delveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled");

                        o_to_p_progressBar.setProgress(100);
                        p_to_s_progressBar.setProgress(100);
                        s_to_d_progressBar.setProgress(100);
                    } else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled.");

                        o_to_p_progressBar.setProgress(100);
                        p_to_s_progressBar.setProgress(100);

                        s_to_d_progressBar.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        delveredTitle.setVisibility(View.GONE);
                    }
                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled.");

                    o_to_p_progressBar.setProgress(100);

                    p_to_s_progressBar.setVisibility(View.GONE);
                    s_to_d_progressBar.setVisibility(View.GONE);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    delveredTitle.setVisibility(View.GONE);
                }

                break;
        }
        loadingDialog.dismiss();
        ////////Rating Layout////////
        rating = model.getRating();
        setRating(rating);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;

            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductID());

                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {

                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                            if (rating != 0) {
                                Long increase = documentSnapshot.getLong(starPosition+1+"_star") + 1;
                                Long decrease = documentSnapshot.getLong(rating+1+"_star") - 1;
                                transaction.update(documentReference,starPosition+1+"_star",increase);
                                transaction.update(documentReference,rating+1+"_star",decrease);
                            } else {
                                Long increase = documentSnapshot.getLong(starPosition+1+"_star") + 1;
                                transaction.update(documentReference,starPosition+1+"_star",increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {

                        @Override
                        public void onSuccess(Object o) {
                            Map<String, Object> rating = new HashMap<>();
                            if (myRatedIds.contains(model.getProductID())) {
                                rating.put("rating_" + myRatedIds.indexOf(model.getProductID()), (long) starPosition + 1);
                            } else {
                                rating.put("list_size", (long) myRatedIds.size() + 1);
                                rating.put("product_ID_" + myRatedIds.size(), model.getProductID());
                                rating.put("rating_" + myRatedIds.size(), (long) starPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                                    .document("MY_RATINGS").update(rating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DBqueries.myOrderItemModelList.get(position).setRating(starPosition);
                                        if (DBqueries.myRatedIds.contains(model.getProductID())) {
                                            DBqueries.myRating.set(DBqueries.myRatedIds.indexOf(model.getProductID()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DBqueries.myRatedIds.add(model.getProductID());
                                            DBqueries.myRating.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        }

        ////////Rating Layout////////

        if (model.isCancellationRequested()) {
            btnCancelOrder.setVisibility(View.VISIBLE);
            btnCancelOrder.setEnabled(false);
            btnCancelOrder.setText("Cancellation in process.");
            btnCancelOrder.setTextColor(getResources().getColor(R.color.red));
            btnCancelOrder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            if (model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")) {
                btnCancelOrder.setVisibility(View.VISIBLE);
                btnCancelOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.findViewById(R.id.btn_no).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });
                        cancelDialog.findViewById(R.id.btn_yes).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String,Object> map = new HashMap<>();
                                map.put("ORDER ID", model.getOrderId());
                                map.put("Product Id", model.getProductID());
                                map.put("Order Cancelled", false);
                                FirebaseFirestore.getInstance().collection("CANCELLED_ORDERS").document().set(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderId())
                                                            .collection("ORDER_ITEMS").document(model.getProductID())
                                                            .update("Cancellation Requested",true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                model.setCancellationRequested(true);
                                                                btnCancelOrder.setEnabled(false);
                                                                btnCancelOrder.setText("Cancellation in process.");
                                                                btnCancelOrder.setTextColor(getResources().getColor(R.color.red));
                                                                btnCancelOrder.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                                                            } else {
                                                                String error = task.getException().getMessage();
                                                                Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                                } else {
                                                    loadingDialog.dismiss();
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(OrderDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                        cancelDialog.show();
                    }
                });
            }
        }

        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());

        totalItems.setText("Price("+model.getProductQuantity()+" items)");
        Long totalItemsPriceValue;
        if (model.getDiscountedPrice().equals("")) {
            totalItemsPriceValue = model.getProductQuantity()*Long.valueOf(model.getProductPrice());
            totalItemsPrice.setText("BDT. "+totalItemsPriceValue+"/-");
        } else {
            totalItemsPriceValue = model.getProductQuantity()*Long.valueOf(model.getDiscountedPrice());
            totalItemsPrice.setText("BDT. "+totalItemsPriceValue+"/-");
        }
        if (model.getDeliveryPrice().equals("FREE")) {
            deliveryPrice.setText(model.getDeliveryPrice());
            totalPrice.setText(totalItemsPrice.getText());
        } else {
            deliveryPrice.setText("BDT. " + model.getDeliveryPrice() + "/-");
            totalPrice.setText("BDT. "+(totalItemsPriceValue + Long.valueOf(model.getDeliveryPrice()))+"/-");
        }
        if (!model.getCuttedPrice().equals("")) {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You will saved BDT. "+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getDiscountedPrice()))+"/- on this order.");
            } else {
                savedAmount.setText("You will saved BDT. "+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice()) - Long.valueOf(model.getProductPrice()))+"/- on this order.");
            }
        } else {
            if (!model.getDiscountedPrice().equals("")) {
                savedAmount.setText("You will saved BDT. "+model.getProductQuantity()*(Long.valueOf(model.getProductPrice()) - Long.valueOf(model.getDiscountedPrice()))+"/- on this order.");
            } else {
                savedAmount.setText("You will saved BDT.0/- on this order.");
            }
        }

        btnChangeOrNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myAddressesIntent = new Intent(OrderDetailsActivity.this, MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", DeliveryActivity.SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });
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

    ////////Rating Layout////////

    private void setRating(int starPosition) {
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#bebebe")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffbb00")));
            }
        }
    }

    ////////Rating Layout////////
}
