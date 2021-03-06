package com.pedalT.app.ui.adverts;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.pedalT.app.BaseActivity;
import com.pedalT.app.MainActivity;
import com.pedalT.app.R;
import com.pedalT.app.ui.adverts.adapters.ViewPager_Adapter;
import com.pedalT.app.ui.chat.ChatRoom_Activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

// Getting File Directories:

/* This class is responsible for handling individual adverts
   that are passed along from the adverts_layout.xml (View_ads.java)
   and display fully the advert in great detail.
 */

public class AdvertDetails_Activity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    // _____________________
    // class variables:

    private static final String TAG = "AdvertInfo";

    private TextView adTitle, adPrice, adLoc, adWheel, adFrame, adAge, adDriveT, adPostTime, adDesc, imgCount;
    private ImageView adOwnerPic;
    private ImageButton favBtn_off, favBtn_on;
    private Button contactBtn;

    private ViewPager viewPager;
    private ViewPager_Adapter viewPagerAdapter;

    private int numImg;
    private String adUid;

    private AdvertsModel advert;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // _____________________
    // class activity cycles:

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Individual Advert Layout:
        setContentView(R.layout.advert_detail);

        // Layout views:
        adTitle = findViewById(R.id.ad_title_txt);
        adPrice = findViewById(R.id.ad_price_txt);
        adLoc = findViewById(R.id.ad_loc_txt);
        adWheel = findViewById(R.id.advert_info_whsize);
        adFrame = findViewById(R.id.advert_info_frsize);
        adDriveT = findViewById(R.id.advert_info_drive_train);
        adDesc = findViewById(R.id.advert_info_desc);
        adAge = findViewById(R.id.advert_info_age);
        adPostTime = findViewById(R.id.advert_info_post_txt);
        viewPager = findViewById(R.id.viewPager_ad);
        favBtn_off = findViewById(R.id.fav_btn);
        favBtn_on = findViewById(R.id.fav_btn2);
        contactBtn = findViewById(R.id.contact_ad_owner);
        imgCount = findViewById(R.id.imgCount);
        // adOwnerPic = (ImageView) findViewById(R.id.advert_image);

        // Set Listeners:
        favBtn_off.setOnClickListener(this);
        favBtn_on.setOnClickListener(this);
        contactBtn.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Access the Passed data through the intent:
        advert = getIntent().getParcelableExtra("advert");

        // Check for Object to contain data:
        if (advert != null) {

            // Assign Object Data to the target layout components:
            viewPagerAdapter = new ViewPager_Adapter(this, advert.getImages());
            viewPager.setAdapter(viewPagerAdapter);

            // Get Number of Images:
            numImg = advert.getImages().size();

            // Set Img Counter:
            imgCount.setText(getString(R.string.ad_img_count, 1, numImg ));

            // Get Advert_Uid:
            adUid = advert.getDocumentId();

            // Dealing with dates:
            Date advert_post_date = advert.getPost_time();
            String day = (String) DateFormat.format("dd", advert_post_date);
            String month = (String) DateFormat.format("MMM", advert_post_date);
            String year = (String) DateFormat.format("yyyy", advert_post_date);

            // Populate Advert Content:
            adTitle.setText(advert.getAd_title());
            adPrice.setText(getString(R.string.ad_price2, advert.getAd_price()));
            adLoc.setText(getString(R.string.ad_loc2, advert.getAd_loc()));
            adWheel.setText(advert.getAd_other().get(1));
            adFrame.setText(advert.getAd_other().get(2));
            adDriveT.setText(advert.getAd_other().get(3));
            adDesc.setText(advert.getAd_desc());
            adAge.setText(advert.getAd_age());
            adPostTime.setText(getString(R.string.set_post_time, day, month, year));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            verify_fav_ad();
        }
    }

    // _____________________
    // class listeners events handler:

    @Override
    public void onClick(View v) {

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null){
            Toast.makeText(this, getString(R.string.feature_disabled), Toast.LENGTH_LONG).show();
            return;
        }

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

            // Open chat with advert owner:
            case R.id.contact_ad_owner:
                Intent open_chat = new Intent(this, ChatRoom_Activity.class);
                open_chat.putExtra("advert_info", advert);
                startActivity(open_chat);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        this.finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int arg0) {

        // Update Image Counter:
        imgCount.setText(getString(R.string.ad_img_count, (arg0 + 1), numImg ));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // ______________
    // user actions handling:

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

    // _____________________
    // user validation:

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
                        if (fav_list != null){
                            for (String element : fav_list) {

                                System.out.println(element);

                                if (adUid.equals(element)){

                                    // Hide-Show Buttons Respectively:
                                    favBtn_off.setVisibility(View.GONE);
                                    favBtn_on.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });

    }

}

