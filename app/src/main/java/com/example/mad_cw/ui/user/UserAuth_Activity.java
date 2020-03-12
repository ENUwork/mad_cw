package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserAuth_Activity extends BaseActivity implements View.OnClickListener {

    /*
        User Sign In/Up Using Email & Password
     */

    // Class Variables:
    private static final String TAG = "UserSignAuth";

    private EditText emailField, passField;
    private EditText unameField_UP, passField_UP, locField_UP, emailField_UP;

    private LinearLayout signUpLayout, signInLayout;

    private FirebaseAuth mAuth;

    // Access a Cloud Firestore instance from the Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // _____________________
    // class activity cycles:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In
        setContentView(R.layout.user_auth_layout);

        // SIGN IN Views:
        signInLayout = findViewById(R.id.sign_in_layout);
        emailField = findViewById(R.id.userEmail);
        passField = findViewById(R.id.userPassword);

        // SIGN UP Views:
        signUpLayout = findViewById(R.id.sign_up_layout);
        unameField_UP = findViewById(R.id.username);
        emailField_UP = findViewById(R.id.userEmail2);
        passField_UP = findViewById(R.id.userPassword2);
        locField_UP = findViewById(R.id.location_input);

        setProgressBar(R.id.progressBar);

        // SIGN IN Buttons & Click Events:
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.sign_Up_HyperLink).setOnClickListener(this);

        // SIGN UP Buttons & Click Events:
        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.sign_In_HyperLink).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verify if user is already logged in:
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null ){
            finish();
            startActivity(new Intent(this, UserProfile_Activity.class));
        }

    }

    // _____________________
    // class click events handler:

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sign_In_HyperLink:

                signInLayout.setVisibility(View.VISIBLE);
                signUpLayout.setVisibility(View.GONE);
                break;

            case R.id.signInButton:
                // Check for Form Validation
                if (!validateSignInForm()) {
                    break;
                }
                showProgressBar();
                signIn(emailField.getText().toString(), passField.getText().toString());
                break;

            case R.id.sign_Up_HyperLink:

                signInLayout.setVisibility(View.GONE);
                signUpLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.signUpButton:
                // Check for Form Validation
                if (!validateSignUpForm()) {
                    break;
                }
                showProgressBar();
                createAccount(emailField_UP.getText().toString(),
                        passField_UP.getText().toString(),
                        locField_UP.getText().toString(),
                        unameField_UP.getText().toString());
                break;

        }
    }

    // _____________________
    // user sign in/up methods:

    private void signIn(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            // FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);

                            // Redirect to User Profile
                            finish();
                            Intent userProfile = new Intent(UserAuth_Activity.this, UserProfile_Activity.class);
                            startActivity(userProfile);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(UserAuth_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            // updateUI(null);
                        }

                        // [START_EXCLUDE]

                        if (!task.isSuccessful()) {
                            Toast.makeText(UserAuth_Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        hideProgressBar();

                        // [END_EXCLUDE]
                    }
                });
    }

    private void createAccount(final String email, final String password, final String location, final String username) {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            // Store the rest of the data
                            storeData(email, location, username);

                            // Access the User Profile / Portal
                            Intent userProfile = new Intent(UserAuth_Activity.this, UserProfile_Activity.class);
                            startActivity(userProfile);

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserAuth_Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                        hideProgressBar();
                    }
                });
    }

    private void storeData(String email, String location, String username) {

        // Get Current User:
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("location", location);
        user.put("username", username);

        // Add a new document with a generated ID
        db.collection("users")
                .document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void Void) {
                        // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Log.w(TAG, "User Successfully Registered");
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
    // user sign in/up validation methods:

    private boolean validateSignInForm() {
        boolean valid = true;

        // Validate Email:
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            emailField.setError("Input Valid Email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        // Validate Password Field
        String password = passField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passField.setError("Required.");
            valid = false;
        } else {
            passField.setError(null);
        }

        return valid;
    }

    private boolean validateSignUpForm() {
        boolean valid = true;

        // Validate Email:
        String email = emailField_UP.getText().toString();
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            emailField_UP.setError("Input Valid Email");
            valid = false;
        } else {
            emailField_UP.setError(null);
        }

        // Validate Password Field
        String password = passField_UP.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passField_UP.setError("Required.");
            valid = false;
        } else {
            passField_UP.setError(null);
        }

        // Validate Username Field
        String userName = unameField_UP.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            unameField_UP.setError("Required.");
            valid = false;
        } else {
            unameField_UP.setError(null);
        }

        // Validate Location Field
        String location = locField_UP.getText().toString();
        if (TextUtils.isEmpty(location)) {
            locField_UP.setError("Required.");
            valid = false;
        } else {
            locField_UP.setError(null);
        }

        return valid;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}