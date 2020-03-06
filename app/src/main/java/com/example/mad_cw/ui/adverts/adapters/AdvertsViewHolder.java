package com.example.mad_cw.ui.adverts.adapters;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertInfoActivity;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.user.User_Advert_Create_Edit_Activity;

public class AdvertsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Class Variables:
    View view;
    TextView ad_title;
    TextView ad_price;
    ImageView image_link;
    private AdvertsModel advertsModel;

    //
    public AdvertsViewHolder(@NonNull View itemView) {
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

        Toast.makeText(view.getContext(), view.getContext().getClass().getSimpleName(), Toast.LENGTH_LONG).show();

        // Verify Caller (View) Class:
        String class_name = view.getContext().getClass().getSimpleName();

        // Identify if the caller class originated from the "User_Personal_Ads,
        // if so, redirect to "Advert Edit Activity"

        if(this.advertsModel != null){

            if (!class_name.equals("User_Adverts_Personal_Activity")) {

                // Create new Intent to pass along data:
                Intent sel_advert = new Intent(itemView.getContext(), AdvertInfoActivity.class);

                // Pass advert data along:
                sel_advert.putExtra("advert", this.advertsModel);

                // Initiate the data passing to the Advert_details class:
                itemView.getContext().startActivity(sel_advert);
            }

            // Redirect User to "Edit page":
            else {

                // Create new Intent to pass along data:
                Intent sel_advert = new Intent(itemView.getContext(), User_Advert_Create_Edit_Activity.class);

                // Pass advert data along:
                sel_advert.putExtra("advert", this.advertsModel);

                // Initiate the data passing to the Advert_details class:
                itemView.getContext().startActivity(sel_advert);
            }
        }
    }

    // Class Constructor:
    public void bindAdverts(AdvertsModel advertsModel) {
        this.advertsModel = advertsModel;
    }

}
