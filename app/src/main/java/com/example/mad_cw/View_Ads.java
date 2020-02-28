package com.example.mad_cw;


// Class that is responsible for displaying classified listings/ads
// publicly to all users.

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class View_Ads extends BaseActivity {

    // (This) - Class Variables:

    private static final String TAG = "Adverts_Page";

    private RecyclerView mAdverts_List;
    private AdvertsListAdapter advertsListAdapter;
    private List<Adverts> adverts_list;

    private EditText searchField;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Main Method

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the view_ads Layout
        setContentView(R.layout.adverts_layout);

        // Layout View

        // Dealing with search engine:
        searchField = (EditText) findViewById(R.id.editText);

        // Click Events Setters

        // Initialize Firebase Auth

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_list = new ArrayList<>();
        advertsListAdapter = new AdvertsListAdapter(adverts_list);

        // Recycler View Config:
        mAdverts_List = (RecyclerView) findViewById(R.id.adverts_recycler_view);
        mAdverts_List.setHasFixedSize(true);
        // mAdverts_List.setLayoutManager(new LinearLayoutManager(this));
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2); // (Context context, int spanCount)
        mAdverts_List.setLayoutManager(mLayoutManager);

        // Accessing the data:
        db.collection("classified_ads")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "Error : " + e.getMessage());
                }

                // Single Calls, Retrives back al of the data, even on changes:
                /*

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    String advert_title = doc.getString("ad_title");

                    Log.d(TAG, "Advert : " + advert_title);

                }

                 */

                // On Document Change retrieves only the updated data:
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        // [Test]
                         String advert_title = doc.getDocument().getString("image_link");
                         Log.d(TAG, "Advert : " + advert_title);

                        Adverts adverts = doc.getDocument().toObject(Adverts.class);
                        adverts_list.add(adverts);

                        advertsListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

        // [Test]
        // getAdvertsData();
    }

    private void getAdvertsData() {

        /* (This) Class Method gets all of the documents from Firebase Firestore
            and then acts accordingly
         */

        // Get the classified listings data
        db.collection("classified_ads")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> myListOfDocuments = task.getResult().getDocuments();

                            // Print the name from the list....
                            for(DocumentSnapshot model : myListOfDocuments) {
                                System.out.println(model);
                            }

                            Log.w(TAG, Integer.toString(myListOfDocuments.size()));
                        }
                    }
                });
    }


    // Filter Method:
    public void filter(String text){

        List<Adverts> temp = new ArrayList();

        for(Adverts d: adverts_list){
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
