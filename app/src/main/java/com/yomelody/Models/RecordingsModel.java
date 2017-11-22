package com.yomelody.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Saurabh Singh on 20/03/2017.
 */

public class RecordingsModel implements Serializable {

    String recordingId;
    String addedBy;
    String recordingName;
    String userName;
    String recordingCreated;
    int likeStatus, likeCount, playCount, commentCount, shareCount;
    String userProfilePic;
    String recordingCover;
    String genreId;
    String genreName;
    String recordingurl;
    String joinCount;
    ArrayList<test_joinModel> arrJoin;
    ArrayList<Joined_model> join_arr_model = new ArrayList<>();

    public ArrayList<test_joinModel> getArrJoin() {
        return arrJoin;
    }

    public void setArrJoin(ArrayList<test_joinModel> arrJoin) {
        this.arrJoin = arrJoin;
    }

    public String getRecordingurl() {
        return recordingurl;
    }

    public void setRecordingurl(String recordingurl) {
        this.recordingurl = recordingurl;
    }

    public ArrayList<Joined_model> getJoin_arr_model() {
        return join_arr_model;
    }

    public void setJoin_arr_model(ArrayList<Joined_model> join_arr_model) {
        this.join_arr_model = join_arr_model;
    }


    String thumnailUrl;
    ArrayList joinUrl;

    public ArrayList getJoinUrl() {
        return joinUrl;
    }

    public void setJoinUrl(ArrayList joinUrl) {
        this.joinUrl = joinUrl;
    }

    public String getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(String joinCount) {
        this.joinCount = joinCount;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getRecordingId() {
        return recordingId;
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = recordingId;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public void setLikeStatus(int likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        userName = "@" + userName;
        this.userName = userName;
    }

    public String getRecordingCreated() {
        return recordingCreated;
    }

    public void setRecordingCreated(String recordingCreated) {
        recordingCreated = recordingCreated.substring(2, recordingCreated.indexOf(" "));
        recordingCreated = recordingCreated.replace('-', '/');
        this.recordingCreated = recordingCreated;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getRecordingCover() {
        return recordingCover;
    }

    public void setRecordingCover(String recordingCover) {
        this.recordingCover = recordingCover;
    }

    public String getGenreId() {
        return genreId;
    }

    public int getLikeStatus() {
        return likeStatus;
    }

    public String getrecordingurl() {
        return recordingurl;
    }

    public void setrecordingurl(String recordingurl) {
        this.recordingurl = recordingurl;
    }

    public void setGenreId(String genreId) {
        genreId = "Genre: " + genreId;
        this.genreId = genreId;
    }

    public String getThumnailUrl() {
        return thumnailUrl;
    }

    public void setThumnailUrl(String thumnailUrl) {
        this.thumnailUrl = thumnailUrl;
    }
}
