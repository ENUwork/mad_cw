package com.example.mad_cw;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

/* This class is responsible for handling individual adverts
   that are passed along from the adverts_layout.xml (View_ads.java)
   and display fully the advert in great detail.
 */

public class Advert_Details extends AppCompatActivity {

    // Class Variables:
    private TextView AdTitle, AdPrice;
    private ImageView AdImg;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load Individual Advert Layout:
        setContentView(R.layout.advert_detail);

        // Layout views:
        AdTitle = (TextView) findViewById(R.id.advert_title);
        AdImg = (ImageView) findViewById(R.id.advert_image);
        AdPrice = (TextView) findViewById(R.id.advert_price);
        viewPager = (ViewPager) findViewById(R.id.viewPager_ad);

        // Access the Passed data through the intent:
        Adverts advert = (Adverts) getIntent().getParcelableExtra("advert");

        // Check for Object to contain data:
        if (advert != null) {

            // Assign Object Data to the target layout components:
            viewPagerAdapter = new ViewPagerAdapter(this, advert.getImages());
            viewPager.setAdapter(viewPagerAdapter);

            // Glide.with(this).load(advert.getImages().get(1)).into(AdImg);
            AdTitle.setText(advert.getAd_title());
            AdPrice.setText(advert.getAd_price());

        }
    }
}
