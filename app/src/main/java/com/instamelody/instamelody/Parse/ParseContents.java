package com.instamelody.instamelody.Parse;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.Models.AudioModel;
import com.instamelody.instamelody.Models.Chat;
import com.instamelody.instamelody.Models.Comments;
import com.instamelody.instamelody.Models.Contacts;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.RecordingsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shubhansh Jaiswal on 08/02/17.
 */

public class ParseContents {

    Context mContext;

    public ParseContents(Context context) {
        mContext = context;
    }

    ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<Genres> genreList = new ArrayList<>();
    static ArrayList<MelodyInstruments> instrumentsList = new ArrayList<>();
    String KEY_FLAG = "flag";
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

    public ArrayList<MelodyCard> parseMelodyPacks(String response, ArrayList<MelodyCard> melodyList, ArrayList<MelodyInstruments> instrumentList) {

        JSONObject jsonObject;
        JSONArray jsonArray, instrumentArray;
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                for (int i = 0; i < jsonArray.length(); i++) {
                    MelodyCard card = new MelodyCard();
                    JSONObject cardJson = jsonArray.getJSONObject(i);
                    card.setMelodyPackId(cardJson.getString(KEY_MELODY_PACK_ID));
//                    card.setAddedBy(cardJson.getInt(KEY_ADDED_BY));
                    card.setUserName(cardJson.getString(KEY_USERNAME));
                    card.setMelodyName(cardJson.getString(KEY_MELODY_NAME));
                    card.setGenreId(cardJson.getString(KEY_GENRE_ID));
                    card.setGenreName(cardJson.getString(KEY_GENRE_NAME));
                    card.setMelodyLength(cardJson.getString(KEY_DURATION));
                    card.setInstrumentCount(cardJson.getString(KEY_INSTRUMENT_COUNT));
                    card.setMelodyBpm(cardJson.getString(KEY_BPM));
                    card.setPlayCount(cardJson.getInt(KEY_PLAY_COUNTS));
                    card.setLikeCount(cardJson.getInt(KEY_LIKES_COUNTS));
                    card.setShareCount(cardJson.getInt(KEY_SHARE_COUNTS));
                    card.setCommentCount(cardJson.getInt(KEY_COMMENTS_COUNTS));
                    card.setMelodyCover(cardJson.getString(KEY_COVER));
                    card.setUserProfilePic(cardJson.getString(KEY_PROFILE_PIC));
                    card.setMelodyCreated(cardJson.getString(KEY_DATE));

                    instrumentArray = cardJson.getJSONArray(KEY_INSTRUMENTS);
                    for (int j = 0; j < instrumentArray.length(); j++) {
                        MelodyInstruments melodyInstruments = new MelodyInstruments();
                        JSONObject instrumentsJson = instrumentArray.getJSONObject(j);
                        melodyInstruments.setInstrumentId(instrumentsJson.getInt(KEY_INSTRUMENT_ID));
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
            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                JSONObject selectedObj = jsonArray.getJSONObject(Integer.parseInt(mpid));
                jsonArray = selectedObj.getJSONArray(KEY_INSTRUMENTS);
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
                jsonArray = jsonObject.getJSONArray(KEY_RESULT);
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
                for(int i=0; i<resultArray.length(); i++)
                {
                    Message message = new Message();
                    JSONObject chatJson = resultArray.getJSONObject(i);
                    message.setMessage(chatJson.getString("message"));
                    message.setId(chatJson.getString("id"));
                    message.setCreatedAt(chatJson.getString("sendat"));
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
                    comments.setUserProfileImage(commentJson.getString("user_profile_url"));
                    commentList.add(comments);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return commentList;
    }

    public ArrayList<RecordingsModel> parseAudio(String response, ArrayList<RecordingsModel> recordingList) {
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
//                    card.setTvContributeDate(cardJson.getString("30/02/17"));
//                    card.setTvContributeLength(cardJson.getString("recordings"));
                    recordingList.add(card);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recordingList;
    }


}

