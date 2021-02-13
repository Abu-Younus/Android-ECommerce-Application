package com.example.sohoz_bazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sohoz_bazar.DBqueries.addressesModelList;
import static com.example.sohoz_bazar.DBqueries.selectedAddress;
import static com.example.sohoz_bazar.DeliveryActivity.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView myAddressesRecyclerView;
    private static AddressesAdapter addressesAdapter;

    private Button btnDeliverHere;

    private LinearLayout btnAddNewAddress;
    private TextView addressesSaved;

    private int previousAddress;

    private int mode;

    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);

        Toolbar toolbar = findViewById(R.id.toolbar);
        myAddressesRecyclerView = findViewById(R.id.addresses_recycler_view);
        btnDeliverHere = findViewById(R.id.btn_deliver_here);
        btnAddNewAddress = findViewById(R.id.btn_add_new_address);
        addressesSaved = findViewById(R.id.total_address_saved);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("My Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(String.valueOf(addressesModelList.size())+" addresses saved");
            }
        });

        /////////Loading Dialog///////////

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        myAddressesRecyclerView.setLayoutManager(linearLayoutManager);

        previousAddress = selectedAddress;

        mode = getIntent().getIntExtra("MODE", -1);

        if (mode == SELECT_ADDRESS) {
            btnDeliverHere.setVisibility(View.VISIBLE);
        } else {
            btnDeliverHere.setVisibility(View.GONE);
        }

        btnDeliverHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedAddress != previousAddress) {

                    final int previousAddressIndex = previousAddress;
                    loadingDialog.show();
                    Map<String,Object> updateSelection = new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddress+1),false);
                    updateSelection.put("selected_"+String.valueOf(selectedAddress+1),true);

                    previousAddress = selectedAddress;

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                            .document("MY_ADDRESSES").update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            } else {
                                previousAddress = previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressesActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else {
                    finish();
                }
            }
        });

        addressesAdapter = new AddressesAdapter(addressesModelList,mode,loadingDialog);
        myAddressesRecyclerView.setAdapter(addressesAdapter);
        ((SimpleItemAnimator)myAddressesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();

        btnAddNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressesIntent = new Intent(MyAddressesActivity.this, AddAddressActivity.class);
                if (mode != SELECT_ADDRESS) {
                    addAddressesIntent.putExtra("INTENT", "manage");
                } else {
                    addAddressesIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressesIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        addressesSaved.setText(String.valueOf(addressesModelList.size())+" addresses saved");
    }

    public static void refreshItem(int deSelect, int select) {
        addressesAdapter.notifyItemChanged(deSelect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (mode == SELECT_ADDRESS) {
                if (selectedAddress != previousAddress) {
                    addressesModelList.get(selectedAddress).setSelected(false);
                    addressesModelList.get(previousAddress).setSelected(true);
                    selectedAddress = previousAddress;
                }
            }
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mode == SELECT_ADDRESS) {
            if (selectedAddress != previousAddress) {
                addressesModelList.get(selectedAddress).setSelected(false);
                addressesModelList.get(previousAddress).setSelected(true);
                selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }
}
