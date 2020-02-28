package com.example.mad_cw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdvertsListAdapter extends RecyclerView.Adapter<AdvertsListAdapter.ViewHolder> {

    //
    public List<Adverts> advertsList;

    // Class (AdvertsListAdapter) [Public] Constructor [1 Args]:
    public AdvertsListAdapter(List<Adverts> advertsList) {

        //
        this.advertsList = advertsList;
    }

    @NonNull
    @Override
    public AdvertsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Identify the target view of the activity:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_item, parent, false);

        //
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertsListAdapter.ViewHolder holder, int position) {

        // Get the Name of the Advert:
        holder.ad_title.setText(advertsList.get(position).getAd_title());

        // Populate Recycler View with an Image:
        Glide.with(holder.image_link.getContext())
                .load(advertsList.get(position).getImage_link())
                .into(holder.image_link);
    }

    @Override
    public int getItemCount() {

        /* Method Counts the number of items that are displayed in the
           list, to then use with the recyclerView.
         */

        return advertsList.size();
    }

    // Class (ViewHolder) Constructor:
    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;

        public TextView ad_title;
        public ImageView image_link;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assigning Target View:
            view = itemView;

            // Views:
            ad_title = (TextView) view.findViewById(R.id.advert_title_item);
            image_link = (ImageView) view.findViewById(R.id.advert_image_item);
        }
    }

}
