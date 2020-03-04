package com.example.mad_cw.ui.adverts.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertsModel;

import java.util.List;

/* This class is responsible for handling the Recycler View for the AdvertsModel that are
   displayed in the AdvertsViewFragment.java.
 */

public class AdvertsListAdapter extends RecyclerView.Adapter<AdvertsViewHolder> {

    // Class Variables:

    // Holds list of AdvertsModel Data:
    public List<AdvertsModel> advertsModelList;

    // Class (AdvertsListAdapter) [Public] Constructor [1 Args]:
    public AdvertsListAdapter(List<AdvertsModel> advertsModelList) {

        this.advertsModelList = advertsModelList;
    }

    @NonNull
    @Override
    public AdvertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Identify the target view of the activity:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_item, parent, false);

        //
        return new AdvertsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertsViewHolder holder, int position) {

        // Get the Name of the Advert:
        holder.ad_title.setText(advertsModelList.get(position).getAd_title());
        holder.ad_price.setText(advertsModelList.get(position).getAd_price());

        // Populate Recycler View with an Image:
        Glide.with(holder.image_link.getContext())
                .load(advertsModelList.get(position).getImages().get(0))
                .into(holder.image_link);

        // Use 'position' to access the correct 'advert' object:
        AdvertsModel a = this.advertsModelList.get(position);

        // Bind the Advert to the holder:
        holder.bindAdverts(a);
    }

     // private final int limit = 2;

    @Override
    public int getItemCount() {

        /* Method Counts the number of items that are displayed in the
           list, to then use with the recyclerView.
         */

        // [Test]:
//        if(advertsModelList.size() > limit){
//            return limit;
//        }
//        else
//        {
//            return advertsModelList.size();
//        }

          return advertsModelList.size();
    }

    public void updateList(List<AdvertsModel> list){

        /* Class method to update Recycler View on Filter:
         */

        advertsModelList = list;
        notifyDataSetChanged();
    }

}
