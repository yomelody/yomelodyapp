package com.yomelody.Models;

import com.yomelody.utils.AppHelper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Shubhansh Jaiswal on 12/2/2016.
 */

public class MelodyCard implements Serializable {

    //Used for storing melody details.
    int addedBy, playCount, likeCount, shareCount, commentCount;
    String userName;
    String userProfilePic;
    String melodyName;
    String melodyCreated;
    String melodyLength;
    String instrumentCount;
    String melodyBpm;
    String genreId;
    String melodyPackId;
    String melodyCover;
    String genreName;
    int LikeStatus;
    String MelodyURL;
    String AddByAdmin;
    String melodyDuration;
    String melodyThumbnail;
    ArrayList<MelodyInstruments> melodyInstrumentsList;

    public String getMelodyDuration() {
        return melodyDuration;
    }

    public void setMelodyDuration(String melodyDuration) {
        this.melodyDuration = melodyDuration;
    }

    public String getMelodyPackId() {
        return melodyPackId;
    }

    public void setMelodyPackId(String melodyPackId) {
        this.melodyPackId = melodyPackId;
    }

    public String getMelodyThumbnail() {return melodyThumbnail;
    }

    public void setMelodyThumbnail(String melodyThumbnail) {this.melodyThumbnail = melodyThumbnail;
    }

    public String getAddByAdmin() {return AddByAdmin;
    }

    public void setAddByAdmin(String addByAdmin) {AddByAdmin = addByAdmin;
    }

//    public int getAddedBy() {
//        return addedBy;
//    }
//
//    public void setAddedBy(int addedBy) {
//        this.addedBy = addedBy;
//    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public String getInstrumentCount() {
        return instrumentCount;
    }

    public void setInstrumentCount(String instrumentCount) {
        if (instrumentCount.equals("1")) {
            instrumentCount = instrumentCount + " Instrumental";
        } else {
            instrumentCount = instrumentCount + " Instrumentals";
        }
        this.instrumentCount = instrumentCount;
    }

    public String getMelodyBpm() {
        return melodyBpm;
    }

    public void setMelodyBpm(String melodyBpm) {
        melodyBpm = "BPM : " + melodyBpm;
        this.melodyBpm = melodyBpm;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getLikeStatus() {
        return LikeStatus;
    }

    public String getMelodyURL() {
        return MelodyURL;
    }

    public void setLikeStatus(int LikeStatus) {
        this.LikeStatus = LikeStatus;
    }

    public void setMelodyURL(String MelodyURL) {
        this.MelodyURL = MelodyURL;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getMelodyCover() {
        return melodyCover;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        genreName = "Genre : "+ genreName;
        this.genreName = genreName;
    }

    public void setMelodyCover(String melodyCover) {
        this.melodyCover = melodyCover;
    }

    public String getMelodyName() {
        return melodyName;
    }

    public void setMelodyName(String melodyName) {
        this.melodyName = melodyName;
    }

    public String getMelodyCreated() {
        return melodyCreated;
    }

    public void setMelodyCreated(String melodyCreated) {
        melodyCreated = melodyCreated.substring(2, melodyCreated.indexOf(" "));
        melodyCreated = melodyCreated.replace('-', '/');
        this.melodyCreated = melodyCreated;
    }

    public String getMelodyLength() {
        return melodyLength;
    }

    public ArrayList<MelodyInstruments> getMelodyInstrumentsList() {
        return melodyInstrumentsList;
    }

    public void setMelodyInstrumentsList(ArrayList<MelodyInstruments> melodyInstrumentsList) {
        this.melodyInstrumentsList = melodyInstrumentsList;
    }

    public void setMelodyLength(String melodyLength) {
//        AppHelper.sop("melodyLength=="+melodyLength);
        int secs=0;
        if (melodyLength.equals(null)) {
            melodyLength = "0";
            secs = Integer.parseInt(melodyLength);
        } else {
            try{
                secs = Integer.parseInt(melodyLength);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }

        }
        int mins = 0, hours = 0;
        if (secs > 0) {
            if (secs > 59) {
                mins = secs / 60;
                secs = secs % 60;
                if (mins > 59) {
                    hours = mins / 60;
                    mins = mins % 60;
                    melodyLength = adjust(hours, mins, secs);
                } else {
                    melodyLength = adjust(hours, mins, secs);
                }
            } else {
                melodyLength = adjust(hours, mins, secs);
//                AppHelper.sop("else=secs=="+secs+"=mins="+mins+"=hours="+hours+"=melodyLength="+melodyLength);
//                melodyLength = "00:00:" + secs;
            }
        }
        this.melodyLength = melodyLength;
    }

    public String adjust(int a, int b, int c) {
//        AppHelper.sop("adjust=a="+a+"=b="+b+"=c="+c);
        String h = "00", m = "00", s = "00";
        String value;
        if (a < 10) {
            h = "0" + String.valueOf(a);
        }

        if (b < 10) {
            m = "0" + String.valueOf(b);
        }
        else{
            m = String.valueOf(b);
        }

        if (c < 10) {
            s = "0" + String.valueOf(c);
        }
        else {
            s = String.valueOf(c);
        }

        value = h + ":" + m + ":" + s;
        return value;
    }
}
