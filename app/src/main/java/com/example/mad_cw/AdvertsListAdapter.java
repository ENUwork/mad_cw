package com.example.mad_cw;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

/* This class is responsible for handling the Recycler View for the Adverts that are
   displayed in the View_Ads.java.
 */

public class AdvertsListAdapter extends RecyclerView.Adapter<AdvertsListAdapter.ViewHolder> {

    // Class Variables:

    // Holds list of Adverts Data:
    public List<Adverts> advertsList;

    // Class (AdvertsListAdapter) [Public] Constructor [1 Args]:
    public AdvertsListAdapter(List<Adverts> advertsList) {
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
        holder.ad_price.setText(advertsList.get(position).getAd_price());

        // Populate Recycler View with an Image:
        Glide.with(holder.image_link.getContext())
                .load(advertsList.get(position).getImage_link())
                .into(holder.image_link);

        // Use 'position' to access the correct 'advert' object:
        Adverts a = this.advertsList.get(position);

        // Bind the Advert to the holder:
        holder.bindAdverts(a);
    }

    @Override
    public int getItemCount() {

        /* Method Counts the number of items that are displayed in the
           list, to then use with the recyclerView.
         */

        return advertsList.size();
    }


    public void updateList(List<Adverts> list){

        /* Class method to update Recycler View on Filter:
         */

        advertsList = list;
        notifyDataSetChanged();
    }

    // Class (ViewHolder) Constructor:
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class Variables:
        View view;
        private TextView ad_title, ad_price;
        private ImageView image_link;
        private Adverts adverts;

        //
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Assigning Target View:
            view = itemView;

            // Views:
            ad_title = (TextView) view.findViewById(R.id.advert_title_item);
            ad_price = (TextView) view.findViewById(R.id.advert_item_price);
            image_link = (ImageView) view.findViewById(R.id.advert_image_item);

            // Set onCLick Triggers:
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(this.adverts != null ){

                // Create new Intent to pass along data:
                Intent sel_advert = new Intent(itemView.getContext(), Advert_Details.class);

                // Pass advert data along:
                sel_advert.putExtra("advert", this.adverts);

                // Initiate the data passing to the Advert_details class:
                itemView.getContext().startActivity(sel_advert);
            }
        }

        // Class Constructor:
        public void bindAdverts(Adverts adverts) {

            this.adverts = adverts;
        }

    }

}
