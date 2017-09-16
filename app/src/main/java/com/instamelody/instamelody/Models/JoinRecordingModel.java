package com.instamelody.instamelody.Models;

/**
 * Created by ADMIN on 9/14/2017.
 */

public class JoinRecordingModel {
    String recording_duration, recording_url;

    public JoinRecordingModel(String recording_duration, String recording_url) {
        this.recording_duration = recording_duration;
        this.recording_url = recording_url;
    }
}
