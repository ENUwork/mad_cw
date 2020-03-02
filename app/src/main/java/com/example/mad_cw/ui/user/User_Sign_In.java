package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User_Sign_In extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserSignIn";

    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In
        setContentView(R.layout.user_sign_in);

        // Views:
        emailField = findViewById(R.id.userEmail);
        passwordField = findViewById(R.id.userPassword);
//        setProgressBar(R.id.progressBar);

        // Buttons & Click Events:
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.sign_Up_HyperLink).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            // FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);

                            // Access the User Profile / Portal
                            Intent userProfile = new Intent(User_Sign_In.this, User_Profile.class);
                            startActivity(userProfile);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(User_Sign_In.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // updateUI(null);
                        }

                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {
                            Toast.makeText(User_Sign_In.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                         hideProgressBar();

                        // [END_EXCLUDE]
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        // Validate Email:
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        // Validate Password Field
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    // Handling Click Events:
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.signInButton:
                signIn(emailField.getText().toString(), passwordField.getText().toString());
                break;

            case R.id.sign_Up_HyperLink:
                Intent sign_up = new Intent(this, User_Sign_Up.class);
                startActivity(sign_up);
                break;
        }
    }

}
