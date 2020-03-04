package com.example.mad_cw.ui.adverts;


// Class that is responsible for displaying classified listings/ads
// publicly to all users.

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.adapters.AdvertsListAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// Implementing Fragments:

public class AdvertsViewFragment extends Fragment {

    // Fragment (Class) Variables:

    private static final String TAG = "AdvertsView";

    private RecyclerView mAdverts_List;
    private AdvertsListAdapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private SearchView searchField;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ________________

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setting the View for Fragment:
        View root = inflater.inflate(R.layout.adverts_layout, container, false);

        // Instantiating Local Class Variables:
        searchField = (SearchView) root.findViewById(R.id.searchField);

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsListAdapter(adverts_Model_list);

        // Recycler View Config:
        mAdverts_List = (RecyclerView) root.findViewById(R.id.adverts_recycler_view);
        mAdverts_List.setHasFixedSize(true);
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2); // (Context context, int spanCount)

        // Set the Recycler View as a Linear Row-by-Row view:
        // mAdverts_List.setLayoutManager(new LinearLayoutManager(this));

        // Declare the assigned Layout:
        mAdverts_List.setLayoutManager(mLayoutManager);

        getAdvertsData();

        searchQuery();

        return root;
    }

    // Get & Listen to DB Changes:
    private void getAdvertsData() {

        /* (This) Class Method gets all of the documents from Firebase Firestore
            and then acts accordingly
         */

        // Accessing the data:
        db.collection("classified_ads")

            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.d(TAG, "Error : " + e.getMessage());
                }

                // On Document Change retrieves only the updated data:
                for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {

                    if (doc.getType() == DocumentChange.Type.ADDED) {

                        // [Test]
//                        Timestamp advert_title = doc.getDocument().getTimestamp("post_time");
//                        Log.d(TAG, "Advert : " + advert_title.getSeconds());

                        AdvertsModel advertsModel = doc.getDocument().toObject(AdvertsModel.class);
                        adverts_Model_list.add(advertsModel);

                        advertsListAdapter.notifyDataSetChanged();
                    }
                }
                }
            });
    }

    // Search Query Listen:
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

    // Filter Method:
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
