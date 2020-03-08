package com.example.mad_cw.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.adverts.adapters.AdvertsList_Adapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Class Variables:

    private static final String TAG = "HomeView";

    private RecyclerView mAdverts_List;
    private AdvertsList_Adapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // _____________________
    // class fragment cycles:

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsList_Adapter(adverts_Model_list);

        // Recycler View Config:
        mAdverts_List = (RecyclerView) root.findViewById(R.id.adverts_recycler_view);
        // mAdverts_List.setHasFixedSize(true);
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2); // (Context context, int spanCount)

        // Declare the assigned Layout:
        mAdverts_List.setLayoutManager(mLayoutManager);

        // Buy - Sell Container, Display first 2 Adverts (Ord. by Newest):
        getAdvertsData();

        // Image of the Day Display:

        return root;
    }


    // _____________________
    // data-intent handling methods:

    private void getAdvertsData() {

        /* Method used to get the first 2 (newest ads first) and populate the "new" ads container */

        db.collection("classified_ads")
                .orderBy("post_time", Query.Direction.DESCENDING)
                .limit(2)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d(TAG, "Error : " + e.getMessage());
                        }

                        // On Document Change (Add), retrieve data:
                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                            // Listen for new Documents:
                            if (doc.getType() == DocumentChange.Type.ADDED) {

                            // [Test]
                                String advert_title = doc.getDocument().getString("ad_title");
                                Log.d(TAG, "Advert : " + advert_title);

                                AdvertsModel advertsModel = doc.getDocument().toObject(AdvertsModel.class);
                                adverts_Model_list.add(0, advertsModel);

                                advertsListAdapter.notifyItemInserted(0);
                                // advertsListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

}