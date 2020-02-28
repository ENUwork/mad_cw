package com.example.mad_cw;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class Advert_Details extends AppCompatActivity {

    private TextView AdTitle;
    private ImageView AdImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the target layout file:
        setContentView(R.layout.advert_detail);

        // Layout views:
        AdTitle = (TextView) findViewById(R.id.advert_title);
        AdImg = (ImageView) findViewById(R.id.advert_image);

        // Access the Passed data through the intent:
        Adverts advert = (Adverts) getIntent().getParcelableExtra("advert");

        // Check for Object to contain data:
        if (advert != null) {

            // Assign Object Data to the target layout components:
            AdTitle.setText(advert.getAd_title());
            Glide.with(this).load(advert.getImage_link()).into(AdImg);
        }
    }
}
