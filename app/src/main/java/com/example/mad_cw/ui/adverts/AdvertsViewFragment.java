package com.example.mad_cw.ui.adverts;


// Class that is responsible for displaying classified listings/ads
// publicly to all users.

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import java.util.Arrays;
import java.util.List;

// Implementing Fragments:

public class AdvertsViewFragment extends Fragment implements View.OnClickListener {

    // Fragment (Class) Variables:

    private static final String TAG = "AdvertsView";

    private RecyclerView mAdverts_List;
    private AdvertsListAdapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private CheckBox w26, w275, w29,
            fXS, fS, fM, fL, fXL, fXXL,
            m, ln, vw, b, rp;

    private SearchView searchField;
    private LinearLayout mainLayout, refineLayout;

    private Animation slideDown, slideUp;

    // Access a Cloud Firestore instance from the Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // ________________
    // activity cycle methods:

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Setting the View for Fragment:
        View root = inflater.inflate(R.layout.adverts_layout, container, false);

        // Set Up Variables
        searchField = root.findViewById(R.id.searchField);
        refineLayout = root.findViewById(R.id.refine_search_layout);
        mainLayout = root.findViewById(R.id.adverts_view_main_layout);

        // Set Listeners:
        root.findViewById(R.id.refine_search_btn).setOnClickListener(this);
        root.findViewById(R.id.refine_apply_btn).setOnClickListener(this);

        // Identify CheckBoxes:
        w26 = root.findViewById(R.id.filter_wheels_size_26);
        w275 = root.findViewById(R.id.filter_wheels_size_275);
        w29 = root.findViewById(R.id.filter_wheels_size_29);
        fXS = root.findViewById(R.id.filter_frame_size_XS);
        fS = root.findViewById(R.id.filter_frame_size_S);
        fM = root.findViewById(R.id.filter_frame_size_M);
        fL = root.findViewById(R.id.filter_frame_size_L);
        fXL = root.findViewById(R.id.filter_frame_size_XL);
        fXXL = root.findViewById(R.id.filter_frame_size_XXL);
        m = root.findViewById(R.id.filter_condition_MINT);
        ln = root.findViewById(R.id.filter_condition_LN);
        vw = root.findViewById(R.id.filter_condition_VW);
        b = root.findViewById(R.id.filter_condition_B);
        rp = root.findViewById(R.id.filter_condition_RP);

        // Animations:
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsListAdapter(adverts_Model_list);

        // Recycler View Config:
        mAdverts_List = root.findViewById(R.id.adverts_recycler_view);
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

    // ________________
    // handling adverts methods:

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

    private void refineSearchSelect() {

        List<String> filter_list = new ArrayList<String>();

        String item_val;

        List<CheckBox> items = new ArrayList<CheckBox>(Arrays.asList(w26, w275, w29, fXS, fS, fM, fL, fXL, fXXL, m, ln, vw, b, rp));
        for (CheckBox item : items){
            if(item.isChecked()) {

                item_val = item.getText().toString();

                filter_list.add(item_val);
            }
        }

        refineSearchFilter(filter_list);
    }

    private void refineSearchFilter(List<String> filter) {

        // Get the Object Other Data Info:

        List<AdvertsModel> temp = new ArrayList();

        for(AdvertsModel d: adverts_Model_list){

            // Compare arrays
            ArrayList<String> filter_values = new ArrayList<>(filter);
            ArrayList<String> advert_values = new ArrayList<>(d.getAd_other());

            filter_values.retainAll(advert_values);

            System.out.println(filter_values);
            if(filter_values.size() > 0){
                temp.add(d);
            }
        }

        // Update Recycler View
        advertsListAdapter.updateList(temp);
    }

    // _____________
    // user click handling:

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.refine_search_btn:
                mainLayout.setVisibility(View.GONE);
                refineLayout.setVisibility(View.VISIBLE);
                refineLayout.startAnimation(slideUp);
                break;

            case R.id.refine_apply_btn:
                refineLayout.setVisibility(View.GONE);
                refineLayout.startAnimation(slideDown);
                mainLayout.setVisibility(View.VISIBLE);
                refineSearchSelect();
                break;
        }

    }
}
