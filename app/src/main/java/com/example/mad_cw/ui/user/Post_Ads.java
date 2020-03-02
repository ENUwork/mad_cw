package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post_Ads extends BaseActivity implements View.OnClickListener {

    /* Class Variables */

    private static final String TAG = "User Profile Post Add";

    private static final int PICK_IMAGE = 1;
    private Uri ImageUri;
    private int upload_count = 0;
    private int counter = 0;

    // Class Local Temp. Storage:
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private Map<String, Object> advert_info = new HashMap<>();

    // Access a Cloud Firestore instance from the Activity:
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access to Firebase Authentication from the Activity:
    private FirebaseAuth mAuth;

    // Class Layout Views Local Var.
    private EditText adPrice, adLoc, adTitle, adDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the User Edit Layout:
        setContentView(R.layout.post_ad);

        // Progress Bar:
        setProgressBar(R.id.progressBar);

        // Locating Views:
        adPrice = findViewById(R.id.post_ad_input_price);
        adDesc = findViewById(R.id.post_ad_input_desc);
        adLoc = findViewById(R.id.post_ad_input_location);
        adTitle = findViewById(R.id.post_ad_input_title);

        // Set Event Clicks
        findViewById(R.id.post_ad_select_img_btn).setOnClickListener(this);
        findViewById(R.id.post_ad_upload_btn).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // Updating Profile Picture
            case R.id.post_ad_select_img_btn:
                selectAdImages();
                break;

            // Save Profile Details
            case R.id.post_ad_upload_btn:
                // setAdDetailsToUser_Info();
                storeAdvert(advert_info);
                break;

        }

    }

    /* Handle Multiple Image Upload */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int countClipData = data.getClipData().getItemCount();
                int currentImageSelect = 0;

                while (currentImageSelect < countClipData){

                    ImageUri = data.getClipData().getItemAt(currentImageSelect).getUri();

                    ImageList.add(ImageUri);

                    currentImageSelect = currentImageSelect + 1;
                }

                handleImgUpload();
            }
        }
    }

    /*
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
     */

    public void selectAdImages() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image(s)");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    /* Class [Private Methods] */

    private void handleImgUpload(){

        showProgressBar();

        // Initializing Storage Location
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ClassifiedAds_Img");

        for( upload_count = 0; upload_count < ImageList.size(); upload_count++ ) {

            Log.w(TAG, Integer.toString(upload_count));

            Uri IndividualImage = ImageList.get(upload_count);
            final StorageReference ImageName = ImageFolder.child("Image" + IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            counter++;

                            String url = String.valueOf(uri);
                            Log.w(TAG, url);
                            Log.w(TAG, Integer.toString(counter));

                            // Store Image_Links:
                            advert_info.put("image_link_" + counter, url);

                        }
                    });
                }
            });
        }
    }

//    private void setAdDetailsToUser(String url, int count) {
//
//        // Get Current User:
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String uid = currentUser.getUid();
//
//        Log.w(TAG, url);
//
//        // Create a new user with a first and last name
//        Map<String, Object> ad = new HashMap<>();
//        ad.put("image_link_" + count, url);
//
//        System.out.println("HashMap Contents:" + Arrays.asList(ad));
//
//        // Add a new document with a generated ID
//        db.collection("users")
//                .document(uid)
//                .collection("users_classified_ads")
//                .document("Hello")
//                .set(ad, SetOptions.merge())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void Void) {
//                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        Log.w(TAG, "User Successfully Registered");
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//
//        hideProgressBar();
//
//    }

//    private void setAdDetailsToUser_Info() {
//
//        // Get Current User:
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String uid = currentUser.getUid();
//
//        // Create a new user with a first and last name:
//        Map<String, Object> ad_info = new HashMap<>();
//        ad_info.put("ad_title", adTitle.getText().toString());
//        ad_info.put("ad_desc", adDesc.getText().toString());
//        ad_info.put("ad_price", adPrice.getText().toString());
//        ad_info.put("ad_loc", adPrice.getText().toString());
//
//        // Place the new data in the database:
//        db.collection("users")
//                .document(uid)
//                .collection("users_classified_ads")
//                .document("Hello")
//                .update(ad_info)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void Void) {
//                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                        Log.w(TAG, "User Successfully Registered");
//                    }
//                })
//
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });
//
//    }

    private void storeAdvert(Map<String, Object> advert_info) {

        /*
        A method used to store the advert data in a separate collection,
        only populated by classified listings.
         */

        // Append more advert info to HashMap:
        advert_info.put("ad_title", adTitle.getText().toString());
        advert_info.put("ad_desc", adDesc.getText().toString());
        advert_info.put("ad_price", adPrice.getText().toString());
        advert_info.put("ad_loc", adPrice.getText().toString());

        // Place the new data in the database:
        db.collection("classified_ads")
                .add(advert_info)

                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
