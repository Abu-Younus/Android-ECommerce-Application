package com.example.sohoz_bazar;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {


    public ForgotPasswordFragment() {

    }

    private FrameLayout parentFrameLayout;

    private EditText txtForgotEmail;
    private Button btnSendPasswordLink;
    private TextView tvGoBack;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView tvEmailIcon;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        txtForgotEmail = view.findViewById(R.id.txtForgotEmailAddress);
        btnSendPasswordLink = view.findViewById(R.id.btnSendPasswordLink);
        tvGoBack = view.findViewById(R.id.tvGoBack);

        emailIconContainer = view.findViewById(R.id.emailIconContainerForgotEmail);
        emailIcon = view.findViewById(R.id.emailIconForgotEmail);
        tvEmailIcon = view.findViewById(R.id.tvEmailIconForgotEmail);

        progressBar = view.findViewById(R.id.forgotPasswordProgressBar);

        parentFrameLayout = getActivity().findViewById(R.id.registerFrameLayout);

        firebaseAuth = FirebaseAuth.getInstance();

        txtForgotEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidationForgotPasswordInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignInFragment());
            }
        });

        btnSendPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                btnSendPasswordLink.setEnabled(false);
                btnSendPasswordLink.setTextColor(Color.argb(50,255,255,255));

                firebaseAuth.sendPasswordResetEmail(txtForgotEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0);
                            scaleAnimation.setDuration(100);
                            scaleAnimation.setInterpolator(new AccelerateInterpolator());
                            scaleAnimation.setRepeatMode(Animation.REVERSE);
                            scaleAnimation.setRepeatCount(1);
                            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIcon.setVisibility(View.VISIBLE);
                                    emailIcon.setColorFilter(getResources().getColor(R.color.successGreen));
                                    tvEmailIcon.setVisibility(View.VISIBLE);
                                    tvEmailIcon.setTextColor(getResources().getColor(R.color.successGreen));
                                    btnSendPasswordLink.setEnabled(false);
                                    btnSendPasswordLink.setTextColor(Color.argb(50,255,255,255));
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            emailIcon.startAnimation(scaleAnimation);

                        } else {
                            String error = task.getException().getMessage();
                            btnSendPasswordLink.setEnabled(true);
                            btnSendPasswordLink.setTextColor(Color.rgb(255,255,255));
                            tvEmailIcon.setText(error);
                            tvEmailIcon.setTextColor(getResources().getColor(R.color.colorPrimary));
                            TransitionManager.beginDelayedTransition(emailIconContainer);
                            emailIcon.setVisibility(View.VISIBLE);
                            emailIcon.setColorFilter(getResources().getColor(R.color.colorPrimary));
                            tvEmailIcon.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        return view;
    }

    private void checkValidationForgotPasswordInputs() {
        if (!TextUtils.isEmpty(txtForgotEmail.getText())) {
            btnSendPasswordLink.setEnabled(true);
            btnSendPasswordLink.setTextColor(Color.rgb(255,255,255));
        } else {
            btnSendPasswordLink.setEnabled(false);
            btnSendPasswordLink.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}
