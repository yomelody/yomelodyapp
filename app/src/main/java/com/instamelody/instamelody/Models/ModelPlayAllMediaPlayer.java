package com.instamelody.instamelody.Models;

import android.media.MediaPlayer;

/**
 * Created by ADMIN on 9/23/2017.
 */

public class ModelPlayAllMediaPlayer {
    boolean Solo;
    boolean Mute;

    public boolean isSolo() {
        return Solo;
    }

    public void setSolo(boolean solo) {
        Solo = solo;
    }

    public boolean isMute() {
        return Mute;
    }

    public void setMute(boolean mute) {
        Mute = mute;
    }

    public boolean isRepete() {
        return Repete;
    }

    public void setRepete(boolean repete) {
        Repete = repete;
    }

    public android.media.MediaPlayer getMediaPlayer() {
        return MediaPlayer;
    }

    public void setMediaPlayer(android.media.MediaPlayer mediaPlayer) {
        MediaPlayer = mediaPlayer;
    }

    boolean Repete;
    MediaPlayer MediaPlayer;

    public ModelPlayAllMediaPlayer(boolean solo, boolean mute, boolean repete, android.media.MediaPlayer mediaPlayer) {
        Solo = solo;
        Mute = mute;
        Repete = repete;
        MediaPlayer = mediaPlayer;
    }
}
