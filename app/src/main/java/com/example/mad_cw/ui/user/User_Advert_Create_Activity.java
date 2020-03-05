package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.user.adapters.UserAdImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User_Advert_Create_Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, UserAdImageAdapter.ItemClickListener {

    /*
        Managing User Classified Ads Posting
     */

    // Class Variables:
    private static final String TAG = "User Profile Post Add";

    private static final int PICK_IMAGE = 1;
    private Uri ImageUri;
    private int upload_count = 0;
    private int counter = 0;
    private int img_count = 0;

    // Class Local Temp. Storage | Var:
    private List<String> imagesList = new ArrayList<>();
    private ArrayList<Uri> ImageList = new ArrayList<>();
    private Map<String, Object> advert_info = new HashMap<>();
    private Map<String, List<String>> imagesHash = new HashMap<>();
    private EditText adPrice, adLoc, adTitle, adDesc, adAge;
    private TextView upCount;
    private String advert_uid;

    private Spinner spinnerCondition, spinnerWheels, spinnerFrame;

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
        setContentView(R.layout.user_advert_create);

        // Hide Support Menu Bar:
        // getSupportActionBar().hide();

        // Progress Bar:
        setProgressBar(R.id.progressBar);

        // Layout View:
        adPrice = findViewById(R.id.post_ad_input_price);
        adDesc = findViewById(R.id.post_ad_input_desc);
        adLoc = findViewById(R.id.post_ad_input_location);
        adTitle = findViewById(R.id.post_ad_input_title);
        adAge = findViewById(R.id.post_ad_input_age);

        upCount = findViewById(R.id.upload_count);

        // Set Event Clicks
        findViewById(R.id.select_ad_img_btn).setOnClickListener(this);
        findViewById(R.id.upload_ad_btn).setOnClickListener(this);

        // Set Values
        upCount.setText(getString(R.string.img_count, "0/10"));

        spinnerData();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    // _____________________
    // class listeners events handler:

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT |
            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(ImageList, fromPosition, toPosition);
            imageListAdapter.notifyItemMoved(fromPosition, toPosition);
            imageListAdapter.notifyDataSetChanged();
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.select_ad_img_btn:
                selectAdImages();
                break;

            case R.id.upload_ad_btn:
                if (!advertValidation()) {
                    Toast.makeText(this, "Uh-oh, please fill correctly all fields :)", Toast.LENGTH_LONG).show();
                    break;
                }
                storeAdvert(advert_info);
                handleImgUpload();
                finish();
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

        // Get Current RecyclerView Count
        int img_track = imageListAdapter.getmData().size() - 1;

        // Update Image Count
        upCount.setText(getString(R.string.img_count,  img_track + "/10"));

        if (img_track == 10) {
            // Disable Add Images Btn.
            findViewById(R.id.select_ad_img_btn).setVisibility(View.GONE);
        } else {
            findViewById(R.id.select_ad_img_btn).setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "You clicked " + imageListAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    // _____________________
    // image handling methods:

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

            if (data.getClipData() != null) {

                int countClipData = data.getClipData().getItemCount();
                int currentImageSelect = 0;

                if (countClipData > 10) {
                    Toast.makeText(this, "Only 10 Images allowed", Toast.LENGTH_LONG).show();
                    return;
                }

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

        // Setup the Recycler View:
        RecyclerView recyclerView = findViewById(R.id.recycler_view_img);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3); // (Context context, int spanCount)
        // Declare the assigned Layout:
        recyclerView.setLayoutManager(mLayoutManager);

        imageListAdapter = new UserAdImageAdapter(this, ImageList);
        imageListAdapter.setClickListener(this);
        recyclerView.setAdapter(imageListAdapter);

        // Update Img Counter Track
        upCount.setText(getString(R.string.img_count,  imageListAdapter.getmData().size() + "/10"));

        if (imageListAdapter.getmData().size() == 10) {
            // Disable Add Images Btn.
            findViewById(R.id.select_ad_img_btn).setVisibility(View.GONE);
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void storeAdvert(Map<String, Object> advert_info) {

        /*
        A method used to store the advert data in a separate collection,
        only populated by classified listings.
         */

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_uid = currentUser.getUid();

        // Append more advert single:
        advert_info.put("ad_title", adTitle.getText().toString());
        advert_info.put("ad_desc", adDesc.getText().toString());
        advert_info.put("ad_price", adPrice.getText().toString());
        advert_info.put("ad_loc", adLoc.getText().toString());
        advert_info.put("ad_age", adAge.getText().toString());
        advert_info.put("post_time", FieldValue.serverTimestamp());
        advert_info.put("ad_owner", user_uid);
        advert_info.put("ad_other", Arrays.asList(spinnerCondition.getSelectedItem().toString(),
                spinnerWheels.getSelectedItem().toString(), spinnerFrame.getSelectedItem().toString()));

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

    // _____________________
    // user actions:

    private void dragAndDropOrder() {

    }

    // _____________________
    // user input validation:

    private boolean advertValidation() {
        boolean valid = true;

        // Get Form Values:
        String title = adTitle.getText().toString();
        String desc = adDesc.getText().toString();
        String loc = adLoc.getText().toString();
        String cond = spinnerCondition.getSelectedItem().toString();

        // Title Validation:
        if (TextUtils.isEmpty(title)) {
            adTitle.setError("Required.");
            valid = false;
        } else {
            adTitle.setError(null);
        }

        // Description Validation:
        if (TextUtils.isEmpty(desc)) {
            adDesc.setError("Required.");
            valid = false;
        }

        // Location Validation:
        if (TextUtils.isEmpty(loc)) {
            adLoc.setError("Required.");
            valid = false;
        }

        // Location Validation:
        if (TextUtils.isEmpty(cond)) {
            ((TextView)spinnerCondition.getSelectedView()).setError("Required.");
            valid = false;
        }

        // Images Validation:

        return valid;
    }

    // _____________________
    // class data population:

    private void spinnerData() {

        // locate spinners
        spinnerCondition = (Spinner) findViewById(R.id.post_ad_select_condition);
        spinnerWheels = (Spinner) findViewById(R.id.spinnerWheelSize);
        spinnerFrame = (Spinner) findViewById(R.id.spinnerFrameSize);

        // create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterCondition = ArrayAdapter.createFromResource(this, R.array.item_condition_wear, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterWheels = ArrayAdapter.createFromResource(this, R.array.bike_wheel_size, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterFrame= ArrayAdapter.createFromResource(this, R.array.bike_frame_size, android.R.layout.simple_spinner_item);


        // specify the layout to use when the list of choices appears
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterWheels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterFrame.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply the adapter to the spinner(s)
        spinnerCondition.setAdapter(adapterCondition);
        spinnerWheels.setAdapter(adapterWheels);
        spinnerFrame.setAdapter(adapterFrame);

        // set spinner listeners:
        spinnerCondition.setOnItemSelectedListener(this);
        spinnerWheels.setOnItemSelectedListener(this);
        spinnerFrame.setOnItemSelectedListener(this);
    }

}
