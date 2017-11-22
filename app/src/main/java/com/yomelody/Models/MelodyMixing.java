package com.yomelody.Models;

import java.util.ArrayList;

/**
 * Created by ADMIN on 8/2/2017.
 */

public class MelodyMixing {
    int admin_intruments_ids;
    String isMelody;
    String topic_name;
    int user_id;
    int public_flag;
    String recordWith;
    int genere;
    int bpm;
    Float duration;


    public int getAdmin_intruments_ids() {
        return admin_intruments_ids;
    }

    public void setAdmin_intruments_ids(int admin_intruments_ids) {
        this.admin_intruments_ids = admin_intruments_ids;
    }

    public String getIsMelody() {
        return isMelody;
    }

    public void setIsMelody(String isMelody) {
        this.isMelody = isMelody;
    }

    public String getTopic_name() {
        return topic_name;
    }

    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPublic_flag() {
        return public_flag;
    }

    public void setPublic_flag(int public_flag) {
        this.public_flag = public_flag;
    }

    public String getRecordWith() {
        return recordWith;
    }

    public void setRecordWith(String recordWith) {
        this.recordWith = recordWith;
    }

    public int getGenere() {
        return genere;
    }

    public void setGenere(int genere) {
        this.genere = genere;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }



    ArrayList<MixingData> vocalsound;

    public ArrayList<MixingData> getVocalsound() {
        return vocalsound;
    }

    public void setVocalsound(ArrayList<MixingData> vocalsound) {
        this.vocalsound = vocalsound;
    }
}
