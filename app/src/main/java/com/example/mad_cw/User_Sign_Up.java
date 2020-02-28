package com.example.mad_cw;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

// Androidx Imports
import androidx.annotation.NonNull;

// Google Android & Firebase Imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// Java Imports
import java.util.HashMap;
import java.util.Map;

public class User_Sign_Up extends BaseActivity implements View.OnClickListener {

    // Logcat Handling
    private static final String TAG = "UserSignIn";

    // Sign Up Input Fields
    private EditText locationField;
    private EditText usernameField;
    private EditText emailField;
    private EditText passwordField;

    // Access a Cloud Firestore instance from the Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access to Firebase Authentication Instance
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sign-Up Layout
        setContentView(R.layout.user_sign_up);

        // Views Input Fields
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.userEmail);
        passwordField = findViewById(R.id.userPassword);
        locationField = findViewById(R.id.location_input);

        // Progress bar
        setProgressBar(R.id.progressBar);

        // Click Events Handler
        findViewById(R.id.signUpButton).setOnClickListener(this);
        findViewById(R.id.sign_In_HyperLink).setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.signUpButton:

                showProgressBar();

                createAccount(emailField.getText().toString(),
                        passwordField.getText().toString(),
                        locationField.getText().toString(),
                        usernameField.getText().toString());
                break;

            case R.id.sign_In_HyperLink:
                Intent sign_in = new Intent(this, User_Sign_In.class);
                startActivity(sign_in);
                break;
        }
    }

    private void createAccount(final String email, final String password, final String location, final String username) {

        // Logcat [Test]
        Log.d(TAG, "createAccount:" + email);

        // Check for Form Validation
        if (!validateForm()) {
            return;
        }

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
                            Intent userProfile = new Intent(User_Sign_Up.this, User_Profile.class);
                            startActivity(userProfile);

                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(User_Sign_Up.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
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

}
