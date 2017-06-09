package com.instamelody.instamelody.Models;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 */

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Message implements Serializable {

    String id, message, createdAt, senderId;
    String userProfileImage;

    public Message() {
    }

    public Message(String id, String message, String createdAt, String userProfileImage) {
        this.id = id;
        this.message = message;
        this.createdAt = createdAt;
        this.userProfileImage = userProfileImage;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(String createdAt) {
        createdAt = DateTime(createdAt);
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String DateTime(String created_At) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String val = "";

        try {
            Date date = dateFormat.parse(created_At);
            String oDate = String.valueOf(date);
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();

            long diff = currentDate.getTime() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days < 7) {
                //Sample time : Mon May 15 21:07:55 GMT+05:30 2017
                String s = oDate.substring(oDate.indexOf(" ") + 1, oDate.indexOf("GMT")).trim();
                String month = s.substring(s.indexOf(""), s.indexOf(" "));
                String dates = s.substring(4, 6);
                String time = s.substring(s.lastIndexOf(" ") + 1, s.length());
                String s2 = time.substring(time.indexOf(""), time.indexOf(":")).trim();
                String s3 = time.substring(time.indexOf(":"), time.lastIndexOf(":"));
                int a = Integer.parseInt(s2);
                if (a > 12) {
                    int b = a - 12;
                    a = b;
                    if (a < 10) {
                        s2 = "0" + String.valueOf(a);
                    } else {
                        s2 = String.valueOf(a);
                    }
                    time = s2 + s3 + " PM";
                } else if (a == 0) {
                    s2 = "12";
                    time = s2 + s3 + " AM";
                }
                else {
                    time = s2 + s3 + " AM";
                }
                val = month + " " + dates + " " + time;

            } else {
                String day = oDate.substring(0, oDate.indexOf(" "));
                String s = oDate.substring(oDate.indexOf(" "), oDate.indexOf("GMT")).trim();
                String s1 = s.substring(s.lastIndexOf(" "), s.length());
                String s2 = s1.substring(s1.indexOf(""), s1.indexOf(":")).trim();
                String s3 = s1.substring(s1.indexOf(":"), s1.lastIndexOf(":"));
                int a = Integer.parseInt(s2);
                if (a > 12) {
                    int b = a - 12;
                    a = b;
                    if (a < 10) {
                        s2 = "0" + String.valueOf(a);
                    } else {
                        s2 = String.valueOf(a);
                    }
                    s1 = s2 + s3 + " PM";
                } else if (a == 0) {
                    s2 = "12";
                    s1 = s2 + s3 + " AM";
                }
                else {
                    s1 = s2 + s3 + " AM";
                }
                val = day + " " + s1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return val;
    }

    public String userProfileImage() {
        return userProfileImage;
    }

    public void userProfileImage(String userProfilePic) {
        this.userProfileImage = userProfilePic;
    }
}