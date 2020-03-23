package com.pedalT.app.ui.user;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.pedalT.app.BaseActivity;
import com.pedalT.app.MainActivity;
import com.pedalT.app.R;
import com.pedalT.app.ui.chat.ChatLobby_Activity;
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

    // Class Variables:
    private static final String TAG = "UserProfile_Activity";
    private static final int PICK_IMAGE = 1;

    // MainActivity Profile View:
    private TextView UserName;
    private ImageView ProPic;

    private EditText fName, userLoc, userEmail, userName;
    private LinearLayout mainInfoLayout, userEditLayout;

    private Map<String, Object> user_updateHMap = new HashMap<>();

    private Animation slideUp, slideDown;

    // FireStore Access:
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access to Firebase Authentication from the Activity
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

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
        UserName = findViewById(R.id.account_displayfName);
        fName = findViewById(R.id.account_first_name);
        userName = findViewById(R.id.account_username);
        userLoc = findViewById(R.id.account_location);
        userEmail = findViewById(R.id.account_email);

        setProgressBar(R.id.progressBar);

        // Animations:
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Click Events Setters
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.accountDetailsBtn).setOnClickListener(this);
        // findViewById(R.id.pedal_tribe_feedback_btn).setOnClickListener(this);
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
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        } else {
            this.finish();
        }
    }

    // _____________________
    // user action methods:

    @Override
    public void onBackPressed() {
        // Check if the user is in "edit account mode":
        if (userEditLayout.getVisibility() == View.VISIBLE) {
            userEditLayout.setVisibility(View.GONE);
            mainInfoLayout.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fav_ads_btn:
                Intent fav_ads = new Intent(this, UserPersonalAds_Activity.class);
                startActivity(fav_ads);
                break;

            case R.id.user_profile_ad_btn:
                Intent post_ad = new Intent(this, UserCURDAds_Activity.class);
                startActivity(post_ad);
                break;

            case R.id.user_profile_myAds_btn:
                Intent personal_ad = new Intent(this, UserFavouriteAds_Activity.class);
                startActivity(personal_ad);
                break;

            case R.id.updateProfilePic:
                selectImageProfile();
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
                showProgressBar();
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
                setImageUpload(bitmap);
            }
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        this.finish();
    }

    // _____________________
    // data-intent handling methods:

    private void selectImageProfile() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void setImageUpload(Bitmap bitmap) {

        showProgressBar();
        // ProPic.setImageBitmap(bitmap);

        // Handle Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        // Get Current User UID String:
        String uid = currentUser.getUid();

        // Store Data in Firebase Storage
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid + ".jpeg");

        // Execute Storage
        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getImageDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: ", e.getCause());
                    }
                });
    }

    private void getImageDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri uri = task.getResult();
                System.out.println(uri);    // [TEST/DEV]
                setImageProfile(uri);
            }
        });
    }

    private void setImageProfile(Uri uri) {

        // Info. required updating:
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        // Update profile & add "complete" listener
        currentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hideProgressBar();
                }
            }
        });

        // Update Profile Picture:
        Glide.with(getApplicationContext()).load(uri).into(ProPic);
    }

    private void updateProfileInfo() {

        // Get Current User:
        String uid = currentUser.getUid();

        // Populate HashMap with Data:
        user_updateHMap.put("email", userEmail.getText().toString());
        user_updateHMap.put("location", userLoc.getText().toString());
        user_updateHMap.put("username", userName.getText().toString());
        user_updateHMap.put("first_name", fName.getText().toString());

        // Update User Exisitng Document:
        db.collection("users").document(uid).update(user_updateHMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        updateUI(currentUser);
                        Toast.makeText(getBaseContext(), "\uD83C\uDF89 Success! Your account info has been updated", Toast.LENGTH_LONG).show();
                        hideProgressBar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "\uD83D\uDE31 Uh-Oh! Something went wrong", Toast.LENGTH_LONG).show();
                        hideProgressBar();
                    }
                });
    }

    // _____________________
    // user UI/UX methods:

    private void updateUI(FirebaseUser user) {

        // User Details:
        Uri photoUrl = user.getPhotoUrl();

        if (photoUrl != null) {
            Glide.with(this).load(photoUrl).into(ProPic);
        }

        // User Edit Fields:
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Set Email to be seen:
                        userEmail.setText(document.getString("email"));
                        userLoc.setText(document.getString("location"));
                        userName.setText(document.getString("username"));
                        fName.setText(document.getString("first_name"));
                        UserName.setText(getString(R.string.user_greet, document.getString("first_name")));
                    } else {
                        Toast.makeText(getApplicationContext(), "Uh-oh, something is not right", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Uh-oh, something is not right", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
