package com.instamelody.instamelody.Models;

/**
 * Created by Shubahansh Jaiswal on 11/29/2016.
 */

public class DataModel {

    int id_, userProfileImage, ivMelodyCover ,tvViewCount, tvLikeCount, tvCommentCount, tvShareCount;
    String tvUserName, tvInstrumentsUsed, tvMelodyGenre, tvBpmRate, tvMelodyName, tvMelodyLength, tvMelodyDate;

    public DataModel( int id_, int ivMelodyCover, int userProfileImage, String tvUserName, String tvMelodyName, String tvMelodyLength, String tvMelodyDate, int tvViewCount, int tvLikeCount, int tvCommentCount, int tvShareCount, String tvInstrumentsUsed, String tvMelodyGenre, String tvBpmRate) {
        this.tvUserName = tvUserName;
        this.tvMelodyName = tvMelodyName;
        this.tvMelodyLength = tvMelodyLength;
        this.ivMelodyCover = ivMelodyCover;
        this.userProfileImage = userProfileImage;
        this.tvMelodyDate = tvMelodyDate;
        this.tvViewCount = tvViewCount;
        this.tvLikeCount = tvLikeCount;
        this.tvCommentCount = tvCommentCount;
        this.tvShareCount = tvShareCount;
        this.tvInstrumentsUsed = tvInstrumentsUsed;
        this.tvMelodyGenre = tvMelodyGenre;
        this.id_ = id_;
        this.tvBpmRate = tvBpmRate;
    }

    public int getUserProfileImage() {
        return userProfileImage;
    }

    public int getIvMelodyCover() {
        return ivMelodyCover;
    }

    public String getTvMelodyGenre() {
        return tvMelodyGenre;
    }

    public String getTvInstrumentsUsed() {
        return tvInstrumentsUsed;
    }

    public String getTvBpmRate() {
        return tvBpmRate;
    }

    public int getId_() {
        return id_;
    }

    public String getTvUserName() {
        return tvUserName;
    }

    public String getTvMelodyName() {
        return tvMelodyName;
    }

    public String getTvMelodyLength() {
        return tvMelodyLength;
    }

    public String getTvMelodyDate() {
        return tvMelodyDate;
    }

    public int getTvViewCount() {
        return tvViewCount;
    }

    public int getTvLikeCount() {
        return tvLikeCount;
    }

    public int getTvCommentCount() {
        return tvCommentCount;
    }

    public int getTvShareCount() {
        return tvShareCount;
    }
}