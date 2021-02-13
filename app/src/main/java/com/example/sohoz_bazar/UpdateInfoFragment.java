package com.example.sohoz_bazar;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateInfoFragment extends Fragment {


    public UpdateInfoFragment() {

    }

    private CircleImageView userProfileImage;
    private Button btnChangePhoto, btnRemovePhoto, btnUpdateUserInfo, btnDone;
    private EditText userName, userEmail, userPassword;

    private String name, email, photo;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    private Dialog loadingDialog, passwordConfirmationDialog;

    private Uri imageUri;

    private boolean updatePhoto = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_info, container, false);

        userProfileImage = view.findViewById(R.id.user_profile_image);
        btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        btnRemovePhoto = view.findViewById(R.id.btn_remove_photo);
        btnUpdateUserInfo = view.findViewById(R.id.btn_update_user_info);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        /////////Loading Dialog///////////

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /////////Loading Dialog///////////

        /////////Password Confirmation Dialog///////////

        passwordConfirmationDialog = new Dialog(getContext());
        passwordConfirmationDialog.setContentView(R.layout.password_confirmation_dialog);
        passwordConfirmationDialog.setCancelable(true);
        passwordConfirmationDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        passwordConfirmationDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        userPassword = passwordConfirmationDialog.findViewById(R.id.user_password);
        btnDone = passwordConfirmationDialog.findViewById(R.id.btn_done);

        /////////Password Confirmation Dialog///////////

        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        photo = getArguments().getString("Photo");

        Glide.with(getContext()).load(photo).into(userProfileImage);
        userName.setText(name);
        userEmail.setText(email);

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                        gallaryIntent.setType("image/*");
                        startActivityForResult(gallaryIntent, 1);
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    }
                } else {
                    Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                    gallaryIntent.setType("image/*");
                    startActivityForResult(gallaryIntent, 1);
                }
            }
        });

        btnRemovePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = null;
                updatePhoto = true;
                Glide.with(getContext()).load(R.drawable.user_profile_icon).into(userProfileImage);
            }
        });

        userName.addTextChangedListener(new TextWatcher() {
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
        userEmail.addTextChangedListener(new TextWatcher() {
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

        btnUpdateUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        return view;
    }

    private void checkValidationSignUpInputs() {
        if (!TextUtils.isEmpty(userName.getText())) {
            if (!TextUtils.isEmpty(userEmail.getText())) {
                btnUpdateUserInfo.setEnabled(true);
                btnUpdateUserInfo.setTextColor(Color.rgb(255, 255, 255));
            } else {
                btnUpdateUserInfo.setEnabled(false);
                btnUpdateUserInfo.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            btnUpdateUserInfo.setEnabled(false);
            btnUpdateUserInfo.setTextColor(Color.argb(50, 255, 255, 255));
        }
    }

    private void checkEmailAndPassword() {

        Drawable customErrorIcon = getResources().getDrawable(R.drawable.error_icon);
        customErrorIcon.setBounds(0, 0, customErrorIcon.getIntrinsicWidth(), customErrorIcon.getIntrinsicHeight());

        if (userEmail.getText().toString().matches(emailPattern)) {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (userEmail.getText().toString().toLowerCase().trim().equals(email.toLowerCase().trim())) {
                loadingDialog.show();
                updatePhoto(user);
            } else {
                passwordConfirmationDialog.show();
                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.show();
                        passwordConfirmationDialog.dismiss();
                        String password = userPassword.getText().toString();
                        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updateEmail(userEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                updatePhoto(user);
                                            } else {
                                                loadingDialog.dismiss();
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        } else {
            userEmail.setError("Invalid Email!", customErrorIcon);
        }
    }

    private void updatePhoto(final FirebaseUser user) {
        ///////////Update Profile Image//////////////

        if (updatePhoto) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile/" + user.getUid() + ".jpg");
            if (imageUri != null) {
                Glide.with(getContext()).asBitmap().load(imageUri).circleCrop().into(new ImageViewTarget<Bitmap>(userProfileImage) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = storageReference.putBytes(data);

                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                imageUri = task.getResult();
                                                DBqueries.profile = task.getResult().toString();
                                                Glide.with(getContext()).load(DBqueries.profile).into(userProfileImage);
                                                Map<String,Object> updateData = new HashMap<>();
                                                updateData.put("email", userEmail.getText().toString());
                                                updateData.put("fullname", userName.getText().toString());
                                                updateData.put("profile", DBqueries.profile);
                                                updateFields(user, updateData);
                                            } else {
                                                loadingDialog.dismiss();
                                                DBqueries.profile = "";
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    loadingDialog.dismiss();
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        return;
                    }

                    @Override
                    protected void setResource(@Nullable Bitmap resource) {
                        userProfileImage.setImageResource(R.drawable.user_profile_icon);
                    }
                });
            } else {
                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DBqueries.profile = "";
                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("email", userEmail.getText().toString());
                            updateData.put("fullname", userName.getText().toString());
                            updateData.put("profile", "");
                            updateFields(user, updateData);
                        } else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else {
            Map<String,Object> updateData = new HashMap<>();
            updateData.put("fullname", userName.getText().toString());
            updateFields(user, updateData);
        }

        ///////////Update Profile Image//////////////
    }

    private void updateFields(FirebaseUser user, final Map<String,Object> updateData) {
        FirebaseFirestore.getInstance().collection("USERS")
                .document(user.getUid()).update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (updateData.size() > 1) {
                                DBqueries.email = userEmail.getText().toString().trim();
                                DBqueries.fullName = userName.getText().toString().trim();
                            } else {
                                DBqueries.fullName = userName.getText().toString().trim();
                            }
                            getActivity().finish();
                            Toast.makeText(getContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == getActivity().RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    updatePhoto = true;
                    Glide.with(getContext()).load(imageUri).into(userProfileImage);
                } else {
                    Toast.makeText(getContext(), "Image not found!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, 1);
            } else {
                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
