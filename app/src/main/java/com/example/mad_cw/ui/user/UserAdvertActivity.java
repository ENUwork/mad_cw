package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.user.adapters.UserAdImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdvertActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, UserAdImageAdapter.ItemClickListener {

    /*
        Managing User Classified Ads Posting
     */

    /* Class Variables */

    private static final String TAG = "User Profile Post Add";

    private static final int PICK_IMAGE = 1;
    private Uri ImageUri;
    private int upload_count = 0;
    private int counter = 0;

    // Class Local Temp. Storage | Var:
    private List<String> imagesList = new ArrayList<>();
    private ArrayList<Uri> ImageList = new ArrayList<>();
    private Map<String, Object> advert_info = new HashMap<>();
    private EditText adPrice, adLoc, adTitle, adDesc;
    private String advert_uid;

    // Displaying Selected Images:
    private RecyclerView imgRecycler;
    private UserAdImageAdapter imageListAdapter;

    // Access a Cloud Firestore instance from the Activity:
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Access to Firebase Authentication from the Activity:
    private FirebaseAuth mAuth;

    // _____________________
    // class activity cycles:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Post Advert:
        setContentView(R.layout.user_post_ad);

        // Progress Bar:
        setProgressBar(R.id.progressBar);

        // Layout View:
        adPrice = findViewById(R.id.post_ad_input_price);
        adDesc = findViewById(R.id.post_ad_input_desc);
        adLoc = findViewById(R.id.post_ad_input_location);
        adTitle = findViewById(R.id.post_ad_input_title);

        // Set Event Clicks
        findViewById(R.id.post_ad_select_img_btn).setOnClickListener(this);
        findViewById(R.id.post_ad_upload_btn).setOnClickListener(this);

        // ____________
        // Set Spinner:
        Spinner spinner = (Spinner) findViewById(R.id.post_ad_select_condition);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.item_condition_wear, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // _____________________
    // class click & select events handler:

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // Updating Profile Picture
            case R.id.post_ad_select_img_btn:
                selectAdImages();
                break;

            // Save Profile Details
            case R.id.post_ad_upload_btn:
                storeAdvert(advert_info);
                handleImgUpload();
                break;

        }

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        System.out.println(parent.getItemAtPosition(pos));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onItemClick(View view, int position) {

        Toast.makeText(this, "You clicked " + imageListAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // _____________________
    // image selection & handling methods:

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int countClipData = data.getClipData().getItemCount();
                int currentImageSelect = 0;

                while (currentImageSelect < countClipData){

                    ImageUri = data.getClipData().getItemAt(currentImageSelect).getUri();

                    ImageList.add(ImageUri);

                    currentImageSelect = currentImageSelect + 1;
                }
                displayAdImages();
            }
        }
    }

    private void selectAdImages() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image(s)");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void displayAdImages() {

        //TODO:
        // Get the stream of images selected and populate the recycler view with those images,
        // and on click on them, you can either delete them or view them in bigger resolution.

        // Setup the Recycler View:
        RecyclerView recyclerView = findViewById(R.id.recycler_view_img);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5); // (Context context, int spanCount)
        // Declare the assigned Layout:
        recyclerView.setLayoutManager(mLayoutManager);

        imageListAdapter = new UserAdImageAdapter(this, ImageList);
        imageListAdapter.setClickListener(this);
        recyclerView.setAdapter(imageListAdapter);

    }

    private void handleImgUpload(){

        showProgressBar();

        // Initializing Storage Location
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ClassifiedAds_Img");

        for( upload_count = 0; upload_count < imageListAdapter.getmData().size(); upload_count++ ) {

            Log.w(TAG, Integer.toString(upload_count));

            Uri IndividualImage = imageListAdapter.getmData().get(upload_count);
            final StorageReference ImageName = ImageFolder.child("Image" + IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            counter++;

                            String url = String.valueOf(uri);
                            Log.w(TAG, url);
                            Log.w(TAG, Integer.toString(counter));

                            // Store Image_Links:
                            // imagesList.add(url);
                            // advert_info.put("images", FieldValue.arrayUnion(url));
                            System.out.println(advert_info.toString()); // method 1

                            // Place the new data in the database:
                            db.collection("classified_ads")
                                    .document(advert_uid)
                                    .update("images", FieldValue.arrayUnion(url))

                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot written Correctly");

                                        }
                                    })

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        }
                    });
                }
            });
        }
    }

    private void storeAdvert(Map<String, Object> advert_info) {

        /*
        A method used to store the advert data in a separate collection,
        only populated by classified listings.
         */

        System.out.println(advert_info.toString()); // method 1


        // Append more advert info to HashMap:
        advert_info.put("ad_title", adTitle.getText().toString());
        advert_info.put("ad_desc", adDesc.getText().toString());
        advert_info.put("ad_price", adPrice.getText().toString());
        advert_info.put("ad_loc", adPrice.getText().toString());

        // Place the new data in the database:
        db.collection("classified_ads")
                .add(advert_info)

                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                        // Store the Advert UID For Posting the Images:
                        advert_uid = documentReference.getId();
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
