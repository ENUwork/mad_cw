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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.adapters.AdvertsList_Adapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Implementing Fragments:

public class AdvertsView_Fragment extends Fragment implements View.OnClickListener {

    // Fragment (Class) Variables:

    private static final String TAG = "AdvertsView";

    private RecyclerView mAdverts_List;
    private AdvertsList_Adapter advertsListAdapter;
    private List<AdvertsModel> adverts_Model_list;

    private CheckBox w26, w275, w29,
            fXS, fS, fM, fL, fXL, fXXL,
            m, ln, vw, b, rp;

    private TextView advertQueryNum;
    private EditText priceMin, priceMax;
    private Button refineSearchApplyBtn, refineSearchBtn;

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
        refineSearchBtn = root.findViewById(R.id.refine_search_btn);
        refineSearchApplyBtn = root.findViewById(R.id.refine_apply_btn);

        // Set Listeners:
        refineSearchBtn.setOnClickListener(this);
        refineSearchApplyBtn.setOnClickListener(this);

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

        // Identify Input Fields:
        priceMin = root.findViewById(R.id.filter_price_min);
        priceMax = root.findViewById(R.id.filter_price_max);

        // Other Layout Identifiers:
        advertQueryNum = root.findViewById(R.id.adverts_query_results_txt);

        // Animations:
        slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);
        slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);

        // Dealing with Instantiating the Recycler View & the View Holder:
        adverts_Model_list = new ArrayList<>();
        advertsListAdapter = new AdvertsList_Adapter(adverts_Model_list);

        // Recycler View Config:
        mAdverts_List = root.findViewById(R.id.adverts_recycler_view);
        mAdverts_List.setHasFixedSize(true);
        mAdverts_List.setAdapter(advertsListAdapter);

        // Set the Recycler View as a Gallery View:
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2); // (Context context, int spanCount)

        // Declare the assigned Layout:
        mAdverts_List.setLayoutManager(mLayoutManager);

        getAdvertsData();

        searchQuery();

        return root;
    }



    // ________________
    // handling adverts methods:

    private void getAdvertsData() {

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
                        AdvertsModel advertsModel = doc.getDocument().toObject(AdvertsModel.class);
                        adverts_Model_list.add(advertsModel);
                        advertsListAdapter.notifyDataSetChanged();
                        advertQueryNum.setText(getString(R.string.display_ads_qty, advertsListAdapter.getItemCount()));
                    }
                }
                }
            });
    }

    private void searchQuery() {

        searchField.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterByTitle(newText);
                return false;
            }
        });
    }

    private void filterByTitle(String text){

        List<AdvertsModel> temp = new ArrayList();

        for(AdvertsModel d: adverts_Model_list){
            if(d.getAd_title().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        // Update Recycler View
        advertsListAdapter.updateList(temp);
    }

    private void refineSearchSelect() {

        // List of all the selected queries:
        List<String> filter_list = new ArrayList<>();

        // Checkboxes:
        String item_val;
        List<CheckBox> items = new ArrayList<>(Arrays.asList(w26, w275, w29, fXS, fS, fM, fL, fXL, fXXL, m, ln, vw, b, rp));
        for (CheckBox item : items){
            if(item.isChecked()) {
                item_val = item.getText().toString();
                filter_list.add(item_val);
            }
        }

        // Price Range:
        String temp_min = priceMin.getText().toString();
        String temp_max = priceMax.getText().toString();
        int pMin, pMax;

        // Price Min,
        if (!temp_min.isEmpty()){
            pMin = Integer.valueOf(temp_min);
        } else {
            pMin = 0;
        }

        // Price Max,
        if (!temp_max.isEmpty()){
            pMax = Integer.valueOf(temp_max);
        }
        else {
            pMax = 10000000;
        }

        refineSearchFilter(filter_list, pMin, pMax);
    }

    private void refineSearchFilter(List<String> filter_list, int pMin, int pMax) {

        // Use Set to Remove Duplicates:
        Set<AdvertsModel> set_temp = new LinkedHashSet<>();

        for (AdvertsModel d : adverts_Model_list) {
            int price = Integer.valueOf(d.getAd_price());

            // Checkbox Query Search,
            if (filter_list.size() != 0) {

                // Compare arrays
                ArrayList<String> filter_values = new ArrayList<>(filter_list);
                ArrayList<String> advert_values = new ArrayList<>(d.getAd_other());

                filter_values.retainAll(advert_values);

                System.out.println(filter_values);
                if (filter_values.size() > 0 && price >= pMin && price <= pMax) {
                    set_temp.add(d);
                }
            } else {
                // Price Range Search:
                if (price >= pMin && price <= pMax) {
                    set_temp.add(d);
                }
            }
        }

        // Copy over non duplicate data to results array:
        List<AdvertsModel> result = new ArrayList<>(set_temp);

        // Update Recycler View
        advertsListAdapter.updateList(result);

        // Update Display Counter:
        advertQueryNum.setText(getString(R.string.display_ads_qty, advertsListAdapter.getItemCount()));
    }

    // _____________
    // user click handling:

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.refine_search_btn:
                mainLayout.setVisibility(View.GONE);
                refineSearchBtn.setVisibility(View.GONE);
                refineLayout.setVisibility(View.VISIBLE);
                // refineLayout.startAnimation(slideUp);
                break;

            case R.id.refine_apply_btn:
                refineLayout.setVisibility(View.GONE);
                // refineLayout.startAnimation(slideDown);
                refineSearchBtn.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.VISIBLE);
                refineSearchSelect();
                break;
        }

    }
}
