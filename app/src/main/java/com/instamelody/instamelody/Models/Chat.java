package com.instamelody.instamelody.Models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class Chat {

    String id;
    String senderID;
    String senderName;
    String receiverID;
    String receiverName;
    String groupName;
    String coverPick;
    String userProfileImage;
    String message;
    String chatID;
    String isRead;
    String sendAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCoverPick() {
        return coverPick;
    }

    public void setCoverPick(String coverPick) {
        this.coverPick = coverPick;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public void setUserProfileImage(String userProfileImage) {
        this.userProfileImage = userProfileImage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getSendAt() {
        return sendAt;
    }

    public void setSendAt(String sendAt) {
        sendAt = DateTime(sendAt);
        this.sendAt = sendAt;
    }

    public String DateTime(String send_at) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String val = "";

        try {
            Date oldDate = dateFormat.parse(send_at);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();

            long diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days == 0) {
                if (hours == 0) {
                    if (minutes == 0) {
                        if (seconds <= 5) {
                            val = "Just now";
                        } else {
                            val = String.valueOf(seconds) + " " + "secs";
                        }
                    } else if (minutes == 1) {
                        val = String.valueOf(minutes) + " " + "min";
                    } else {
                        val = String.valueOf(minutes) + " " + "mins";
                    }
                } else if (hours == 1) {
                    val = String.valueOf(hours) + " " + "hour";
                } else {
                    val = String.valueOf(hours) + " " + "hrs";
                }
            } else if (days == 1) {
                val = "1 day";
            } else {
                val = send_at.substring(2, send_at.indexOf(" ")).replace("-", "/");

                String year = val.substring(0, val.indexOf("/"));
                String month = val.substring(val.indexOf("/") + 1, val.lastIndexOf("/"));
                String date = val.substring(val.lastIndexOf("/") + 1, val.length());
                val = date + "/" + month + "/" + year;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return val;
    }
}