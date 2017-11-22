package com.instamelody.instamelody.Models;

import java.io.Serializable;

/**
 * Created by Macmini on 10/11/17.
 */

public class Joined_model implements Serializable {
    String rec_duration, recording_url;

    public String getRec_duration() {
        return rec_duration;
    }

    public void setRec_duration(String rec_duration) {
        this.rec_duration = rec_duration;
    }

    public String getRecording_url() {
        return recording_url;
    }

    public void setRecording_url(String recording_url) {
        this.recording_url = recording_url;
    }


}
