package com.yomelody.Parse;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.yomelody.ChatActivity;
import com.yomelody.ContactsActivity;

import com.yomelody.JoinActivity;
import com.yomelody.Models.AdvertisePagingData;
import com.yomelody.Models.Comments;
import com.yomelody.Models.Contacts;
import com.yomelody.Models.Genres;
import com.yomelody.Models.JoinedArtists;
import com.yomelody.Models.JoinedUserProfile;
import com.yomelody.Models.MelodyCard;
import com.yomelody.Models.MelodyInstruments;
import com.yomelody.Models.Message;
import com.yomelody.Models.MixingData;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.Models.SharedAudios;
import com.yomelody.Models.SubscriptionPackage;
import com.yomelody.StudioActivity;
import com.yomelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.yomelody.utils.Const.ServiceType.BASE_URL;

/**
 * Created by Shubhansh Jaiswal on 08/02/17.
 */

public class ParseContents {

    Context mContext;

    public ParseContents(Context context) {
        mContext = context;
    }

    public static ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();
    ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<Genres> genreList = new ArrayList<>();
    static ArrayList<MelodyInstruments> instrumentsList = new ArrayList<>();
    private ArrayList<MelodyInstruments> instrumentsListLocal = new ArrayList<>();
    String KEY_FLAG = "flag";
    String KEY_INFO = "info";
    String KEY_RESPONSE = "response";//JSONArray
    String KEY_RESULT = "result";//JSONArray
    String KEY_INSTRUMENTS = "instruments";//JSONArray\
    String mpid;

    String KEY_MELODY_PACK_ID = "melodypackid";
    String KEY_ADDED_BY = "added_by";
    String KEY_USERNAME = "username";
    String KEY_MELODY_NAME = "name";
    String KEY_GENRE_ID = "genre";
    String KEY_DURATION = "duration";
    String KEY_INSTRUMENT_COUNT = "instrument";
    String KEY_BPM = "bpm";
    String KEY_URL = "url";
    String KEY_PLAY_COUNTS = "playcounts";
    String KEY_LIKES_COUNTS = "likescounts";
    String KEY_SHARE_COUNTS = "sharecounts";
    String KEY_COMMENTS_COUNTS = "commentscounts";
    String KEY_COVER = "cover";
    String KEY_PROFILE_PIC = "profilepic";
    String KEY_DATE = "date";
    String like_status = "like_status";
    String melodyurl = "melodyurl";

    String KEY_INSTRUMENT_ID = "id";
    String KEY_INSTRUMENT_NAME = "instruments_name";
    String KEY_INSTRUMENT_TYPE = "instruments_type";
    String KEY_INSTRUMENT_BPM = "bpm";
    String KEY_INSTRUMENT_FILE_SIZE = "file_size";
    String KEY_INSTRUMENT_URL = "instrument_url";
    String KEY_INSTRUMENT_DURATION = "duration";
    String KEY_INSTRUMENT_DATE = "uploadeddate";
    String KEY_INSTRUMENT_PROFILE_PIC = "profilepic";
    String KEY_INSTRUMENT_COVER_PIC = "coverpic";
    String KEY_INSTRUMENT_USERNAME = "username";

    String KEY_GENRENAME_ID = "id";
    String KEY_GENRE_NAME = "genre_name";

    String ADV_ID = "id";
    String ADV_NAME = "adv_name";
    String ADV_IMAGE = "adv_image";
    String ADV_URL = "adv_url";
    String ADV_START_DATE = "start_date";
    String ADV_END_DATE = "end_date";

    String SUBSCRIPTION_PACKAGE_ID = "package_id";
    String SUBSCRIPTION_PACKAGE_NAME = "package_name";
    String SUBSCRIPTION_TOTAL_MELODY = "total_melody";
    String SUBSCRIPTION_RECORDING_TIME = "recording_time";
    String SUBSCRIPTION_RECORDINGTIME = "RecordTime";
    String SUBSCRIPTION_COST = "cost";
    JSONArray audiosDetailsArray;

    public ArrayList<MelodyCard> parseMelodyPacks(String response, ArrayList<MelodyCard> melodyList, ArrayList<MelodyInstruments> instrumentList) {
        instrumentsListLocal.clear();
        JSONObject jsonObject;
        JSONArray jsonArray, instrumentArray;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    instrumentsListLocal.clear();
                    MelodyCard card = new MelodyCard();
                    JSONObject cardJson = jsonArray.getJSONObject(i);
                    card.setMelodyPackId(cardJson.getString(KEY_MELODY_PACK_ID));
                    card.setAddByAdmin(cardJson.getString("added_by_admin"));
                    card.setMelodyThumbnail(cardJson.getString("thumbnail_url"));
                    card.setMelodyDuration(cardJson.getString("duration"));
//                    card.setAddedBy(cardJson.getInt(KEY_ADDED_BY));
                    card.setUserName(cardJson.getString(KEY_USERNAME));
                    card.setMelodyName(cardJson.getString(KEY_MELODY_NAME));
                    card.setGenreId(cardJson.getString(KEY_GENRE_ID));
                    card.setGenreName(cardJson.getString(KEY_GENRE_NAME));
                    card.setMelodyLength(cardJson.getString(KEY_DURATION));
                    AppHelper.sop("KEY_DURATION=="+cardJson.getString(KEY_DURATION));
//                    card.setInstrumentCount(cardJson.getString(KEY_INSTRUMENT_COUNT));
                    card.setMelodyBpm(cardJson.getString(KEY_BPM));
                    card.setPlayCount(cardJson.getInt(KEY_PLAY_COUNTS));
                    card.setLikeCount(cardJson.getInt(KEY_LIKES_COUNTS));
                    card.setShareCount(cardJson.getInt(KEY_SHARE_COUNTS));
                    card.setCommentCount(cardJson.getInt(KEY_COMMENTS_COUNTS));
                    card.setMelodyCover(cardJson.getString(KEY_COVER));
                    card.setUserProfilePic(cardJson.getString(KEY_PROFILE_PIC));
                    card.setMelodyCreated(cardJson.getString(KEY_DATE));
                    card.setLikeStatus(cardJson.getInt(like_status));
                    card.setMelodyURL(cardJson.getString(melodyurl));

                    instrumentArray = cardJson.getJSONArray(KEY_INSTRUMENTS);
                    for (int j = 0; j < instrumentArray.length(); j++) {
                        MelodyInstruments melodyInstruments = new MelodyInstruments();
                        JSONObject instrumentsJson = instrumentArray.getJSONObject(j);
                        MelodyInstruments.setInstrumentId(instrumentsJson.getInt(KEY_INSTRUMENT_ID));
                        int a = instrumentsJson.getInt(KEY_INSTRUMENT_ID);
                        melodyInstruments.setInstrumentName(instrumentsJson.getString(KEY_INSTRUMENT_NAME));
                        melodyInstruments.setInstrumentType(instrumentsJson.getString(KEY_INSTRUMENT_TYPE));
                        melodyInstruments.setMelodyPacksId(instrumentsJson.getInt(KEY_MELODY_PACK_ID));
                        melodyInstruments.setInstrumentBpm(instrumentsJson.getString(KEY_INSTRUMENT_BPM));
                        melodyInstruments.setInstrumentFileSize(instrumentsJson.getString(KEY_INSTRUMENT_FILE_SIZE));
                        melodyInstruments.setInstrumentFile(instrumentsJson.getString(KEY_INSTRUMENT_URL));
                        melodyInstruments.setInstrumentLength(instrumentsJson.getString(KEY_INSTRUMENT_DURATION));
                        melodyInstruments.setInstrumentCreated(instrumentsJson.getString(KEY_INSTRUMENT_DATE));
                        melodyInstruments.setUserProfilePic(instrumentsJson.getString(KEY_INSTRUMENT_PROFILE_PIC));
                        melodyInstruments.setInstrumentCover(instrumentsJson.getString(KEY_INSTRUMENT_COVER_PIC));
                        melodyInstruments.setUserName(instrumentsJson.getString(KEY_INSTRUMENT_USERNAME));
                        instrumentList.add(melodyInstruments);
                        instrumentsListLocal.add(melodyInstruments);
                    }

//                    AppHelper.sop("=instrumentsListLocal=size="+instrumentsListLocal.size());
//                    AppHelper.sop("=instrumentArray=size="+instrumentArray.length());
                    card.setInstrumentCount(instrumentArray.length()+"");
                    card.setMelodyInstrumentsList(instrumentsListLocal);
                    if (instrumentArray.length()<=0){
//                        card.setMelodyLength("00");
                    }else {
                        card.setMelodyLength(instrumentArray.getJSONObject(0).getString(KEY_INSTRUMENT_DURATION));
                    }
                    instrumentsList = instrumentList;
                    melodyList.add(card);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return melodyList;
    }

    public ArrayList<MelodyInstruments> getInstruments() {
        return instrumentsList;
    }

    public ArrayList<MelodyInstruments> parseInstrumentsAttached(String response, ArrayList<MelodyInstruments> instrumentList, String mpid) {

        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(response);
            StudioActivity.list.clear();
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                try {
                    JSONObject selectedObj = jsonArray.getJSONObject(Integer.parseInt(mpid));
                    jsonArray = selectedObj.getJSONArray(KEY_INSTRUMENTS);
                }catch (Exception ex){
                    ex.printStackTrace();
                }



                MelodyInstruments.setInstrumentCount(jsonArray.length());  // This code added by Abhishek
                Log.d("Count", "" + jsonArray.length());
                for (int j = 0; j < jsonArray.length(); j++) {

                    MelodyInstruments melodyInstruments = new MelodyInstruments();
                    JSONObject instrumentsJson = jsonArray.getJSONObject(j);
                    melodyInstruments.setInstrumentId(instrumentsJson.getInt(KEY_INSTRUMENT_ID));
                    melodyInstruments.setInstrumentName(instrumentsJson.getString(KEY_INSTRUMENT_NAME));
                    melodyInstruments.setInstrumentType(instrumentsJson.getString(KEY_INSTRUMENT_TYPE));
                    melodyInstruments.setMelodyPacksId(instrumentsJson.getInt(KEY_MELODY_PACK_ID));
                    melodyInstruments.setInstrumentBpm(instrumentsJson.getString(KEY_INSTRUMENT_BPM));
                    melodyInstruments.setInstrumentFileSize(instrumentsJson.getString(KEY_INSTRUMENT_FILE_SIZE));
                    melodyInstruments.setInstrumentFile(instrumentsJson.getString(KEY_INSTRUMENT_URL));
                    melodyInstruments.setInstrumentLength(instrumentsJson.getString(KEY_INSTRUMENT_DURATION));
                    melodyInstruments.setInstrumentCreated(instrumentsJson.getString(KEY_INSTRUMENT_DATE));
                    melodyInstruments.setUserProfilePic(instrumentsJson.getString(KEY_INSTRUMENT_PROFILE_PIC));
                    melodyInstruments.setInstrumentCover(instrumentsJson.getString(KEY_INSTRUMENT_COVER_PIC));
                    melodyInstruments.setUserName(instrumentsJson.getString(KEY_INSTRUMENT_USERNAME));
                    melodyInstruments.setAudioType("instrument");
                    instrumentList.add(melodyInstruments);
                    StudioActivity.list.add(j, new MixingData(String.valueOf(instrumentsJson.getInt(KEY_INSTRUMENT_ID)), "5", "-5", "20", "0", "44100", "0", "0", "0", "2000", "0", "0", "0", "0", "0", "0", "0", instrumentsJson.getString(KEY_INSTRUMENT_URL).replace(BASE_URL, ""), String.valueOf(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return instrumentList;
    }

    public ArrayList<Genres> parseGenres(String response, ArrayList<Genres> genreList) {

        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Genres genres = new Genres();
                    JSONObject genreJson = jsonArray.getJSONObject(i);
                    genres.setId(genreJson.getString(KEY_GENRENAME_ID));
                    genres.setName(genreJson.getString(KEY_GENRE_NAME));
                    genreList.add(genres);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genreList;
    }

    public ArrayList<Genres> getGenreList() {
        return genreList;
    }

    public ArrayList<RecordingsModel> parseRecordings(String response, ArrayList<RecordingsModel> recordingList) {

        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    RecordingsModel card = new RecordingsModel();
                    JSONObject cardJson = jsonArray.getJSONObject(i);

                    card.setRecordingCreated(cardJson.getString("date_added"));
                    card.setGenreId(cardJson.getString("genre"));
                    card.setRecordingName(cardJson.getString("recording_topic"));
                    card.setUserName(cardJson.getString("user_name"));
                    card.setRecordingId(cardJson.getString("recording_id"));
                    card.setPlayCount(cardJson.getInt("play_count"));
                    card.setCommentCount(cardJson.getInt("comment_count"));
                    card.setLikeCount(cardJson.getInt("like_count"));
                    card.setShareCount(cardJson.getInt("share_count"));
                    card.setRecordingCover(cardJson.getString("cover_url"));
                    card.setUserProfilePic(cardJson.getString("profile_url"));
                    card.setGenreName(cardJson.getString("genre_name"));
//                    card.setTvContributeDate(cardJson.getString("30/02/17"));
//                    card.setTvContributeLength(cardJson.getString("recordings"));

//                    instrumentArray = cardJson.getJSONArray(KEY_INSTRUMENTS);
//                    for (int j = 0; j < instrumentArray.length(); j++) {
//                        MelodyInstruments melodyInstruments = new MelodyInstruments();
//                        JSONObject instrumentsJson = instrumentArray.getJSONObject(j);
//                        melodyInstruments.setInstrumentId(instrumentsJson.getInt(KEY_INSTRUMENT_ID));
//                        int a = instrumentsJson.getInt(KEY_INSTRUMENT_ID);
//                        melodyInstruments.setInstrumentName(instrumentsJson.getString(KEY_INSTRUMENT_NAME));
//                        melodyInstruments.setInstrumentType(instrumentsJson.getString(KEY_INSTRUMENT_TYPE));
//                        melodyInstruments.setMelodyPacksId(instrumentsJson.getInt(KEY_MELODY_PACK_ID));
//                        melodyInstruments.setInstrumentBpm(instrumentsJson.getString(KEY_INSTRUMENT_BPM));
//                        melodyInstruments.setInstrumentFileSize(instrumentsJson.getString(KEY_INSTRUMENT_FILE_SIZE));
//                        melodyInstruments.setInstrumentFile(instrumentsJson.getString(KEY_INSTRUMENT_URL));
//                        melodyInstruments.setInstrumentLength(instrumentsJson.getString(KEY_INSTRUMENT_DURATION));
//                        melodyInstruments.setInstrumentCreated(instrumentsJson.getString(KEY_INSTRUMENT_DATE));
//                        melodyInstruments.setUserProfilePic(instrumentsJson.getString(KEY_INSTRUMENT_PROFILE_PIC));
//                        melodyInstruments.setInstrumentCover(instrumentsJson.getString(KEY_INSTRUMENT_COVER_PIC));
//                        melodyInstruments.setUserName(instrumentsJson.getString(KEY_INSTRUMENT_USERNAME));
//                        instrumentList.add(melodyInstruments);
//                    }
//                    instrumentsList = instrumentList;

                    recordingList.add(card);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recordingList;
    }

    public ArrayList<Contacts> parseContacts(ArrayList<Contacts> chatList, String response) {

        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_INFO);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Contacts contacts = new Contacts();
                    JSONObject commentJson = jsonArray.getJSONObject(i);
                    contacts.setUserProfileImage(commentJson.getString("profilepic"));
                    contacts.setfName(commentJson.getString("fname"));
                    contacts.setlName(commentJson.getString("lname"));
                    contacts.setUser_id(commentJson.getString("id"));
                    contacts.setDeviceToken(commentJson.getString("devicetoken"));
                    contacts.setUserName(commentJson.getString("username"));
                    chatList.add(contacts);
                }
            } else {
                ContactsActivity.rlNoContacts.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatList;
    }

    public ArrayList<Message> parseChats(String response, ArrayList<Message> chatList) {

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                JSONObject result = jsonObject.getJSONObject("result");
                JSONArray resultArray = result.getJSONArray("message LIst");
                String uname = null;
                for (int i = 0; i < resultArray.length(); i++) {
                    Message message = new Message();
                    JSONObject chatJson = resultArray.getJSONObject(i);
                    message.setMessage(chatJson.getString("message"));
                    message.setId(chatJson.getString("id"));
                    message.setDateTime(chatJson.getString("dateTime"));
//                    message.setCreatedAt(chatJson.getString("sendat"));
                    message.setSenderId(chatJson.getString("senderID"));
                    SharedPreferences loginSharedPref = mContext.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    String profilePic = loginSharedPref.getString("profilePic", null);
                    message.setProfilePic(profilePic);
                    uname = chatJson.getString("receiver_name");
                    chatList.add(message);
                }
                ChatActivity.tvUserName.setText(uname);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return chatList;
    }

    public ArrayList<Comments> parseComments(String response, ArrayList<Comments> commentList) {

        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Comments comments = new Comments();
                    JSONObject commentJson = jsonArray.getJSONObject(i);
                    comments.setComment_id(commentJson.getString("comment_id"));
                    comments.setUser_id(commentJson.getString("user_id"));
                    comments.setTvRealName(commentJson.getString("name"));
                    comments.setTvUsername(commentJson.getString("user_name"));
                    comments.setTvMsg(commentJson.getString("comment_text"));
                    comments.setTvTime(commentJson.getString("comment_time"));
                    comments.setDisplay_time(commentJson.getString("display_time"));
                    comments.setUserProfileImage(commentJson.getString("user_profile_url"));
                    commentList.add(comments);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    public ArrayList<RecordingsModel> parseAudio(String response, ArrayList<RecordingsModel> recordingList, ArrayList<RecordingsPool> recordingsPools) {
        JSONObject jsonObject;
        JSONArray jsonArray, instrumentArray, JoinedArray;

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    RecordingsModel card = new RecordingsModel();
                    JSONObject cardJson = jsonArray.getJSONObject(i);
                    card.setAddedBy(cardJson.getString("added_by"));

                    card.setRecordingCreated(cardJson.getString("date_added"));
                    card.setGenreId(cardJson.getString("genre"));
                    card.setRecordingName(cardJson.getString("recording_topic"));
                    card.setUserName(cardJson.getString("user_name"));
                    card.setRecordingId(cardJson.getString("recording_id"));
                    card.setPlayCount(cardJson.getInt("play_count"));
                    card.setCommentCount(cardJson.getInt("comment_count"));
                    card.setLikeCount(cardJson.getInt("like_count"));
                    card.setJoinCount(cardJson.getString("join_count"));
                    card.setLikeStatus(cardJson.getInt("like_status"));
                    card.setJoinCount(cardJson.getString("join_count"));
                    card.setThumnailUrl(cardJson.getString("thumbnail_url"));
                    card.setIsPublic(cardJson.getString("public"));

                    if (cardJson.isNull("recording_url")) {
                        card.setrecordingurl("");
                    } else {
                        card.setrecordingurl(cardJson.getString("recording_url"));
                    }

                    card.setShareCount(cardJson.getInt("share_count"));
                    card.setRecordingCover(cardJson.getString("cover_url"));
                    card.setUserProfilePic(cardJson.getString("profile_url"));
                    card.setGenreName(cardJson.getString("genre_name"));

                    ArrayList arrayList=new ArrayList();


                    JoinedArray = cardJson.getJSONArray("joined");

                    try {
                        if (JoinedArray.length() > 0) {
                            for (int j = 0; j < JoinedArray.length(); j++) {
                                if (JoinedArray.get(j) instanceof JSONObject){
                                    JSONObject jsonObject1 = new JSONObject(JoinedArray.get(j).toString());
//                                JSONObject jsonObject1 = JoinedArray.getJSONObject(j);

                                    //JSONArray jsonArray1=new JSONArray(jsonObject1.getJSONArray(jsonObject1));

                                    arrayList.add(jsonObject1.get("recording_url"));
                                }

                            }
                            card.setJoinUrl(arrayList);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                    if (cardJson.isNull("recordings")) {
                        Log.d("Error", "pls check");
                    } else {
                        instrumentArray = cardJson.getJSONArray("recordings");


                        for (int j = 0; j < instrumentArray.length(); j++) {
                            RecordingsPool rp = new RecordingsPool();
                            JSONObject instrumentJson = instrumentArray.getJSONObject(j);
                            rp.setAddedById(instrumentJson.getString("added_by_id"));
                            rp.setUserName(instrumentJson.getString("user_name"));
                            rp.setName(instrumentJson.getString("name"));
                            rp.setCoverUrl(instrumentJson.getString("cover_url"));
                            rp.setProfileUrl(instrumentJson.getString("profile_url"));
                            rp.setDateAdded(instrumentJson.getString("date_added"));
                            rp.setDuration(instrumentJson.getString("duration"));
                            rp.setRecordingUrl(instrumentJson.getString("recording_url"));
                            rp.setInstruments(instrumentJson.getString("instruments"));
                            recordingsPools.add(rp);
                        }
                    }



                    recordingList.add(card);
//                    card.setTvContributeDate(cardJson.getString("30/02/17"));
//                    card.setTvContributeLength(cardJson.getString("recordings"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recordingList;
    }

    public ArrayList<JoinedArtists> parseJoin(String response, ArrayList<JoinedArtists> JoinArtist) {
        JSONObject jsonObject;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JoinedArtists join = new JoinedArtists();
                    JSONObject cardJoin = jsonArray.getJSONObject(i);
                    join.setUser_id(cardJoin.getString("user_id"));
                    join.setJoined_image(cardJoin.getString("profile_pic"));
                    join.setJoined_usr_name(cardJoin.getString("user_name"));
                    join.setJoined_artists(cardJoin.getString("joined_artists"));
                    join.setRecording_id(cardJoin.getString("recording_id"));
                    join.setRecording_name(cardJoin.getString("recording_name"));
                    join.setRecording_url(cardJoin.getString("recording_url"));
                    join.setRecording_duration(cardJoin.getString("recording_duration"));
                    join.setRecording_date(cardJoin.getString("recording_date"));
                    join.setLike_status(cardJoin.getString("like_status"));
                    join.setPlay_counts(cardJoin.getString("play_counts"));
                    join.setLike_counts(cardJoin.getString("like_counts"));
                    join.setShare_counts(cardJoin.getString("share_counts"));
                    join.setComment_counts(cardJoin.getString("comment_counts"));
                    join.setJoin_Thumbnail(cardJoin.getString("thumbnail_url"));
                    join.setRecCover(cardJoin.getString("recording_cover"));
                    //    instrumentsList = instrumentList;
                    JoinArtist.add(join);
                    JoinActivity.listProfile.add(i, new JoinedUserProfile(String.valueOf(cardJoin.getString("user_id")), "0"));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return JoinArtist;
    }

    public ArrayList<MelodyInstruments> parseJoinInstrument(String response, ArrayList<MelodyInstruments> instrumentList, int mpid) {

        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                JSONObject selectedObj = jsonArray.getJSONObject(mpid);

                jsonArray = selectedObj.getJSONArray(KEY_INSTRUMENTS);

                MelodyInstruments.setInstrumentCount(jsonArray.length());  // This code added by Abhishek
                Log.d("Count", "" + jsonArray.length());
                for (int j = 0; j < jsonArray.length(); j++) {

                    MelodyInstruments melodyInstruments = new MelodyInstruments();
                    JSONObject instrumentsJson = jsonArray.getJSONObject(j);
                    MelodyInstruments.setInstrumentId(instrumentsJson.getInt("instrument_id"));
                    melodyInstruments.setInstrumentName(instrumentsJson.getString("instruments_name"));
                    melodyInstruments.setInstrumentType(instrumentsJson.getString("instruments_type"));
                    melodyInstruments.setInstrumentBpm(instrumentsJson.getString("bpm"));
                    melodyInstruments.setInstrumentFileSize(instrumentsJson.getString("file_size"));
                    melodyInstruments.setInstrumentFile(instrumentsJson.getString("instrument_url"));
                    melodyInstruments.setInstrumentLength(instrumentsJson.getString("duration"));
                    melodyInstruments.setInstrumentCreated(instrumentsJson.getString("uploadeddate"));
                    melodyInstruments.setUserProfilePic(instrumentsJson.getString("profilepic"));
                    melodyInstruments.setInstrumentCover(instrumentsJson.getString("coverpic"));
                    melodyInstruments.setUserName(instrumentsJson.getString("username"));
                    instrumentList.add(melodyInstruments);
                    StudioActivity.list.add(j, new MixingData(String.valueOf(instrumentsJson.getInt("instrument_id")), "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", instrumentsJson.getString(KEY_INSTRUMENT_URL).replace(BASE_URL, ""), String.valueOf(j)));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return instrumentList;
    }

    public ArrayList<AdvertisePagingData> parseAdvertisePaging(String response, ArrayList<AdvertisePagingData> pagingDataArrayList) {
        JSONObject jsonObject, advertiseJson;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            String flag = jsonObject.getString(KEY_FLAG);
            if (flag.equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    AdvertisePagingData advertisePagingData = new AdvertisePagingData();
                    advertiseJson = jsonArray.getJSONObject(i);
                    advertisePagingData.setId_adv(advertiseJson.getString(ADV_ID));
                    advertisePagingData.setAdv_name(advertiseJson.getString(ADV_NAME));
                    advertisePagingData.setAdv_image(advertiseJson.getString(ADV_IMAGE));
                    advertisePagingData.setAdv_url(advertiseJson.getString(ADV_URL));
                    advertisePagingData.setStart_date(advertiseJson.getString(ADV_START_DATE));
                    advertisePagingData.setEnd_date(advertiseJson.getString(ADV_END_DATE));
                    pagingDataArrayList.add(advertisePagingData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pagingDataArrayList;
    }

    public ArrayList<SubscriptionPackage> parsePackageSubscription(String response, ArrayList<SubscriptionPackage> subscriptionPackageArrayList) {
        JSONObject jsonObject, subscriptionJson;
        JSONArray jsonArray;

        try {
            jsonObject = new JSONObject(response);
            String flag = jsonObject.getString(KEY_FLAG);
            if (flag.equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    SubscriptionPackage subscriptionPackage = new SubscriptionPackage();
                    subscriptionJson = jsonArray.getJSONObject(i);
                    subscriptionPackage.setPackage_id(subscriptionJson.getString(SUBSCRIPTION_PACKAGE_ID));
                    subscriptionPackage.setPackage_name(subscriptionJson.getString(SUBSCRIPTION_PACKAGE_NAME));
                    subscriptionPackage.setTotal_melody(subscriptionJson.getString(SUBSCRIPTION_TOTAL_MELODY));
                    subscriptionPackage.setRecording_time(subscriptionJson.getString(SUBSCRIPTION_RECORDING_TIME));
                    subscriptionPackage.setRecordingtime(subscriptionJson.getString(SUBSCRIPTION_RECORDINGTIME));
                    subscriptionPackage.setCost(subscriptionJson.getString(SUBSCRIPTION_COST));
                    subscriptionPackageArrayList.add(subscriptionPackage);

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subscriptionPackageArrayList;
    }

    public ArrayList<SharedAudios> parseSharedJoin(int pos, ArrayList<Message> message) {
        try {
            audiosDetailsArray = message.get(pos).getAudioDetails();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        if (audiosDetailsArray != null && audiosDetailsArray.length() > 0) {
            if (audiosDetailsArray != null && audiosDetailsArray.length() > 0) {
                for (int j = 0; j < audiosDetailsArray.length(); j++) {
                    try {
                        JSONObject detailsJson = audiosDetailsArray.getJSONObject(j);
                        if (detailsJson.has("recordings") && !detailsJson.get("recordings").equals(null) && !detailsJson.get("recordings").equals("")) {
                            JSONArray sharedAudiosArray = detailsJson.getJSONArray("recordings");

                            if (sharedAudiosArray.length() > 0) {
                                for (int k = 0; k < sharedAudiosArray.length(); k++) {
                                    SharedAudios sharedAudios = new SharedAudios();
                                    JSONObject audioJson = sharedAudiosArray.getJSONObject(k);
                                    sharedAudios.setAddedById(audioJson.getString("added_by_id"));
                                    sharedAudios.setUserName(audioJson.getString("user_name"));
                                    sharedAudios.setName(audioJson.getString("name"));
                                    sharedAudios.setProfileUrl(audioJson.getString("profile_url"));
                                    sharedAudios.setDateAdded(audioJson.getString("date_added"));
                                    sharedAudios.setDuration(audioJson.getString("duration"));
                                    sharedAudios.setRecordingUrl(audioJson.getString("recording_url"));
                                    sharedAudioList.add(sharedAudios);
                                }
                            }
                        }
                        else if (detailsJson.has("instruments") && !detailsJson.get("instruments").equals(null) && !detailsJson.get("instruments").equals("")) {
                            JSONArray instrumentsArray = detailsJson.getJSONArray("instruments");
                            SharedAudios sharedAudios = new SharedAudios();

                            sharedAudios.setAddedById(detailsJson.getString("added_by_admin"));
                            sharedAudios.setUserName(detailsJson.getString("username"));
                            sharedAudios.setName(detailsJson.getString("name"));
                            sharedAudios.setProfileUrl(detailsJson.getString("profilepic"));
                            sharedAudios.setDateAdded(detailsJson.getString("date"));
                            sharedAudios.setDuration(detailsJson.getString("duration"));
                            sharedAudios.setRecordingUrl(detailsJson.getString("melodyurl"));
                            sharedAudioList.add(sharedAudios);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sharedAudioList;

    }

}
