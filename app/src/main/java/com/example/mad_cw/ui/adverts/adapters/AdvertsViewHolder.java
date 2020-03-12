package com.example.mad_cw.ui.adverts.adapters;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.adverts.AdvertDetails_Activity;
import com.example.mad_cw.ui.adverts.AdvertsModel;
import com.example.mad_cw.ui.chat.ChatRoom_Activity;
import com.example.mad_cw.ui.user.User_Advert_Create_Edit_Activity;

public class AdvertsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Class Variables:
    View view;
    TextView ad_title, ad_price;
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

        // !!! [TEST/DEV] Toast.makeText(view.getContext(), view.getContext().getClass().getSimpleName(), Toast.LENGTH_LONG).show();

        // Identify Caller Class, and act accordingly:
        String class_name = view.getContext().getClass().getSimpleName();

        if(this.advertsModel != null){

            if (class_name.equals("ChatLobby_Activity")) {

                // Redirect to "Chat Room":
                Intent open_chat = new Intent(itemView.getContext(), ChatRoom_Activity.class);
                open_chat.putExtra("advert_info", this.advertsModel);
                itemView.getContext().startActivity(open_chat);
            }
            else if (class_name.equals("User_Adverts_Personal_Activity")) {

                // Redirect User to "Edit page":
                // Create new Intent to pass along data:
                Intent sel_advert = new Intent(itemView.getContext(), User_Advert_Create_Edit_Activity.class);
                // Pass advert data along:
                sel_advert.putExtra("advert", this.advertsModel);
                // Initiate the data passing to the Advert_details class:
                itemView.getContext().startActivity(sel_advert);
            }
            else {

                // Create new Intent to pass along data:
                Intent sel_advert = new Intent(itemView.getContext(), AdvertDetails_Activity.class);
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
