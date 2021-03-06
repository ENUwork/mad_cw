package com.pedalT.app.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pedalT.app.BaseActivity;
import com.pedalT.app.R;
import com.pedalT.app.ui.adverts.AdvertsModel;
import com.pedalT.app.ui.adverts.adapters.AdvertsList_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/* (This) Class Deals with the Display of Users favorite Ads */

public class UserPersonalAds_Activity extends BaseActivity implements View.OnClickListener {

    // Fragment (Class) Variables:

    // private static final String TAG = "UserFavAds"; // [TEST/DEV]

    private RecyclerView mAdverts_List;
    private AdvertsList_Adapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private SearchView searchField;
    private ImageButton backBtnPress;

    private List<String> fav_ads_list;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    // ______________
    // Activity Cycle Methods:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In
        setContentView(R.layout.user_favourite_ads_layout);

        // Instantiating Local Class Variables:
        searchField = (SearchView) findViewById(R.id.searchField);
        backBtnPress = findViewById(R.id.backBtn);

        // Set onCLickListeners:
        backBtnPress.setOnClickListener(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        searchQuery();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsList_Adapter(adverts_Model_list);

        // Instantiate ArrayList<>
        fav_ads_list = new ArrayList<>();

        // Recycler View Config:
        mAdverts_List = (RecyclerView) findViewById(R.id.adverts_recycler_view);
        mAdverts_List.setHasFixedSize(true);
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2); // (Context context, int spanCount)

        // Declare the assigned Layout:
        mAdverts_List.setLayoutManager(mLayoutManager);

        getFavouriteAds();
    }

    // ______________
    // Custom Methods:

    private void getFavouriteAds() {

        /* (This) Class Method gets all of the documents from Firebase Firestore
            and then acts accordingly
         */

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        // Get the list of favourite ads from the user db:
        db.collection("users")
                .document(userUid)
                .get()

                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        List<String> fav_list = (List<String>) document.get("fav_ads");
                        if (fav_list != null){
                            fav_ads_list.addAll(fav_list);
                            queryFavouriteAds();
                        }
                    }
                });
    }

    private void queryFavouriteAds() {

        // Use those UID to populate the advertsModel Object, and build up a Recycler View:
        for (String element : fav_ads_list) {

            System.out.println(element);

            db.collection("classified_ads")
                    .document(element)
                    .get()

                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot document = task.getResult();

                            System.out.println(document);

                            AdvertsModel advertsModel = document.toObject(AdvertsModel.class);
                            adverts_Model_list.add(advertsModel);

                            advertsListAdapter.notifyDataSetChanged();
                        }
                    });
        }

    }

    private void searchQuery() {

        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // collapse the view ?
                //menu.findItem(R.id.menu_search).collapseActionView();
                Log.e("queryText",query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // search goes here !!
                // listAdapter.getFilter().filter(query);

                // filter your list from your input
                filter(newText);
                //you can use runnable postDelayed like 500 ms to delay search text

                Log.e("queryText",newText);
                return false;
            }
        });
    }

    private void filter(String text){

        List<AdvertsModel> temp = new ArrayList();

        for(AdvertsModel d: adverts_Model_list){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getAd_title().contains(text)){
                temp.add(d);
            }
        }
        // Update Recycler View
        advertsListAdapter.updateList(temp);
    }

    // _____________________
    // user action methods:

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.backBtn:
                finish();
                break;
        }
    }
}
