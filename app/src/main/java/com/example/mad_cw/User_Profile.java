package com.example.mad_cw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class User_Profile extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "User Profile";
    private static final int PICK_IMAGE = 1;

    private TextView UserUidNum;
    private ImageView ProfilePicture;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        // Views
        ProfilePicture = findViewById(R.id.userProfilePic);
        UserUidNum = findViewById(R.id.textView);

        // Buttons
        findViewById(R.id.updateProfilePic).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);

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

//            for (UserInfo profile : user.getProviderData()) {
//                // Id of the provider (ex: google.com)
//                String providerId = profile.getProviderId();
//
//                // UID specific to the provider
//                String uid = profile.getUid();
//
//                // Name, email address, and profile photo Url
//                String name = profile.getDisplayName();
//                String email = profile.getEmail();
//                Uri photoUrl = profile.getPhotoUrl();
//
//                Log.w(TAG, providerId + " " + uid +  " " + name + " " + email + " " + photoUrl.toString());
//            }

            // Display user Profile Details:
            // mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            UserUidNum.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            Uri photoUrl = user.getPhotoUrl();
            Glide.with(this).load(photoUrl).into(ProfilePicture);

//            Log.w(TAG, photoUrl.toString());
//            mProfilePic.setImageURI(Uri.parse(photoUrl.toString()));

//            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);
//            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
//            findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    /**
     * Handling User Profile Image Update:
     */

    public void selectProfilePic() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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

                ProfilePicture.setImageBitmap(bitmap);
                handleUpload(bitmap);
            }
        }
    }

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

    public void updateProfile(Uri uri) {

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

    /**
     * Class [Public Method]
     * ______
     * Params: View v
     * ______
     * Usage: User Clicks:
     *
     */

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.updateProfilePic:
                selectProfilePic();
                break;

            case R.id.signOutButton:
                signOut();
                break;
        }
    }
}
