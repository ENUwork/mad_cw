package com.example.mad_cw;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    This is the backbone Activity of the App. It handles all of the
    main user actions and interactions.
 */

public class Main2Activity extends AppCompatActivity {

    // ________________
    // class variables:

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseAuth mAuth;

    // ______________
    // Activity Cycle Methods:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Instantiate Load Layout:
        setContentView(R.layout.activity_main2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Dealing with the the ToolbBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Dealing with the Floating Screen Button:
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Dealing with the NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_adverts, R.id.nav_account, R.id.nav_share)
                .setDrawerLayout(drawer)
                .build();

        // Instantiating the menu:
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verify if user is already logged in:
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null ){
            updateUI(currentUser);
        }
    }

    // _____________
    // navigation drawer methods:

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // _________________
    // user UI/UX Update:

    private void updateUI(FirebaseUser user) {

        /*
        Updating Navigation Bar Layout, based on user logged in
        or not
         */

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        // Profile Image:
        Uri photoUrl = user.getPhotoUrl();

        TextView nav_user = (TextView) hView.findViewById(R.id.textView);
        ImageView profilePic = hView.findViewById(R.id.imageView);

        if (photoUrl != null) {
            // Display user Profile Details:
            Glide.with(this).load(photoUrl).into(profilePic);
        }

        // User email
        nav_user.setText(user.getEmail());
    }
}
