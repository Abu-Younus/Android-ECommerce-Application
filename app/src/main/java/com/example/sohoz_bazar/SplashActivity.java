package com.example.sohoz_bazar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private ImageView easyShoppyLogo, imgInternetConnection;
    private TextView tvInternetConnection;
    private Button btnRetryConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        easyShoppyLogo = findViewById(R.id.easy_shoppy_logo);
        imgInternetConnection = findViewById(R.id.img_internet_connection);
        tvInternetConnection = findViewById(R.id.tv_internet_connection);
        btnRetryConnection = findViewById(R.id.btn_retry_connection);

        firebaseAuth = FirebaseAuth.getInstance();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        btnRetryConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkInfo = connectivityManager.getActiveNetworkInfo();
        currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intentSignIn = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intentSignIn);
            finish();
        } else {
            if (networkInfo != null && networkInfo.isConnected() == true) {
                imgInternetConnection.setVisibility(View.GONE);
                tvInternetConnection.setVisibility(View.GONE);
                btnRetryConnection.setVisibility(View.GONE);
                easyShoppyLogo.setVisibility(View.VISIBLE);
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .update("Last seen", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intentHome = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intentHome);
                            finish();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                easyShoppyLogo.setVisibility(View.GONE);
                imgInternetConnection.setVisibility(View.VISIBLE);
                tvInternetConnection.setVisibility(View.VISIBLE);
                btnRetryConnection.setVisibility(View.VISIBLE);
                Glide.with(this).load(R.drawable.no_internet_connection).into(imgInternetConnection);
            }
        }
    }

    private void reloadPage() {
        networkInfo = connectivityManager.getActiveNetworkInfo();
        currentUser = firebaseAuth.getCurrentUser();
        if (networkInfo != null && networkInfo.isConnected() == true) {
            imgInternetConnection.setVisibility(View.GONE);
            tvInternetConnection.setVisibility(View.GONE);
            btnRetryConnection.setVisibility(View.GONE);
            easyShoppyLogo.setVisibility(View.VISIBLE);
            FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                    .update("Last seen", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Intent intentHome = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intentHome);
                        finish();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            easyShoppyLogo.setVisibility(View.GONE);
            imgInternetConnection.setVisibility(View.VISIBLE);
            tvInternetConnection.setVisibility(View.VISIBLE);
            btnRetryConnection.setVisibility(View.VISIBLE);
            Toast.makeText(SplashActivity.this, "Internet Connection not found", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(R.drawable.no_internet_connection).into(imgInternetConnection);
        }
    }
}
