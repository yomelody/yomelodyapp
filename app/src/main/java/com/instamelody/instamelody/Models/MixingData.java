package com.instamelody.instamelody.Models;

/**
 * Created by ADMIN on 8/3/2017.
 */


public class MixingData {

    String Id;
    String Volume;
    String Bass;
    String Treble;
    String Pan;
    String Pitch;
    String Reverb;
    String Compression;
    String Delay;
    String Tempo;
    String threshold;
    String ratio;
    String attack;
    String release;
    String makeup;
    String knee;
    String mix;
    String instaURL;

    public MixingData(String id, String volume, String bass, String treble, String pan,String pitch,String reverb,String compression,String delay,String tempo,String threshold,String ratio,String attack,String release,String makeup,String knee,String mix,String instURL ) {
        this.Id = id;
        this.Volume = volume;
        this.Bass = bass;
        this.Treble = treble;
        this.Pan = pan;
        this.Pitch = pitch;
        this.Reverb = reverb;
        this.Compression = compression;
        this.Delay = delay;
        this.Tempo = tempo;
        this.threshold = threshold;
        this.ratio = ratio;
        this.attack = attack;
        this.release = release;
        this.makeup = makeup;
        this.knee = knee;
        this.mix = mix;
        this.instaURL = instURL;

    }
}