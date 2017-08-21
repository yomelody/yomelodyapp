package com.instamelody.instamelody.utils;

/**
 * Created by Shubhansh Jaiswal on 05/07/17.
 */

public class Const {

    // web services
    public class ServiceType {
        private static final String HOST_URL = "http://52.89.220.199/";
        private static final String BASE_URL = HOST_URL + "api/";
        public static final String LOGIN = BASE_URL + "login.php";
        public static final String REGISTER = BASE_URL + "registration.php";
        public static final String GENERE = BASE_URL + "genere.php";
        public static final String MELODY = BASE_URL + "melody.php";
        public static final String LIKESAPI = BASE_URL + "likes.php";
        public static final String ADD_RECORDINGS = BASE_URL + "Add_Recording.php";
        public static final String PLAY_COUNT = BASE_URL + "playcount.php";
        public static final String COMMENTS = BASE_URL + "comments.php";
        public static final String RECORDINGS = BASE_URL + "recordings.php";
        public static final String UPLOAD_COVER_MELODY_FILE = BASE_URL + "upload_cover_melody_file.php";
        public static final String UPLOAD_FILE = BASE_URL + "uploadfile.php";
        public static final String SHARE_AUDIO_CHAT = BASE_URL + "ShareAudioChat.php";
        public static final String ACTIVITY = BASE_URL + "activity.php";
        public static final String COMMENT_LIST = BASE_URL + "commentlist.php";
        public static final String CONTACT_LIST = BASE_URL + "contactList.php";
        public static final String FOLLOWER_LIST = BASE_URL + "followerlist.php";
        public static final String CHAT = BASE_URL + "chat.php";
        public static final String PROFILE_DETAILS = BASE_URL + "profile_details.php";
        public static final String USER_CONVERSATION = BASE_URL + "UserConversation.php";
        public static final String MESSAGE_LIST = BASE_URL + "messageList.php";
        public static final String USER_CHAT_ID = BASE_URL + "user_chat_id.php";
        public static final String FOLLOWERS = BASE_URL + "followers.php";
        public static final String USERS_BIO = BASE_URL + "users_bio.php";
        public static final String UPDATEPROFILE = BASE_URL + "updateprofile.php";
        public static final String FORGOT_PASSWORD = BASE_URL + "forgot_password.php";
        public static final String GENERE1 = BASE_URL + "genere1.php";


//        public static final String USERS_BIO = BASE_URL + "uploads/melody/instruments/";
    }

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "instamelody_firebase";
}
