package com.example.mad_cw;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User_Sign_Up extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserSignIn";

    private EditText locationField;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sign-Up Layout
        setContentView(R.layout.user_sign_up);

        // Views
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.userEmail);
        passwordField = findViewById(R.id.userPassword);
        locationField = findViewById(R.id.location_input);
//        setProgressBar(R.id.progressBar);

        // Buttons
        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.sign_In_HyperLink).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(User_Sign_Up.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        // validate User Name:
        // Check that the user name does not exist in the database:

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

        // Validate Location:
        // check that the target location exists in the designated country.

        return valid;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.signUpButton:
                createAccount(emailField.getText().toString(), passwordField.getText().toString());
                break;

            case R.id.sign_In_HyperLink:
                Intent sign_in = new Intent(this, User_Sign_In.class);
                startActivity(sign_in);
                break;
        }
    }
}
