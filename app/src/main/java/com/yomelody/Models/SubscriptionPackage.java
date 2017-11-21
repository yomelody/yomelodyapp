package com.yomelody.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by CBPC 41 on 9/15/2017.
 */

public class SubscriptionPackage implements Parcelable {
    String package_id, package_name, total_melody, recording_time, cost,recordingtime;

    public SubscriptionPackage() {

    }

    public String getRecordingtime() {
        return recordingtime;
    }

    public void setRecordingtime(String recordingtime) {
        this.recordingtime = recordingtime;
    }

    public String getPackage_id() {
        return package_id;
    }

    public void setPackage_id(String package_id) {
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getTotal_melody() {
        return total_melody;
    }

    public void setTotal_melody(String description) {
        this.total_melody = description;
    }

    public String getRecording_time() {
        return recording_time;
    }

    public void setRecording_time(String status) {
        this.recording_time = status;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }


    public SubscriptionPackage(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.package_id = data[0];
        this.package_name = data[1];
        this.total_melody = data[2];
        this.recording_time = data[3];
        this.cost = data[4];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.package_id,
                this.package_name,
                this.total_melody,
                recording_time,
                cost});
    }

    public static final Creator CREATOR = new Creator() {
        public SubscriptionPackage createFromParcel(Parcel in) {
            return new SubscriptionPackage(in);
        }

        public SubscriptionPackage[] newArray(int size) {
            return new SubscriptionPackage[size];
        }
    };
}




