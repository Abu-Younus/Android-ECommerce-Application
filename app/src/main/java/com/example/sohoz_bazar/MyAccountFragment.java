package com.example.sohoz_bazar;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.sohoz_bazar.DBqueries.clearData;


public class MyAccountFragment extends Fragment {


    public MyAccountFragment() {
        // Required empty public constructor
    }

    public static final int MANAGE_ADDRESS = 1;

    private Button btnViewAllAddress, btnSignOut;

    private CircleImageView userProfileImage, currentOrderImage;
    private TextView userName, userEmail, currentOrderStatus;
    private FloatingActionButton btnProfileSettings;
    private LinearLayout layoutContainer, recentOrdersContainer;
    private ImageView orderedIndicator, packedIndicator, shippedIndicator, deliveredIndicator;
    private ProgressBar o_to_p_progressBar, p_to_s_progressBar, s_to_d_progressBar;
    private TextView recentOrdersTitle;
    private TextView fullName, address, pincode;
    private Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        /////////Loading Dialog///////////

        btnViewAllAddress = view.findViewById(R.id.btn_view_all_addresses);
        userProfileImage = view.findViewById(R.id.user_profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        btnProfileSettings = view.findViewById(R.id.btn_profile_settings);
        layoutContainer = view.findViewById(R.id.layout_container);
        currentOrderImage = view.findViewById(R.id.current_order_image);
        currentOrderStatus = view.findViewById(R.id.current_order_status);
        orderedIndicator = view.findViewById(R.id.ordered_indicator);
        packedIndicator = view.findViewById(R.id.packed_indicator);
        shippedIndicator = view.findViewById(R.id.shipped_indicator);
        deliveredIndicator = view.findViewById(R.id.delivered_indicator);
        o_to_p_progressBar = view.findViewById(R.id.ordered_packed_progress);
        p_to_s_progressBar = view.findViewById(R.id.packed_shipped_progress);
        s_to_d_progressBar = view.findViewById(R.id.shipped_delivered_progress);
        recentOrdersContainer = view.findViewById(R.id.recent_orders_container);
        recentOrdersTitle = view.findViewById(R.id.recent_orders_title);
        fullName = view.findViewById(R.id.full_name);
        address = view.findViewById(R.id.address);
        pincode = view.findViewById(R.id.pin_code);
        btnSignOut = view.findViewById(R.id.btn_sign_out);

        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for (MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                    if (!myOrderItemModel.isCancellationRequested()) {
                        if (!myOrderItemModel.getOrderStatus().equals("Delivered") && !myOrderItemModel.getOrderStatus().equals("Cancelled")) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into(currentOrderImage);
                            currentOrderStatus.setText(myOrderItemModel.getOrderStatus());

                            switch (myOrderItemModel.getOrderStatus()) {
                                case "Ordered":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    break;

                                case "Packed":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_to_p_progressBar.setProgress(100);
                                    break;

                                case "Shipped":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_to_p_progressBar.setProgress(100);
                                    p_to_s_progressBar.setProgress(100);
                                    break;

                                case "Out for Delivery":
                                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_to_p_progressBar.setProgress(100);
                                    p_to_s_progressBar.setProgress(100);
                                    s_to_d_progressBar.setProgress(100);
                                    break;
                            }
                        }
                    }
                }
                loadingDialog.dismiss();
                int i = 0;
                for (MyOrderItemModel orderItemModel : DBqueries.myOrderItemModelList) {
                    if (i < 4) {
                        if (orderItemModel.getOrderStatus().equals("Delivered")) {
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.drawable.icon_placeholder)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                            i++;
                        }
                    } else {
                        break;
                    }
                }
                if (i == 0) {
                    recentOrdersTitle.setText("No recent orders.");
                }
                if (i < 3) {
                    for (int x = i;x < 4;x++) {
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if (DBqueries.addressesModelList.size() == 0) {
                            fullName.setText("No Address.");
                            address.setText("-");
                            pincode.setText("-");
                        } else {
                            setAddress();
                        }
                        loadingDialog.dismiss();
                    }
                });
                DBqueries.loadAddresses(getContext(), loadingDialog, false);
            }
        });

        DBqueries.loadOrders(getContext(),null, loadingDialog);

        btnViewAllAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myAddressesIntent = new Intent(getContext(), MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", MANAGE_ADDRESS);
                getContext().startActivity(myAddressesIntent);
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                Intent registerIntent = new Intent(getContext(), RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });

        btnProfileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfoIntent = new Intent(getContext(), UpdateUserInfoActivity.class);
                updateUserInfoIntent.putExtra("Name", userName.getText());
                updateUserInfoIntent.putExtra("Email", userEmail.getText());
                updateUserInfoIntent.putExtra("Photo", DBqueries.profile);
                startActivity(updateUserInfoIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userName.setText(DBqueries.fullName);
        userEmail.setText(DBqueries.email);

        if (!DBqueries.profile.equals("")) {
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.user_profile_icon)).into(userProfileImage);
        } else {
            userProfileImage.setImageResource(R.drawable.user_profile_icon);
        }
        if (!loadingDialog.isShowing()) {
            if (DBqueries.addressesModelList.size() == 0) {
                fullName.setText("No Address.");
                address.setText("-");
                pincode.setText("-");
            } else {
                setAddress();
            }
        }
    }

    private void setAddress() {
        String name,mobileNo;
        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        if (DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo().equals("")) {
            fullName.setText(name + " - " + mobileNo);
        } else {
            fullName.setText(name + " - " + mobileNo + " or " + DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMobileNo());
        }
        String flatNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocalityOrArea();
        String landmark = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();
        if (landmark.equals("")) {
            address.setText(flatNo + ", " + locality + ", " + city + ", " + state);
        } else {
            address.setText(flatNo + ", " + locality + ", " + landmark + ", " + city + ", " + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPinCode());
    }
}
