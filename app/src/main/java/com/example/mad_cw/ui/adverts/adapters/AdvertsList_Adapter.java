package com.example.mad_cw.ui.adverts.adapters;

import android.content.res.Resources;
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
   displayed in the AdvertsView_Fragment.java.
 */

public class AdvertsList_Adapter extends RecyclerView.Adapter<AdvertsViewHolder> {

    // ________________
    // class variables:

    // Holds list of AdvertsModel Data:
    public List<AdvertsModel> advertsModelList;

    // ________________
    // Class (AdvertsList_Adapter) [Public] Constructor [1 Args]:
    public AdvertsList_Adapter(List<AdvertsModel> advertsModelList) {

        this.advertsModelList = advertsModelList;
    }

    @NonNull
    @Override
    public AdvertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        // Verify Caller (View) Class:
        String class_name = parent.getContext().getClass().getSimpleName();
        // [TEST/DEV] Toast.makeText(parent.getContext(), parent.getContext().getClass().getSimpleName(), Toast.LENGTH_LONG).show();

        if (class_name.equals("ChatLobby_Activity")){
            // Load the chat lobby inflatable layout:
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_item_adapter_v2, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_item_adapter_v1, parent, false);
        }

        return new AdvertsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdvertsViewHolder holder, int position) {

        // Access the Resources from ViewHolder:
        Resources res = holder.itemView.getContext().getResources();

        // Get the Name of the Advert:
        holder.ad_title.setText(advertsModelList.get(position).getAd_title());
        holder.ad_price.setText(res.getString(R.string.set_ad_priceV2, advertsModelList.get(position).getAd_price()));

        // Populate Recycler View with an Image:
        Glide.with(holder.image_link.getContext())
                .load(advertsModelList.get(position).getImages().get(0))
                .into(holder.image_link);

        // Use 'position' to access the correct 'advert' object:
        AdvertsModel a = this.advertsModelList.get(position);

        // Bind the Advert to the holder:
        holder.bindAdverts(a);
    }

    @Override
    public int getItemCount() {
          return advertsModelList.size();
    }

    public void updateList(List<AdvertsModel> list){

        /* Class method to update Recycler View on Filter:
         */

        advertsModelList = list;
        notifyDataSetChanged();
    }

}
