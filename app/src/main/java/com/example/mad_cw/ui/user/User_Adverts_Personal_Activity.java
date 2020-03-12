package com.example.mad_cw.ui.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.adverts.adapters.AdvertsList_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class User_Adverts_Personal_Activity extends BaseActivity {

    // Fragment (Class) Variables:

    private static final String TAG = "UserFavAds";

    private RecyclerView mAdverts_List;
    private AdvertsList_Adapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private SearchView searchField;

    private List<String> fav_ads_list;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    // ______________
    // activity cycle Methods:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In
        setContentView(R.layout.user_personal_ads_layout);

        // Instantiating Local Class Variables:
        searchField = (SearchView) findViewById(R.id.searchField);

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

        // Set the Recycler View as a Linear Row-by-Row view:
        // mAdverts_List.setLayoutManager(new LinearLayoutManager(this));

        // Declare the assigned Layout:
        mAdverts_List.setLayoutManager(mLayoutManager);

        getPersonalAds();
    }

    // ________________
    // handling adverts methods:

    private void getPersonalAds() {

        /* (This) Class Method gets all of the documents from Firebase Firestore
            and then acts accordingly
         */

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        // Get the list of favourite ads from the user db:
        db.collection("classified_ads")
            .whereEqualTo("ad_owner", userUid)
            .get()

            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adverts_Model_list.add(document.toObject(AdvertsModel.class));
                            advertsListAdapter.notifyDataSetChanged();
                        }
                        advertsListAdapter.notifyDataSetChanged();
                    }
                }
            });
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

}
