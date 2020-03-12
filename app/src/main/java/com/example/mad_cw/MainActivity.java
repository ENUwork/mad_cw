package com.example.mad_cw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.mad_cw.ui.adverts.AdvertsView_Fragment;
import com.example.mad_cw.ui.user.UserAuth_Activity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
    This is the backbone Activity of the App. It handles all of the
    main user actions and interactions.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // ________________
    // class variables:

    private DrawerLayout drawer;
    private FirebaseAuth mAuth;

    // ______________
    // activity cycles:

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // SplashScreen
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        // instantiate target layout:
        setContentView(R.layout.activity_main);

        // Dealing with the the ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // initialize firebase-auth:
        mAuth = FirebaseAuth.getInstance();

        // navigationDrawer:
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation Bar Handling:
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Dealing with Rotating Screens, & initial displayed fragment:
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AdvertsView_Fragment()).commit();
            navigationView.setCheckedItem(R.id.nav_adverts);
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            /*
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
             */

            case R.id.nav_account:
                Intent open_account = new Intent(this, UserAuth_Activity.class);
                startActivity(open_account);
                break;

            case R.id.nav_adverts:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AdvertsView_Fragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // _________________
    // user UI/UX Update:

    private void updateUI(FirebaseUser user) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        TextView nav_user = (TextView) hView.findViewById(R.id.textView);
        // User email
        nav_user.setText(user.getEmail());
    }

}
