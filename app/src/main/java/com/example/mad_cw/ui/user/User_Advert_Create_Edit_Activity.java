package com.example.mad_cw.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.user.adapters.User_Advert_Image_Select_Adapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class User_Advert_Create_Edit_Activity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, User_Advert_Image_Select_Adapter.ItemClickListener {

    /*
        Managing User Classified Adverts Creation & Editing:
     */

    // Class Variables:
    private static final String TAG = "User Profile Post Add";

    private static final int PICK_IMAGE = 1;
    private int counter = 0;

    // Class Local Temp. Storage | Var:
    private ArrayList<String> ImageStringList = new ArrayList<>();
    private ArrayList<String> old_images = new ArrayList<>();
    private Map<String, Object> advert_info = new HashMap<>();

    private EditText adPrice, adLoc, adTitle, adDesc, adAge;
    private TextView upCount;
    private String advert_uid;
    private Spinner spinnerCondition, spinnerWheels, spinnerFrame;
    private ImageButton selectImgBtn;
    private Button createAdBtn, updateAdBtn, deleteAdBtn;

    // Displaying Selected Images:
    private RecyclerView recyclerView;
    private User_Advert_Image_Select_Adapter imageListAdapter;

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
        setContentView(R.layout.user_advert_create_edit);

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
        createAdBtn = findViewById(R.id.create_ad_btn);
        updateAdBtn = findViewById(R.id.update_ad_btn);
        selectImgBtn = findViewById(R.id.select_ad_img_btn);
        deleteAdBtn = findViewById(R.id.delete_ad_btn);

        // Setup the Recycler View, set Gallery as Layout, and assign it:
        recyclerView = findViewById(R.id.recycler_view_img);
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 3); // (Context context, int spanCount)
        recyclerView.setLayoutManager(mLayoutManager);

        // Set Event Clicks:
        selectImgBtn.setOnClickListener(this);
        createAdBtn.setOnClickListener(this);
        updateAdBtn.setOnClickListener(this);
        deleteAdBtn.setOnClickListener(this);

        // Set Values:
        upCount.setText(getString(R.string.img_count, "0/10"));

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        spinnerData();
        dragAndDropReOrder();

        // Check for incoming Intent Data:
        AdvertsModel advert = getIntent().getParcelableExtra("advert");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check for Object to contain data:
        if (advert != null && currentUser != null && advert.getAd_owner().equals(currentUser.getUid())) {
            Toast.makeText(this, "Data Packet Received", Toast.LENGTH_LONG).show();

            // Modify Layout Accordingly:
            createAdBtn.setVisibility(View.GONE);
            updateAdBtn.setVisibility(View.VISIBLE);
            deleteAdBtn.setVisibility(View.VISIBLE);

            // Populate With data:
            advertEditData(advert);
            advert_uid = advert.getDocumentId();
        }
    }

    // _____________________
    // class listeners events handler:

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.select_ad_img_btn:
                selectAdvertImages();
                break;

            case R.id.delete_ad_btn:
                deleteAdvert();
                break;

            case R.id.update_ad_btn:
            case R.id.create_ad_btn:

                /*
                if (!advertValidation()) {
                    Toast.makeText(this, "Uh-oh, please fill correctly all fields :)", Toast.LENGTH_LONG).show();
                    break;
                }
                 */

                imgReUploadValidation();
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        // Get Current RecyclerView Count
        int img_track = imageListAdapter.getmData().size() - 1;

        // Update Image Count
        upCount.setText(getString(R.string.img_count,  img_track + "/10"));

        // Disable Add Images Btn.
        if (img_track == 10) {
            selectImgBtn.setVisibility(View.GONE);
        } else {
            selectImgBtn.setVisibility(View.VISIBLE);
        }

        Toast.makeText(this, "You clicked " + imageListAdapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        System.out.println(parent.getItemAtPosition(pos));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
                    String ImageString = data.getClipData().getItemAt(currentImageSelect).getUri().toString();
                    ImageStringList.add(ImageString);
                    currentImageSelect = currentImageSelect + 1;
                }

                displayAdvertImages(ImageStringList);
            }
        }
    }

    private void selectAdvertImages() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image(s)");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    private void displayAdvertImages(ArrayList<String> ImageList) {

        imageListAdapter = new User_Advert_Image_Select_Adapter(this, ImageList);
        imageListAdapter.setClickListener(this);
        recyclerView.setAdapter(imageListAdapter);

        // Update Img Counter Track
        upCount.setText(getString(R.string.img_count,  imageListAdapter.getmData().size() + "/10"));

        if (imageListAdapter.getmData().size() == 10) {
            // Disable Add Images Btn.
            findViewById(R.id.select_ad_img_btn).setVisibility(View.GONE);
        }
    }

    // _____________________
    // data parsing & upload methods:

    private void handleImgUpload(final ArrayList<String> newImgStringList, final ArrayList<String> currentImgList){

        showProgressBar();

        // Initializing Storage Location
        StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ClassifiedAds_Img");

        for(int upload_count = 0; upload_count < currentImgList.size(); upload_count++) {
            Log.w(TAG, Integer.toString(upload_count));
            Uri IndividualImage = Uri.parse(currentImgList.get(upload_count));
            final StorageReference ImageName = ImageFolder.child("Image" + IndividualImage.getLastPathSegment());

            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            newImgStringList.add(url);
                            counter++;

                            if (counter == currentImgList.size() && advert_uid != null){
                                updateAdvert(newImgStringList);
                            }
                            else if (counter == currentImgList.size()) {
                                createAdvert(newImgStringList);
                            }
                        }
                    });
                }
            });
        }
    }

    private void createAdvert(ArrayList<String> newImgStringList) {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_uid = currentUser.getUid();

        // Get a new write batch
        WriteBatch batch = db.batch();

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
        advert_info.put("images", newImgStringList);

        // Set the value of 'NYC'
        DocumentReference nycRef = db.collection("classified_ads").document();

        batch.set(nycRef, advert_info);

        // Place the new data in the database:
        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getBaseContext(), "\uD83C\uDF89 Success! Your advert is now live!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "\uD83D\uDE31 Uh-Oh! Something went wrong", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateAdvert(ArrayList<String> newImgStringList) {

        //TODO:
        // Delete/Remove Values from the "images" filed, due to image order rearranging
        // Disallow the updating of the "time" field, to preserve the original post_time:

        // Get a new write batch
        WriteBatch batch = db.batch();
        DocumentReference exist_doc = db.collection("classified_ads").document(advert_uid);

        // Append more advert single:
        advert_info.put("ad_title", adTitle.getText().toString());
        advert_info.put("ad_desc", adDesc.getText().toString());
        advert_info.put("ad_price", adPrice.getText().toString());
        advert_info.put("ad_loc", adLoc.getText().toString());
        advert_info.put("ad_age", adAge.getText().toString());
        advert_info.put("ad_other", Arrays.asList(spinnerCondition.getSelectedItem().toString(),
                spinnerWheels.getSelectedItem().toString(), spinnerFrame.getSelectedItem().toString()));
        advert_info.put("images", newImgStringList);

        // Testing: Using just set:
        batch.update(exist_doc, advert_info);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getBaseContext(), "\uD83C\uDF89 Success! Your advert has been updated", Toast.LENGTH_LONG).show();
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), "\uD83D\uDE31 Uh-Oh! Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteAdvert() {

        // Delete Images First:
        deleteAdvertImg();

        // Get a new write batch
        WriteBatch batch = db.batch();
        DocumentReference exist_doc = db.collection("classified_ads").document(advert_uid);

        // Delete Advert:
        batch.delete(exist_doc);

        batch.commit()
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getBaseContext(), "\uD83C\uDF89 Success! Your advert has been removed", Toast.LENGTH_LONG).show();
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), "\uD83D\uDE31 Uh-Oh! Something went wrong", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void deleteAdvertImg() {

        for (String img_link : old_images){

            // Get the Image Cloud Location:
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(img_link);

            // Delete Image:
            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }
    }

    // _____________________
    // user actions:

    private void dragAndDropReOrder() {

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT |
                ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(ImageStringList, fromPosition, toPosition);
                imageListAdapter.notifyItemMoved(fromPosition, toPosition);
                imageListAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // _____________________
    // validations:

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

    private void imgReUploadValidation() {

        // Check for new images to upload:
        ArrayList<String> currentImgList = new ArrayList<>(imageListAdapter.getmData());
        ArrayList<String> newImgStringList = new ArrayList<>(imageListAdapter.getmData());

        // Identify New Links:
        currentImgList.removeAll(old_images);

        // Identify Already Existing Links:
        newImgStringList.retainAll(old_images);

        // New Images Found,
        if (currentImgList.size() > 0) {
            handleImgUpload(newImgStringList, currentImgList);
        } else if (advert_uid != null && currentImgList.size() == 0){
            updateAdvert(newImgStringList);
        }
    }

    // _____________________
    // class data population methods:

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

    private void advertEditData(AdvertsModel advert) {

        // Text Info
        adTitle.setText(advert.getAd_title());
        adDesc.setText(advert.getAd_desc());
        adPrice.setText(advert.getAd_price());
        adLoc.setText(advert.getAd_loc());
        adAge.setText(advert.getAd_age());
        upCount.setText(getString(R.string.img_count,  advert.getImages().size() + "/10"));

        // Spinner Info
        for (int i = 0; i < spinnerCondition.getAdapter().getCount(); i++){
            if (spinnerCondition.getAdapter().getItem(i).toString().contains(advert.getAd_other().get(0))) {
                spinnerCondition.setSelection(i);
            }
        }
        for (int i = 0; i < spinnerWheels.getAdapter().getCount(); i++){
            if (spinnerWheels.getAdapter().getItem(i).toString().contains(advert.getAd_other().get(1))) {
                spinnerWheels.setSelection(i);
            }
        }
        for (int i = 0; i < spinnerFrame.getAdapter().getCount(); i++){
            if (spinnerFrame.getAdapter().getItem(i).toString().contains(advert.getAd_other().get(2))) {
                spinnerFrame.setSelection(i);
            }
        }

        // Image Info, from string to URI
        ImageStringList.addAll(advert.getImages());
        old_images.addAll(advert.getImages());
        displayAdvertImages(ImageStringList);
    }

}
