package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.chat.ChatLobby_Activity;
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
import java.util.HashMap;
import java.util.Map;

public class UserProfile_Activity extends BaseActivity implements View.OnClickListener {

    /*
        Handles User & its Activity:
     */

    // Class Variables:
    private static final String TAG = "UserProfile_Activity";
    private static final int PICK_IMAGE = 1;

    // MainActivity Profile View:
    private TextView UserUidNum;
    private ImageView ProPic;

    private EditText fName, userLoc, userEmail, userName;
    private LinearLayout mainInfoLayout, userEditLayout;

    private Map<String, Object> user_updateHMap = new HashMap<>();

    private Animation slideUp, slideDown;

    // Access to Firebase Authentication from the Activity
    private FirebaseAuth mAuth;

    // Access a Cloud Firestore instance from the Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // _____________________
    // class activity cycles:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In:
        setContentView(R.layout.user_profile_layout);

        // Layout View:
        mainInfoLayout = findViewById(R.id.main_user_layout);
        userEditLayout = findViewById(R.id.user_edit_layout);

        ProPic = findViewById(R.id.userProfilePic);
        UserUidNum = findViewById(R.id.textView);
        fName = findViewById(R.id.account_first_name);
        userName = findViewById(R.id.account_username);
        userLoc = findViewById(R.id.account_location);
        userEmail = findViewById(R.id.account_email);

        // Animations:
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Click Events Setters
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.accountDetailsBtn).setOnClickListener(this);
        findViewById(R.id.pedal_tribe_feedback_btn).setOnClickListener(this);
        findViewById(R.id.user_profile_ad_btn).setOnClickListener(this);
        findViewById(R.id.fav_ads_btn).setOnClickListener(this);
        findViewById(R.id.updateProfilePic).setOnClickListener(this);
        findViewById(R.id.saveAccount).setOnClickListener(this);
        findViewById(R.id.user_profile_myAds_btn).setOnClickListener(this);
        findViewById(R.id.user_profile_myChat).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // _____________________
    // class click events handler:

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fav_ads_btn:
                Intent fav_ads = new Intent(this, User_Adverts_Favourite_Activity.class);
                startActivity(fav_ads);
                break;

            case R.id.user_profile_ad_btn:
                Intent post_ad = new Intent(this, User_Advert_Create_Edit_Activity.class);
                startActivity(post_ad);
                break;

            case R.id.user_profile_myAds_btn:
                Intent personal_ad = new Intent(this, User_Adverts_Personal_Activity.class);
                startActivity(personal_ad);
                break;

            case R.id.updateProfilePic:
                selectProfilePic();
                break;

            case R.id.accountDetailsBtn:
                // userEditLayout.startAnimation(slideUp);
                mainInfoLayout.setVisibility(View.GONE);
                userEditLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.user_profile_myChat:
                Intent open_chats = new Intent(this, ChatLobby_Activity.class);
                startActivity(open_chats);
                break;

            case R.id.saveAccount:
                updateProfileInfo();
                // userEditLayout.startAnimation(slideDown);
                userEditLayout.setVisibility(View.GONE);
                mainInfoLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.signOutButton:
                signOut();
                break;
        }
    }

    // _____________________
    // data-intent handling methods:

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
                handleImageUpload(bitmap);
            }
        }
    }

    private void selectProfilePic() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void handleImageUpload(Bitmap bitmap){

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

    private void updateProfileInfo() {

        // Get Current User:
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        user_updateHMap.put("email", userEmail.getText().toString());
        user_updateHMap.put("location", userLoc.getText().toString());
        user_updateHMap.put("username", userName.getText().toString());
        user_updateHMap.put("first_name",fName.getText().toString());

        // Add a new document with a generated ID
        db.collection("users")
                .document(uid)
                .update(user_updateHMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.w(TAG, "User Successfully Registered");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

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
                // Display user Profile Details:
                UserUidNum.setText(getString(R.string.firebase_status_fmt, user.getUid()));
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

        }
    }

    // _____________________
    // user action methods:

    private void signOut() {
        mAuth.signOut();
        this.finish();
        updateUI(null);
    }

}
