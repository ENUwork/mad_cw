package com.example.mad_cw.ui.adverts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.adapters.ViewPagerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// Getting File Directories:

/* This class is responsible for handling individual adverts
   that are passed along from the adverts_layout.xml (View_ads.java)
   and display fully the advert in great detail.
 */

public class AdvertInfoActivity extends AppCompatActivity implements View.OnClickListener {

    // Class Variables:

    private static final String TAG = "AdvertInfo";

    private TextView adTitle, adPrice, adLoc;
    private String adUid;
    private ImageView AdImg;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private ImageButton favBtn_off, favBtn_on;

    private FirebaseAuth mAuth;

    // Access a Cloud Firestore instance from the Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Individual Advert Layout:
        setContentView(R.layout.advert_detail);

        // Layout views:
        adTitle = (TextView) findViewById(R.id.ad_title_txt);
        // AdImg = (ImageView) findViewById(R.id.advert_image);
        adPrice = (TextView) findViewById(R.id.ad_price_txt);
        adLoc = (TextView) findViewById(R.id.ad_loc_txt);
        viewPager = (ViewPager) findViewById(R.id.viewPager_ad);
        favBtn_off = (ImageButton) findViewById(R.id.fav_btn);
        favBtn_on =  (ImageButton) findViewById(R.id.fav_btn2);

        // Set Click Listeners:
        findViewById(R.id.fav_btn).setOnClickListener(this);
        findViewById(R.id.fav_btn2).setOnClickListener(this);

        // Access the Passed data through the intent:
        AdvertsModel advert = (AdvertsModel) getIntent().getParcelableExtra("advert");

        // Check for Object to contain data:
        if (advert != null) {

            // Assign Object Data to the target layout components:
            viewPagerAdapter = new ViewPagerAdapter(this, advert.getImages());
            viewPager.setAdapter(viewPagerAdapter);

            // Assign AD_Uid:
            adUid = advert.getDocumentId();

            // Glide.with(this).load(advert.getImages().get(1)).into(AdImg);

            // Set Advert Content:
            adTitle.setText(advert.getAd_title());
            adPrice.setText(advert.getAd_price());
            adLoc.setText(advert.getAd_title());

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        Verify if user is signed in,
        check if this ad is one of users favourite ads,
        if so, change the favourite icon accordingly.
        */

         // FirebaseUser currentUser = mAuth.getCurrentUser();
         verify_fav_ad();
    }

    @Override
    public void onClick(View v) {

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        switch (v.getId()) {

            // Remove from Favourites:
            case R.id.fav_btn2:
                Toast.makeText(this, "Removed from Favourites", Toast.LENGTH_SHORT).show();
                removeFavourite(userUid, adUid);
                break;


            // Set Add as Favourite:
            case R.id.fav_btn:
                Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
                addToFavourites(userUid, adUid);
                break;
        }

    }

    private void removeFavourite(String userUid, String adUid) {

        // Add advert to fav_ads user array:
        db.collection("users")
                .document(userUid)
                .update("fav_ads", FieldValue.arrayRemove(adUid))

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.w(TAG, "User Successfully Registered");

                        // Hide-Show Buttons Respectively:
                        favBtn_off.setVisibility(View.VISIBLE);
                        favBtn_on.setVisibility(View.GONE);

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void addToFavourites(String userUid, String adUid) {

        // Add advert to fav_ads user array:
        db.collection("users")
                .document(userUid)
                .update("fav_ads", FieldValue.arrayUnion(adUid))

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.w(TAG, "User Successfully Registered");

                        // Hide-Show Buttons Respectively:
                        favBtn_off.setVisibility(View.GONE);
                        favBtn_on.setVisibility(View.VISIBLE);

                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void verify_fav_ad() {

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        db.collection("users")
                .document(userUid)
                .get()

        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                List<String> fav_list = (List<String>) document.get("fav_ads");

                for (String element : fav_list) {

                    System.out.println(element);

                    if (adUid.equals(element)){

                        // Hide-Show Buttons Respectively:
                        favBtn_off.setVisibility(View.GONE);
                        favBtn_on.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

    }

}
