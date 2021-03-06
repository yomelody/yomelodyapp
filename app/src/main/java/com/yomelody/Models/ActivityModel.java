package com.yomelody.Models;

import java.io.Serializable;

/**
 * Created by Saurabh Singh on 12/21/2016.
 */

public class ActivityModel implements Serializable{
    public String getMsgfirst_user() {
        return Msgfirst_user;
    }

    public void setMsgfirst_user(String msgfirst_user) {
        Msgfirst_user = msgfirst_user;
    }

    public String getMsgsecond_user() {
        return Msgsecond_user;
    }

    public void setMsgsecond_user(String msgsecond_user) {
        Msgsecond_user = msgsecond_user;
    }

    int id_;
    String tvmsg, tvtopic, tvtime, UserImgURL, createdByUserId,Msgfirst_user,Msgsecond_user;
    String recordingID,melodyID,adminmelodyid;

    public String getRecordingID() {
        return recordingID;
    }

    public String getMelodyID() {
        return melodyID;
    }

    public String getAdminmelodyid() {
        return adminmelodyid;
    }



    public ActivityModel(int id_, String tvmsg, String tvtopic, String tvtime,String ImageUrl,
                         String createdByUserId, String MsgFirst,String MsgSecond,
                         String recordingID, String melodyID,String adminmelodyid) {
        this.tvmsg = tvmsg;
        this.tvtopic = tvtopic;
        this.tvtime = tvtime;
        this.id_ = id_;
        this.UserImgURL = ImageUrl;
        this.createdByUserId = createdByUserId;
        this.Msgfirst_user=MsgFirst;
        this.Msgsecond_user=MsgSecond;

        this.recordingID=recordingID;
        this.melodyID=melodyID;
        this.adminmelodyid=adminmelodyid;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public String getUserImgURL() {
        return UserImgURL;
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
