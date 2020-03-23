package com.pedalT.app.ui.chat;

import com.google.firebase.Timestamp;

public class ChatModel {

    // ____________
    // chat class variables:

    private String msg_text, user_uid;
    private Timestamp timestamp;

    // ____________
    // constructors:

    // [0 Args]
    public ChatModel(){}

    // [2 Args]
    public ChatModel(String msg_text, String user_uid, Timestamp timestamp) {
        this.msg_text = msg_text;
        this.user_uid = user_uid;
        this.timestamp = timestamp;
    }

    // ________
    // class getters & setters:

    public String getMsg_text(){ return this.msg_text; }
    public String getUser_uid() { return this.user_uid; }
    public Timestamp getTimestamp() { return this.timestamp; }

    public void setMsg_text(String msg_text) { this.msg_text = msg_text; }
    public void setUser_uid(String user_uid) { this.user_uid = user_uid; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

}
