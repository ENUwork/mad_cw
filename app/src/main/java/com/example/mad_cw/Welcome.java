package com.example.mad_cw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Welcome extends BaseActivity implements View.OnClickListener {

    // Main Method

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view_ads Layout
        setContentView(R.layout.welcome_layout);

        // Layout View

        // Click Events Setters
        findViewById(R.id.button).setOnClickListener(this);

        // Initialize Firebase Auth

    }

    @Override
    public void onStart() {
        super.onStart();

        /* (This) Class Method verifies if the user is already signed-in,
           if not, it will present with a sign in/up or skip sign in/up
           activity. Otherwise, it will redirect the user to the main
           activity/page.
         */

        // FirebaseUser currentUser = mAuth.getCurrentUser();

        // updateUI(currentUser);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button:

                Intent view_ads = new Intent(this, View_Ads.class);
                startActivity(view_ads);
                break;

        }

    }
}