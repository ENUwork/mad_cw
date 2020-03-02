package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User_Profile extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "User Profile";

    // MainActivity Profile View:
    private TextView UserUidNum;         // [Dev]
    private ImageView ProPic;

    // Access to Firebase Authentication from the Activity
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Layout View
        ProPic = findViewById(R.id.userProfilePic);
        UserUidNum = findViewById(R.id.textView);

        // Click Events Setters
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.accountDetailsBtn).setOnClickListener(this);
        findViewById(R.id.pedal_tribe_feedback_btn).setOnClickListener(this);
        findViewById(R.id.user_profile_ad_btn).setOnClickListener(this);

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

    private void signOut() {
        mAuth.signOut();
        this.finish();
        updateUI(null);
    }

    private void updateUI(FirebaseUser user) {
        hideProgressBar();

        // Check User Details
        if (user != null) {

            // Display user Profile Details:
            // mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            UserUidNum.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).into(ProPic);

//            Log.w(TAG, photoUrl.toString());
//            mProfilePic.setImageURI(Uri.parse(photoUrl.toString()));

//            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());

        } else {

            // Nothing happens

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            // _______
            // Sign Out Account Trigger

            case R.id.signOutButton:
                signOut();
                break;

            // _______
            // User Favourite Layout Ads (Show)

            // _______
            // User Classified Layout Ads (Show)

            // _______
            // Account Details Layout (Show)

            case R.id.accountDetailsBtn:
                Intent acct_det = new Intent(this, User_Profile_Edit.class);
                startActivity(acct_det);
                break;

            // _______
            // User Post Ad (Initialize)

            case R.id.user_profile_ad_btn:
                Intent post_ad = new Intent(this, Post_Ads.class);
                startActivity(post_ad);
                break;

        }
    }
}
