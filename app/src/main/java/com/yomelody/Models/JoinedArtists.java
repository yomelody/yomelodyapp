package com.yomelody.Models;

import java.io.Serializable;

/**
 * Created by Shubhansh Jaiswal on 27/04/17.
 */

public class JoinedArtists implements Serializable {

    String joined_image,joined_usr_name,user_id,joined_artists,recording_id,recording_name,
    recording_url,recording_duration,recording_date,like_status,play_counts,like_counts,share_counts,comment_counts,Join_Thumbnail;

    public String getJoin_Thumbnail() {
        return Join_Thumbnail;
    }

    public void setJoin_Thumbnail(String join_Thumbnail) {
        Join_Thumbnail = join_Thumbnail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJoined_artists() {
        return joined_artists;
    }

    public void setJoined_artists(String joined_artists) {
        this.joined_artists = joined_artists;
    }

    public String getRecording_id() {
        return recording_id;
    }

    public void setRecording_id(String recording_id) {
        this.recording_id = recording_id;
    }

    public String getRecording_name() {
        return recording_name;
    }

    public void setRecording_name(String recording_name) {
        this.recording_name = recording_name;
    }

    public String getRecording_url() {
        return recording_url;
    }

    public void setRecording_url(String recording_url) {
        this.recording_url = recording_url;
    }

    public String getRecording_duration() {
        return recording_duration;
    }

    public void setRecording_duration(String recording_duration) {
        this.recording_duration = recording_duration;
    }

    public String getRecording_date() {
        return recording_date;
    }

    public void setRecording_date(String recording_date) {
        this.recording_date = recording_date;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public String getPlay_counts() {
        return play_counts;
    }

    public void setPlay_counts(String play_counts) {
        this.play_counts = play_counts;
    }

    public String getLike_counts() {
        return like_counts;
    }

    public void setLike_counts(String like_counts) {
        this.like_counts = like_counts;
    }

    public String getShare_counts() {
        return share_counts;
    }

    public void setShare_counts(String share_counts) {
        this.share_counts = share_counts;
    }

    public String getComment_counts() {
        return comment_counts;
    }

    public void setComment_counts(String comment_counts) {
        this.comment_counts = comment_counts;
    }

    public String getJoined_image() {

        return joined_image;
    }

    public void setJoined_image(String joined_image) {
        this.joined_image = joined_image;
    }

    public String getJoined_usr_name() {
        return joined_usr_name;
    }

    public void setJoined_usr_name(String joined_usr_name) {
        this.joined_usr_name = joined_usr_name;
    }
}
