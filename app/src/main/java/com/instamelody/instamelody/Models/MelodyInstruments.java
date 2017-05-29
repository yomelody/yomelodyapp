package com.instamelody.instamelody.Models;

/**
 * Created by Shubhansh Jaiswal on 09/02/17.
 */

public class MelodyInstruments {

    //Used for storing instruments details

    int instrumentId;
    int melodyPacksId;
    String userName, userProfilePic, instrumentCover, instrumentType, instrumentFileSize, instrumentBpm;
    String instrumentCreated, instrumentLength;
    String instrumentFile, instrumentName;
    String audioType;
    boolean isFooter;

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public int getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getInstrumentFileSize() {
        return instrumentFileSize;
    }

    public void setInstrumentFileSize(String instrumentFileSize) {
        this.instrumentFileSize = instrumentFileSize;
    }

    public String getInstrumentBpm() {
        instrumentBpm = "BPM: " + instrumentBpm;
        return instrumentBpm;
    }

    public void setInstrumentBpm(String instrumentBpm) {
        this.instrumentBpm = instrumentBpm;
    }

    public int getMelodyPacksId() {
        return melodyPacksId;
    }

    public void setMelodyPacksId(int melodyPacksId) {
        this.melodyPacksId = melodyPacksId;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getInstrumentCover() {
//        instrumentCover = "https://www.sleekcover.com/covers/batman-smile-facebook-cover.jpg";
        return instrumentCover;
    }

    public void setInstrumentCover(String instrumentCover) {
        this.instrumentCover = instrumentCover;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getInstrumentFile() {
        return instrumentFile;
    }

    public void setInstrumentFile(String instrumentFile) {
        this.instrumentFile = instrumentFile;
    }

    public String getInstrumentCreated() {
        return instrumentCreated;
    }

    public void setInstrumentCreated(String instrumentCreated) {
        this.instrumentCreated = instrumentCreated;
    }

    public String getInstrumentLength() {
        return instrumentLength;
    }

    public void setInstrumentLength(String instrumentLength) {
        this.instrumentLength = instrumentLength;
    }
}
