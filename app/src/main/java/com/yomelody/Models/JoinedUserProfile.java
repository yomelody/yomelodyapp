package com.yomelody.Models;

/**
 * Created by Macmini on 14/09/17.
 */

public class JoinedUserProfile {

    String userId,status;

    public JoinedUserProfile(String userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
