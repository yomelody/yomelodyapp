package com.instamelody.instamelody.Models;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityModel {
    int id_,userProfileImage;
    String tvmsg,tvtopic,tvtime;

    public ActivityModel(int id_,int userProfileImage,String tvmsg,String tvtopic,String tvtime){
        this.tvmsg = tvmsg;
        this.tvtopic = tvtopic;
        this.tvtime = tvtime;
        this.id_=id_;
        this.userProfileImage = userProfileImage;
    }
    public int getUserProfileImage(){
        return userProfileImage;
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
