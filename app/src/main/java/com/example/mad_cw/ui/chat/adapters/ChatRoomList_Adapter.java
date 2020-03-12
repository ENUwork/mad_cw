package com.example.mad_cw.ui.chat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_cw.R;
import com.example.mad_cw.ui.chat.ChatModel;

import java.util.ArrayList;

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

        // Change the chat message layout depending on which user sent it:
        if (chatList.get(position).getUser_uid().equals(currentUserUid)) {
            holder.msg_txt_in.setVisibility(View.GONE);
            holder.msg_txt_out.setText(chatList.get(position).getMsg_text());
        } else {
            holder.msg_txt_out.setVisibility(View.GONE);
            holder.msg_txt_in.setText(chatList.get(position).getMsg_text());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // View Holder
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView msg_txt_out, msg_txt_in;

        ViewHolder(View itemView) {
            super(itemView);

            msg_txt_out = itemView.findViewById(R.id.msg_txt_outgoing);
            msg_txt_in = itemView.findViewById(R.id.msg_txt_incoming);
        }
    }
}
