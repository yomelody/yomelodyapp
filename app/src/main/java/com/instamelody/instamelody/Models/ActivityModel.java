package com.instamelody.instamelody.Models;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityModel {
    int id_;
    String tvmsg, tvtopic, tvtime, UserImgURL;

    public ActivityModel(int id_, String tvmsg, String tvtopic, String tvtime, String ImageUrl) {
        this.tvmsg = tvmsg;
        this.tvtopic = tvtopic;
        this.tvtime = tvtime;
        this.id_ = id_;
        this.UserImgURL = ImageUrl;
    }

    public String getUserImgURL() {
        return UserImgURL;
    }

    public void setUserImgURL(String userImgURL) {
        UserImgURL = userImgURL;
    }

    public int getId_() {
        return id_;
    }

    public String getTvmsg() {
        return tvmsg;
    }

    public String gettvtopic() {
        return tvtopic;
    }

    public String getTvtime() {
        return tvtime;
    }
}
