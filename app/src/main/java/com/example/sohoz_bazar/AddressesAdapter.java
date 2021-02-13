package com.example.sohoz_bazar;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sohoz_bazar.DeliveryActivity.SELECT_ADDRESS;
import static com.example.sohoz_bazar.MyAccountFragment.MANAGE_ADDRESS;
import static com.example.sohoz_bazar.MyAddressesActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    private List<AddressesModel> addressesModelList;
    private int MODE;
    private int preSelectedPosition;
    private boolean refresh = false;

    private Dialog loadingDialog;

    public AddressesAdapter(List<AddressesModel> addressesModelList, int MODE, Dialog loadingDialog) {
        this.addressesModelList = addressesModelList;
        this.MODE = MODE;
        preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city = addressesModelList.get(position).getCity();
        String flatNo = addressesModelList.get(position).getFlatNo();
        String locality = addressesModelList.get(position).getLocalityOrArea();
        String pincode = addressesModelList.get(position).getPinCode();
        String landmark = addressesModelList.get(position).getLandmark();
        String name = addressesModelList.get(position).getName();
        String mobileNo = addressesModelList.get(position).getMobileNo();
        String alternateMobileNo = addressesModelList.get(position).getAlternateMobileNo();
        String state = addressesModelList.get(position).getState();
        Boolean selected = addressesModelList.get(position).getSelected();

        holder.setData(city,flatNo,locality,pincode,landmark, name, mobileNo, alternateMobileNo, state, selected, position);
    }

    @Override
    public int getItemCount() {
        return addressesModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName;
        private TextView address;
        private TextView pinCode;
        private ImageView checkIcon;
        private LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fullName = itemView.findViewById(R.id.full_name);
            address = itemView.findViewById(R.id.address);
            pinCode = itemView.findViewById(R.id.pin_code);
            checkIcon = itemView.findViewById(R.id.check_icon);
            optionContainer = itemView.findViewById(R.id.option_container);
        }

        private void setData(String city, String flatNo, String locality, String pinCodeText, String landmark, String fullNameText, String mobileNo, String alternateMobileNo, String state, Boolean selected, final int position) {
            if (alternateMobileNo.equals("")) {
                fullName.setText(fullNameText + " - " + mobileNo);
            } else {
                fullName.setText(fullNameText + " - " + mobileNo + " or " + alternateMobileNo);
            }
            if (landmark.equals("")) {
                address.setText(flatNo + ", " + locality + ", " + city + ", " + state);
            } else {
                address.setText(flatNo + ", " + locality + ", " + landmark + ", " + city + ", " + state);
            }
            pinCode.setText(pinCodeText);

            if (MODE == SELECT_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                checkIcon.setImageResource(R.drawable.ic_check_black_24dp);
                if (selected) {
                    optionContainer.setVisibility(View.GONE);
                    checkIcon.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    checkIcon.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (preSelectedPosition != position) {
                            addressesModelList.get(position).setSelected(true);
                            addressesModelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress = position;
                        }
                    }
                });

            } else if (MODE == MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent addAddressesIntent = new Intent(itemView.getContext(), AddAddressActivity.class);
                        addAddressesIntent.putExtra("INTENT", "update_address");
                        addAddressesIntent.putExtra("index", position);
                        itemView.getContext().startActivity(addAddressesIntent);
                        refresh = false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        Map<String,Object> addresses = new HashMap<>();
                        int x = 0;
                        int selected = -1;
                        for (int i = 0;i < addressesModelList.size();i++) {
                            if (i != position) {
                                x++;
                                addresses.put("city_"+x, addressesModelList.get(i).getCity());
                                addresses.put("locality_"+x, addressesModelList.get(i).getLocalityOrArea());
                                addresses.put("flat_no_"+x, addressesModelList.get(i).getFlatNo());
                                addresses.put("pincode_"+x, addressesModelList.get(i).getPinCode());
                                addresses.put("landmark_"+x, addressesModelList.get(i).getLandmark());
                                addresses.put("name_"+x, addressesModelList.get(i).getName());
                                addresses.put("mobile_no_"+x, addressesModelList.get(i).getMobileNo());
                                addresses.put("alternate_mobile_no_"+x, addressesModelList.get(i).getAlternateMobileNo());
                                addresses.put("state_"+x, addressesModelList.get(i).getState());
                                if (addressesModelList.get(position).getSelected()) {
                                    if (position - 1 >= 0) {
                                        if (x == position) {
                                            addresses.put("selected_"+x, true);
                                            selected = x;
                                        } else {
                                            addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                        }
                                    } else {
                                        if (x == 1) {
                                            addresses.put("selected_"+x, true);
                                            selected = x;
                                        } else {
                                            addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                        }
                                    }
                                } else {
                                    addresses.put("selected_"+x, addressesModelList.get(i).getSelected());
                                    if (addressesModelList.get(i).getSelected()) {
                                        selected = x;
                                    }
                                }
                            }
                        }
                        addresses.put("list_size", x);
                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                .collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    DBqueries.addressesModelList.remove(position);
                                    if (finalSelected != -1) {
                                        DBqueries.selectedAddress = finalSelected - 1;
                                        DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                    } else if (DBqueries.addressesModelList.size() == 0) {
                                        DBqueries.selectedAddress = -1;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
                        refresh = false;
                    }
                });
                checkIcon.setImageResource(R.drawable.ic_more_vert_black_24dp);
                checkIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if (refresh) {
                            refreshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh = true;
                        }
                        preSelectedPosition = position;
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }
        }
    }
}
