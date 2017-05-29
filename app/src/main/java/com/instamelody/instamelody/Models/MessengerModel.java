package com.instamelody.instamelody.Models;

/**
 * Created by Shubhansh Jaiswal on 1/4/2017.
 */

public class MessengerModel {

    int id_, userProfileImage;
    String tvMsg, tvTime, tvUserName;

    public MessengerModel(int id_, String tvUserName, String tvMsg, String tvTime, int userProfileImage) {

        this.id_ = id_;
        this.tvUserName = tvUserName;
        this.tvMsg = tvMsg;
        this.tvTime = tvTime;
        this.userProfileImage = userProfileImage;
    }

    public String getTvMsg() {
        return tvMsg;
    }

    public String getTvTime() {
        return tvTime;
    }

    public int getUserProfileImage() {
        return userProfileImage;
    }

    public int getId_() {
        return id_;
    }

    public String getTvUserName() {
        return tvUserName;
    }
}