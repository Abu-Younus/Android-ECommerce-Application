package com.example.sohoz_bazar;


import android.content.Intent;
import android.graphics.Color;
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

public class SignInFragment extends Fragment {

    private TextView tvSignUp;
    private FrameLayout parentFrameLayout;

    private EditText txtEmail;
    private EditText txtPassword;

    private Button btnSignIn;
    private TextView tvForgotPassword;
    private Button btnClose;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disabledCloseBtn = false;

    public SignInFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        tvSignUp = view.findViewById(R.id.tvSignUp);
        parentFrameLayout = getActivity().findViewById(R.id.registerFrameLayout);

        txtEmail = view.findViewById(R.id.txtEmailAddressSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);

        btnSignIn = view.findViewById(R.id.btnSignIn);

        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        btnClose = view.findViewById(R.id.btn_close_sign_in);

        progressBar = view.findViewById(R.id.signInProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();

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

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationSignInInputs();
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
                checkValidationSignInInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
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

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onForgotPasswordFragment = true;
                setFragment(new ForgotPasswordFragment());
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkValidationSignInInputs() {
        if (!TextUtils.isEmpty(txtEmail.getText())) {
            if (!TextUtils.isEmpty(txtPassword.getText())) {
                btnSignIn.setEnabled(true);
                btnSignIn.setTextColor(Color.rgb(255,255,255));
            } else {
                btnSignIn.setEnabled(false);
                btnSignIn.setTextColor(Color.argb(50,255,255,255));
            }
        } else {
            btnSignIn.setEnabled(false);
            btnSignIn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void checkEmailAndPassword() {
        if (txtEmail.getText().toString().matches(emailPattern)) {
            if (txtPassword.length() >= 8) {
                progressBar.setVisibility(View.VISIBLE);
                btnSignIn.setEnabled(false);
                btnSignIn.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.signInWithEmailAndPassword(txtEmail.getText().toString(),txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            if (disabledCloseBtn) {
                                disabledCloseBtn = false;
                            } else {
                                Intent intentHome = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intentHome);
                            }
                            getActivity().finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnSignIn.setEnabled(true);
                            btnSignIn.setTextColor(Color.rgb(255,255,255));
                            String error = task.getException().getMessage();
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(getActivity(),"Incorrect Email or Password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(),"Incorrect Email or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
