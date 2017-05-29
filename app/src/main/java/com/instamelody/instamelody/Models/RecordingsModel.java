package com.instamelody.instamelody.Models;

/**
 * Created by Saurabh Singh on 20/03/2017.
 */

public class RecordingsModel {

    //Used for storing melody details.

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

    public void setGenreId(String genreId) {
        genreId = "Genre: " + genreId;
        this.genreId = genreId;
    }



    /*    "recording_id": "739",
            "added_by": "43",
            "recording_topic": "neo4",
            "user_name": "optimus prime",
            "date_added": "2017-04-18 21:22:37",
            "like_status": 0,
            "like_count": "0",
            "play_count": "0",
            "comment_count": "0",
            "share_count": "0",
            "profile_url": "http://35.165.96.167/api/uploads/defaultpropik.jpg",
            "cover_url": "http://35.165.96.167/api/uploads/cover.jpg",
            "genre": "2",*/

}
