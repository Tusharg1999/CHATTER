package com.example.superman.chatter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private Button updateProfileButton;
    private EditText editTextName, editTextStatus;
    private CircleImageView settingsImage;
    private FirebaseAuth mAuth;
    private DatabaseReference currentUserReference;
    private String currentUserId;
    private static final int GALLERY_PICK = 1;
    private StorageReference mImageStorage;
    private ProgressDialog mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_settings2);
        initializeFields();
        editTextName.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        currentUserReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = mAuth.getCurrentUser().getUid();
        updateProfileButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
        retriveUserData();
        settingsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    private void retriveUserData() {
        currentUserReference.child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image"))) {
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String reteriveImage=dataSnapshot.child("image").getValue().toString();
                    editTextName.setText(retrievename);
                    editTextStatus.setText(retrieveStatus);
                    Picasso.get().load(reteriveImage).into(settingsImage);
                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))) {
                    String retrievename = dataSnapshot.child("name").getValue().toString();
                    String retrieveStatus = dataSnapshot.child("status").getValue().toString();
                    editTextName.setText(retrievename);
                    editTextStatus.setHint(retrieveStatus);
                } else {
                    editTextName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please enter your Information first", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initializeFields() {
        updateProfileButton = findViewById(R.id.submit_status_button);
        editTextName = findViewById(R.id.name_edit_text_name);
        editTextStatus = findViewById(R.id.name_status);
        settingsImage = findViewById(R.id.profile_image);
        mImageStorage = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mProgressBar = new ProgressDialog(SettingsActivity.this);
    }

    private void updateUserProfile() {
        String userName = editTextName.getText().toString();
        String userStatus = editTextStatus.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(SettingsActivity.this, "Please Enter Your User Name...", Toast.LENGTH_SHORT).show();

        }
        if (TextUtils.isEmpty(userStatus)) {
            Toast.makeText(SettingsActivity.this, "Please Enter Your Profile Status...", Toast.LENGTH_SHORT).show();

        } else {
            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserId);
            profileMap.put("name", userName);
            profileMap.put("status", userStatus);
            currentUserReference.child("user").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Profile is successfully Update", Toast.LENGTH_SHORT).show();
                        sendUserToMainActivity();
                    } else {
                        String error = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_PICK && data != null) {
            Uri imageuri = data.getData();
            CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgressBar.setTitle("Loading");
                mProgressBar.setMessage("Wait while we are uploading your image");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();
                Uri ResultUri = result.getUri();
                final StorageReference filepath = mImageStorage.child("Profile Images").child(currentUserId + ".jpg");

                filepath.putFile(ResultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            mImageStorage.child("Profile Images").child(currentUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override public void onSuccess(Uri uri) {

                                    String downloadUrl = uri.toString();
                                    currentUserReference.child("user").child(currentUserId).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(SettingsActivity.this, "done", Toast.LENGTH_SHORT).show();
                                                mProgressBar.dismiss();
                                            }
                                            else
                                            {
                                                String error=task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                mProgressBar.dismiss();
                                            }

                                        }
                                    });
                                }
                                }

                            );
                        }

                    }
                });
            }
        }
    }
}

