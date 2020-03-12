package com.example.mad_cw.ui.chat.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.chat.ChatModel;

import java.util.ArrayList;
import java.util.Date;

public class ChatRoomList_Adapter extends RecyclerView.Adapter<ChatRoomList_Adapter.ViewHolder> {

    // ____________
    // class variables:
    private String currentUserUid;
    private ArrayList<ChatModel> chatList;

    // _____________
    // constructor:

    public ChatRoomList_Adapter(ArrayList<ChatModel> chatList, String currentUserUid) {
        this.currentUserUid = currentUserUid;
        this.chatList = chatList;
    }

    // _____________________
    // RecyclerView Interface Methods:

    @NonNull
    @Override
    public ChatRoomList_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Identify the target view of the activity:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_bubble_adapter, parent, false);
        return new ChatRoomList_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomList_Adapter.ViewHolder holder, int position) {

        Date prev_date = null;
        String prev_day = null, prev_month = null;

        // Dealing with dates:
        if (position != 0){
            prev_date = chatList.get(position - 1).getTimestamp().toDate();
            prev_day = (String) DateFormat.format("dd", prev_date);
            prev_month = (String) DateFormat.format("MM", prev_date);
        }

        // Current Date/Times:
        Date current_date = chatList.get(position).getTimestamp().toDate();
        String day = (String) DateFormat.format("dd", current_date);
        String month = (String) DateFormat.format("MM", current_date);
        String hour = (String) DateFormat.format("HH", current_date);
        String min = (String) DateFormat.format("mm", current_date);

        // Showing chat day, month as main, if the date changed;
        if (prev_date != null && !day.equals(prev_day) || !month.equals(prev_month)){
            holder.ad_post_time.setText(day + "." + month);
            holder.ad_post_time.setVisibility(View.VISIBLE);
        } else {
            holder.ad_post_time.setVisibility(View.GONE);
        }

        // Change the chat message layout depending on which user sent it:
        if (chatList.get(position).getUser_uid().equals(currentUserUid)) {
            holder.msg_in_layout.setVisibility(View.GONE);
            holder.msg_out_layout.setVisibility(View.VISIBLE);
            holder.msg_txt_out.setText(chatList.get(position).getMsg_text());
            holder.ad_post_out_time.setText(hour + ":" + min);
        } else {
            holder.msg_out_layout.setVisibility(View.GONE);
            holder.msg_in_layout.setVisibility(View.VISIBLE);
            holder.msg_txt_in.setText(chatList.get(position).getMsg_text());
            holder.ad_post_in_time.setText(hour + ":" + min);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // View Holder
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout msg_out_layout, msg_in_layout;
        TextView msg_txt_out, msg_txt_in,
                ad_post_time, ad_post_out_time, ad_post_in_time;

        ViewHolder(View itemView) {
            super(itemView);

            msg_out_layout = itemView.findViewById(R.id.msg_outgoing_layout);
            msg_in_layout = itemView.findViewById(R.id.msg_incoming_layout);

            msg_txt_out = itemView.findViewById(R.id.msg_txt_outgoing);
            msg_txt_in = itemView.findViewById(R.id.msg_txt_incoming);
            ad_post_time = itemView.findViewById(R.id.ad_chat_time);
            ad_post_out_time = itemView.findViewById(R.id.ad_chat_out_time);
            ad_post_in_time = itemView.findViewById(R.id.ad_chat_in_time);
        }
    }
}
