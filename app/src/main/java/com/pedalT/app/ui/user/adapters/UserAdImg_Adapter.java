package com.pedalT.app.ui.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pedalT.app.R;

import java.util.List;

public class UserAdImg_Adapter extends RecyclerView.Adapter<UserAdImg_Adapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public UserAdImg_Adapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.advert_image_viewpager, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String image = mData.get(position);

        // Populate Recycler View with an Image:
        Glide.with(holder.image_link.getContext())
                .load(image)
                .into(holder.image_link);

        // Apply different layout for the main image:
        if (position == 0){
            holder.image_link.setBackgroundResource(R.drawable.image_border);
        } else {
            holder.image_link.setBackgroundResource(0);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image_link;

        ViewHolder(View itemView) {
            super(itemView);

            image_link = itemView.findViewById(R.id.imageVal);
            itemView.setOnClickListener(this);
        }

        public void removeItem(int position){
            mData.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
                removeItem(getAdapterPosition());
                notifyDataSetChanged();
            }
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id);
    }

    public List<String> getmData() {
        return mData;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}