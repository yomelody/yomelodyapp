package com.instamelody.instamelody.Models;

/**
 * Created by CBPC 41 on 7/27/2017.
 */

public class UserMelodyPlay {

    String melodypackid;
    String name;
    String added_by;
    String genre;
    String duration;
    String bpm;
    String playcounts;
    String likescounts;
    String sharecounts;
    String commentscounts;
    String melodyurl;
    String cover;
    String profilepic;
    String date;

    public String getMelodypackid() {
        return melodypackid;
    }

    public void setMelodypackid(String melodypackid) {
        this.melodypackid = melodypackid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdded_by() {
        return added_by;
    }

    public void setAdded_by(String added_by) {
        this.added_by = added_by;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getPlaycounts() {
        return playcounts;
    }

    public void setPlaycounts(String playcounts) {
        this.playcounts = playcounts;
    }

    public String getLikescounts() {
        return likescounts;
    }

    public void setLikescounts(String likescounts) {
        this.likescounts = likescounts;
    }

    public String getSharecounts() {
        return sharecounts;
    }

    public void setSharecounts(String sharecounts) {
        this.sharecounts = sharecounts;
    }

    public String getCommentscounts() {
        return commentscounts;
    }

    public void setCommentscounts(String commentscounts) {
        this.commentscounts = commentscounts;
    }

    public String getMelodyurl() {
        return melodyurl;
    }

    public void setMelodyurl(String melodyurl) {
        this.melodyurl = melodyurl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    String instrument;

}
