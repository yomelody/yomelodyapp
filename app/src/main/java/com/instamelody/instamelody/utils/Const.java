package com.instamelody.instamelody.utils;

/**
 * Created by Shubhansh Jaiswal on 05/07/17.
 */

public class Const {
    // web services
    public class ServiceType {
        public static final String HOST_URL = "http://52.89.220.199/";
        public static final String BASE_URL = HOST_URL + "api/";
        public static final String LOGIN = BASE_URL + "login.php";
        public static final String REGISTER = BASE_URL + "registration.php";
        public static final String GENERE = BASE_URL + "genere.php";
        public static final String MELODY = BASE_URL + "melody.php";
        public static final String LIKESAPI = BASE_URL + "likes.php";
        public static final String sharefile = BASE_URL + "sharefile.php";
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
        public static final String CREATE_GROUP=BASE_URL + "chatgroup.php";
        public static final String FOLLOWERS = BASE_URL + "followers.php";
        public static final String USERS_BIO = BASE_URL + "users_bio.php";
        public static final String UPDATEPROFILE = BASE_URL + "updateprofile.php";
        public static final String FORGOT_PASSWORD = BASE_URL + "forgot_password.php";
        public static final String JOINED_USERS = BASE_URL + "joined_users.php";
        public static final String MIXING_AUDIO_INSTRUMENTS = BASE_URL + "tempMixing.php";
        public static final String UPDATE_GROUP = BASE_URL + "UpdateGroup.php";
        public static final String READ_STATUS = BASE_URL + "Readstatus.php";
        public static final String AuthenticationKeyValue = "@_$%yomelody%audio#@mixing(app*";
        public static final String MixingAudio_InstrumentsAudio = BASE_URL + "audiomixing.php";
        public static final String AuthenticationKeyName = "ApiAuthenticationKey";
        public static final String LOGOUT = BASE_URL + "logout.php";
        public static final String change_social_status = BASE_URL + "change_social_status.php";
        public static final String social_status = BASE_URL + "social_status.php";
        public static final String description = BASE_URL + "description.php";

        public static final String JoinRecording = BASE_URL + "join_recording.php";

        public static final String ADVERTISEMENT = BASE_URL + "advertisement.php";
        public static final String PACKAGES = BASE_URL + "pakages.php";
        public static final String SUBSCRIPTION = BASE_URL + "subscription.php";
        public static final String SUBSCRIPTION_DETAIL = BASE_URL + "subscription_detail.php";
        public static final String SUB_DETAIL = BASE_URL + "sub_detail.php";
        public static final String TOTAL_COUNT = BASE_URL + "totalnewmessage.php";
        public static final String JOIN_DELETE = BASE_URL + "join_hide_recordings.php";
        public static final String BRAINTREE_FILES_CHECKOUT = BASE_URL + "braintree/files/checkout.php";
        public static final String BRAINTREE_FILES_CLIENT_TOKEN = BASE_URL + "braintree/files/client_token.php";
        public static final String BRAINTREE_FILES_TRANSACTION = BASE_URL + "braintree/files/transaction.php";



//        public static final String USERS_BIO = BASE_URL + "uploads/melody/instruments/";
    }

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String READ_NOTIFICATION = "readNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "instamelody_firebase";

    public static final String SOCIAL_STATUS_PREF = "SOCIAL_STATUS_PREF";
    public static final String FB_STATUS = "FB_STATUS";
    public static final String TWITTER_STATUS = "TWITTER_STATUS";
    public static final String GOOGLE_STATUS = "GOOGLE_STATUS";
    public static final String REC_SHARE_STATUS = "REC_SHARE_STATUS";
}
