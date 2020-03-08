package com.example.mad_cw.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.BaseActivity;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.adverts.adapters.AdvertsList_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatLobby_Activity extends BaseActivity {
    // Fragment (Class) Variables:

    private static final String TAG = "UserFavAds";

    private RecyclerView mAdverts_List;
    private AdvertsList_Adapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private List<String> ad_uid_list;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    // ______________
    // Activity Cycle Methods:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set Layout for User Sign In
        setContentView(R.layout.chat_lobby_layout);

        // Instantiating Local Class Variables:
        // searchField = (SearchView) findViewById(R.id.searchField);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onResume() {
        super.onResume();

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsList_Adapter(adverts_Model_list);

        // Instantiate ArrayList<>
        ad_uid_list = new ArrayList<>();

        // Recycler View Config:
        mAdverts_List = (RecyclerView) findViewById(R.id.recycler_view_chat_rooms);
        mAdverts_List.setHasFixedSize(true);
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Linear Row-by-Row view:
        mAdverts_List.setLayoutManager(new LinearLayoutManager(this));

        getChatRoom();
    }

    // ______________
    // data handling methods:

    private void getChatRoom() {

        /* (This) Class Method gets all of the documents from Firebase Firestore
            and then acts accordingly
         */

        // Get Current User UID Num.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userUid = currentUser.getUid();

        // Check if Chat Already Exists between both users, act accordingly:
        db.collection("chat").whereArrayContains("users", userUid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String uid = (String) doc.getData().get("ad_uid");
                            if (!uid.isEmpty()) {
                                ad_uid_list.add(uid);
                            }
                        }
                        queryFavouriteAds();
                    }
                });
    }

    private void queryFavouriteAds() {

        // Use those UID to populate the advertsModel Object, and build up a Recycler View:
        for (String element : ad_uid_list) {
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
}
