package com.example.sohoz_bazar;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUpFragment extends Fragment {

    private FrameLayout parentFrameLayout;
    private TextView tvSignIn;

    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtPassword;
    private EditText txtConfirmPassword;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private Button btnSignUp;

    private Button btnClose;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disabledCloseBtn = false;

    public SignUpFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        tvSignIn = view.findViewById(R.id.tvSignIn);
        parentFrameLayout = getActivity().findViewById(R.id.registerFrameLayout);

        txtFirstName = view.findViewById(R.id.txtFirstNameSignUp);
        txtLastName = view.findViewById(R.id.txtLastNameSignUp);
        txtEmail = view.findViewById(R.id.txtEmailAddressSignUp);
        txtPassword = view.findViewById(R.id.txtPasswordSignUp);
        txtConfirmPassword = view.findViewById(R.id.txtConfirmPasswordSignUp);

        progressBar = view.findViewById(R.id.signUpProgressBar);

        btnSignUp = view.findViewById(R.id.btnSignUp);

        btnClose = view.findViewById(R.id.btn_close_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (disabledCloseBtn) {
            btnClose.setVisibility(View.GONE);
        } else {
            btnClose.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        txtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignUpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignUpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignUpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignUpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignUpInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onForgotPasswordFragment = true;
                Intent intentHome = new Intent(getActivity(), HomeActivity.class);
                startActivity(intentHome);
                getActivity().finish();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkValidationSignUpInputs() {
        if(!TextUtils.isEmpty(txtFirstName.getText())) {
            if(!TextUtils.isEmpty(txtLastName.getText())) {
                if(!TextUtils.isEmpty(txtEmail.getText())) {
                    if(!TextUtils.isEmpty(txtPassword.getText()) && txtPassword.length() >= 8) {
                        if(!TextUtils.isEmpty(txtConfirmPassword.getText())) {
                            btnSignUp.setEnabled(true);
                            btnSignUp.setTextColor(Color.rgb(255,255,255));
                        }else {
                            btnSignUp.setEnabled(false);
                            btnSignUp.setTextColor(Color.argb(50,255,255,255));
                        }
                    }else {
                        btnSignUp.setEnabled(false);
                        btnSignUp.setTextColor(Color.argb(50,255,255,255));
                    }
                }else {
                    btnSignUp.setEnabled(false);
                    btnSignUp.setTextColor(Color.argb(50,255,255,255));
                }
            }else {
                btnSignUp.setEnabled(false);
                btnSignUp.setTextColor(Color.argb(50,255,255,255));
            }
        }else {
            btnSignUp.setEnabled(false);
            btnSignUp.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.error_icon);
        customErrorIcon.setBounds(0,0,customErrorIcon.getIntrinsicWidth(),customErrorIcon.getIntrinsicHeight());

        if(txtEmail.getText().toString().matches(emailPattern)) {
            if(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
                progressBar.setVisibility(View.VISIBLE);
                btnSignUp.setEnabled(false);
                btnSignUp.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.createUserWithEmailAndPassword(txtEmail.getText().toString(), txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Map<String,Object> userData = new HashMap<>();
                            userData.put("fullname",txtFirstName.getText().toString()+' '+txtLastName.getText().toString());
                            userData.put("email", txtEmail.getText().toString());
                            userData.put("profile", "");

                            firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).
                                    set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {

                                        CollectionReference userDataReference =  firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");

                                        Map<String,Object> wishListMap = new HashMap<>();
                                        wishListMap.put("list_size", (long) 0);

                                        Map<String,Object> ratingsMap = new HashMap<>();
                                        ratingsMap.put("list_size", (long) 0);

                                        Map<String,Object> cartMap = new HashMap<>();
                                        cartMap.put("list_size", (long) 0);

                                        Map<String,Object> myAddressesMap = new HashMap<>();
                                        myAddressesMap.put("list_size", (long) 0);

                                        Map<String,Object> notificationsMap = new HashMap<>();
                                        notificationsMap.put("list_size", (long) 0);

                                        final List<String> documentNames = new ArrayList<>();
                                        documentNames.add("MY_WISHLIST");
                                        documentNames.add("MY_RATINGS");
                                        documentNames.add("MY_CART");
                                        documentNames.add("MY_ADDRESSES");
                                        documentNames.add("MY_NOTIFICATIONS");

                                        List<Map<String,Object>> documentFields = new ArrayList<>();
                                        documentFields.add(wishListMap);
                                        documentFields.add(ratingsMap);
                                        documentFields.add(cartMap);
                                        documentFields.add(myAddressesMap);
                                        documentFields.add(notificationsMap);

                                        for (int x = 0;x < documentNames.size();x++) {
                                            final int finalX = x;
                                            userDataReference.document(documentNames.get(x)).set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                       if (finalX == documentNames.size() - 1) {
                                                           homeIntent();
                                                       }
                                                    } else {
                                                        progressBar.setVisibility(View.GONE);
                                                        btnSignUp.setEnabled(true);
                                                        btnSignUp.setTextColor(Color.rgb(255,255,255));
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnSignUp.setEnabled(true);
                            btnSignUp.setTextColor(Color.rgb(255,255,255));
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else {
                txtConfirmPassword.setError("Password and Confirm Password doesn't match!", customErrorIcon);
            }
        }else {
            txtEmail.setError("Invalid Email!", customErrorIcon);
        }
    }

    private void homeIntent() {
        if (disabledCloseBtn) {
            disabledCloseBtn = false;
        } else {
            Intent homeIntent = new Intent(getActivity(), HomeActivity.class);
            startActivity(homeIntent);
        }
        getActivity().finish();
    }
}
