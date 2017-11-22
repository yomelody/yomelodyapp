package com.yomelody.Models;

/**
 * Created by Shubhansh Jaiswal on 22/08/17.
 */

public class AudioDetails {

    String recordingId, addedBy, recordingTopic, name, userName;

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getRecordingTopic() {
        return recordingTopic;
    }

    public void setRecordingTopic(String recordingTopic) {
        this.recordingTopic = recordingTopic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return "@"+userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
