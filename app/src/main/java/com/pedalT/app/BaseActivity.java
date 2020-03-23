package com.pedalT.app;

import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {


    // Class Variables:
    @VisibleForTesting
    public ProgressBar mProgressBar;

    // ________________
    // class activity cycle:

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }

    // _________________
    // progress bar methods:

    public void setProgressBar(int resId) {
        mProgressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
