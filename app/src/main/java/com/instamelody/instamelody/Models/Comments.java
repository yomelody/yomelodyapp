package com.instamelody.instamelody.Models;

/**
 * Created by Shubhansh Jaiswal on 06/04/17.
 */

public class Comments {

    String comment_id, user_id, userProfileImage, tvRealName, tvUsername, tvMsg, tvTime;

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getTvRealName() {
        return tvRealName;
    }

    public void setTvRealName(String tvRealName) {
        this.tvRealName = tvRealName;
    }

    public String getTvUsername() {
        return tvUsername;
    }

    public void setTvUsername(String tvUsername) {
        this.tvUsername = tvUsername;
    }

    public String getTvMsg() {
        return tvMsg;
    }

    public void setTvMsg(String tvMsg) {
        this.tvMsg = tvMsg;
    }

    public String getTvTime() {
        return tvTime;
    }

    public void setTvTime(String tvTime) {
        this.tvTime = tvTime;
    }
}
