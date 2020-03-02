package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class User_Profile_Edit extends BaseActivity implements View.OnClickListener {

    /* Class Variables */

    private static final String TAG = "User Profile Edit";
    private static final int PICK_IMAGE = 1;

    private EditText fName, userLoc, userEmail, userName;

    private ImageView ProPic;

    // Access to Firebase Authentication from the Activity
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the User Edit Layout:
        setContentView(R.layout.user_profile_edit);

        // Locating Views:
        fName = findViewById(R.id.account_first_name);
        userName = findViewById(R.id.account_username);
        userLoc = findViewById(R.id.account_location);
        userEmail = findViewById(R.id.account_email);
        ProPic = findViewById(R.id.userProfilePic);

        // Set Event Clicks
        findViewById(R.id.updateProfilePic).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // Updating Profile Picture
            case R.id.updateProfilePic:
                selectProfilePic();
                break;

            // Save Profile Details
            case R.id.saveAccount:
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass Image Data Along:
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                // this is the image selected by the user
                Uri imageUri = data.getData();
                Log.w(TAG, imageUri.toString());

                // Handle the user image in bitmap:
                // Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ProPic.setImageBitmap(bitmap);
                handleUpload(bitmap);
            }
        }
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();

        // Check User Details
        if (user != null) {

            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Profile Image:
            Uri photoUrl = user.getPhotoUrl();

            if (photoUrl != null) {
                Glide.with(this).load(photoUrl).into(ProPic);
            }

            // User Email:
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getString("email"));

                            // Set Email to be seen:
                            userEmail.setText(document.getString("email"));
                            userLoc.setText(document.getString("location"));
                            userName.setText(document.getString("username"));

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            // [ Test Env ]
            // findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
            // UserUidNum.setText(getString(R.string.firebase_status_fmt, user.getUid()));

        } else {

            // Nothing happens

        }
    }

    public void selectProfilePic() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    /* Class [Public Methods] */

    private void handleUpload(Bitmap bitmap){

        // Handle Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // Get Current User UID String:
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Store Data in Firebase Storage
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        // Execute Storage
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.w(TAG, "onSuccess: " + uri);
                updateProfile(uri);
            }
        });
    }

    private void updateProfile(Uri uri) {

        // Get Current (Signed-In) User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Set Parameters that require Updating
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        // Update profile & add "complete" listener
        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User profile updated.");
                }
            }
        });

        // Refresh the UI Page:
        onStart();

    }

}
