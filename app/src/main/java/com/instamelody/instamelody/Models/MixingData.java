package com.instamelody.instamelody.Models;

/**
 * Created by ADMIN on 8/3/2017.
 */


public class MixingData {
    public String positionId;
    public String id;
    public String volume;
    public String bass;
    public String treble;
    public String pan;
    public String pitch;
    public String reverb;
    public String compression;
    public String delay;
    public String tempo;
    public String threshold;
    public String ratio;
    public String attack;
    public String release;
    public String makeup;
    public String knee;
    public String mix;
    public String fileurl;

    public MixingData(String id, String volume, String bass, String treble, String pan, String pitch, String reverb, String compression, String delay, String tempo, String threshold, String ratio, String attack, String release, String makeup, String knee, String mix, String instURL, String PositionId) {
        this.id = id;
        this.volume = volume;
        this.bass = bass;
        this.treble = treble;
        this.pan = pan;
        this.pitch = pitch;
        this.reverb = reverb;
        this.compression = compression;
        this.delay = delay;
        this.tempo = tempo;
        this.threshold = threshold;
        this.ratio = ratio;
        this.attack = attack;
        this.release = release;
        this.makeup = makeup;
        this.knee = knee;
        this.mix = mix;
        this.fileurl = instURL;
        this.positionId = PositionId;

    }
}