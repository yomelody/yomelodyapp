package com.instamelody.instamelody.Fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.ChatAdapter;
import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.ChatActivity.CHAT_ID;
import static com.instamelody.instamelody.ChatActivity.CHAT_ID_;
import static com.instamelody.instamelody.ChatActivity.FILE;
import static com.instamelody.instamelody.ChatActivity.FILE_TYPE;
import static com.instamelody.instamelody.ChatActivity.IS_READ;
import static com.instamelody.instamelody.ChatActivity.RECEIVER_ID;
import static com.instamelody.instamelody.ChatActivity.SENDER_ID;
import static com.instamelody.instamelody.ChatActivity.TITLE;
import static com.instamelody.instamelody.ChatActivity.appbar;
import static com.instamelody.instamelody.ChatActivity.cAdapter;
import static com.instamelody.instamelody.ChatActivity.chatId;
import static com.instamelody.instamelody.ChatActivity.chatList;
import static com.instamelody.instamelody.ChatActivity.chatType;
import static com.instamelody.instamelody.ChatActivity.flSeekbar;
import static com.instamelody.instamelody.ChatActivity.fotter;
import static com.instamelody.instamelody.ChatActivity.group;
import static com.instamelody.instamelody.ChatActivity.groupList;
import static com.instamelody.instamelody.ChatActivity.ivRecieverProfilePic;
import static com.instamelody.instamelody.ChatActivity.receiverId;
import static com.instamelody.instamelody.ChatActivity.receiverImage;
import static com.instamelody.instamelody.ChatActivity.receiverName;
import static com.instamelody.instamelody.ChatActivity.recyclerViewChat;
import static com.instamelody.instamelody.ChatActivity.rlChatPlayer;
import static com.instamelody.instamelody.ChatActivity.rlNoMsg;
import static com.instamelody.instamelody.ChatActivity.rlTxtContent;
import static com.instamelody.instamelody.ChatActivity.sendImageBitmap;
import static com.instamelody.instamelody.ChatActivity.sendImageName;
import static com.instamelody.instamelody.ChatActivity.senderId;
import static com.instamelody.instamelody.ChatActivity.tvRecieverName;
import static com.instamelody.instamelody.ChatActivity.tvUserName;
import static com.instamelody.instamelody.ChatActivity.userId;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.CHAT;
import static com.instamelody.instamelody.utils.Const.ServiceType.MESSAGE_LIST;
import static com.instamelody.instamelody.utils.Const.ServiceType.READ_STATUS;


public class viewImageFragment extends Fragment {


    ImageView viewImage, backButton, sendButton;
    Activity mactivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mactivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //  mactivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_view_image, container, false);
        viewImage = (ImageView) view.findViewById(R.id.viewImage);
        backButton = (ImageView) view.findViewById(R.id.backButton);
        sendButton = (ImageView) view.findViewById(R.id.sendButton);
        viewImage.setImageBitmap(sendImageBitmap);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImage(userId, sendImageName, sendImageBitmap);

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appbar.setVisibility(View.VISIBLE);
                fotter.setVisibility(View.VISIBLE);
                getFragmentManager().beginTransaction()
                        .remove(viewImageFragment.this).commit();
            }
        });

        return view;
    }

    private void sendImage(final String user_Id, final String imageName, final Bitmap bit) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, CHAT,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String str = new String(response.data);
                        AppHelper.sop("Chat.php Response for image :- " + str);
                        getFragmentManager().beginTransaction()
                                .remove(viewImageFragment.this).commit();
                        appbar.setVisibility(View.VISIBLE);
                        fotter.setVisibility(View.VISIBLE);
                        //    ((ChatActivity)mactivity).recreate();

//                            Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject json = new JSONObject(str);
                            JSONObject jsonMsg = json.getJSONObject("usermsg");
                            String chat_id = jsonMsg.getString("chat_id");
                            getChatMsgs(chat_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMsg = "";
                if (error instanceof TimeoutError) {
                    errorMsg = "Internet connection timed out";
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "There is no connection";
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "AuthFailureError";
                } else if (error instanceof ServerError) {
                    errorMsg = "We are facing problem in connecting to server";
                } else if (error instanceof NetworkError) {
                    errorMsg = "We are facing problem in connecting to network";
                } else if (error instanceof ParseError) {
                    errorMsg = "Parse error";
                } else if (error == null) {

                }

                getFragmentManager().beginTransaction()
                        .remove(viewImageFragment.this).commit();
                appbar.setVisibility(View.VISIBLE);
                fotter.setVisibility(View.VISIBLE);
                if (!errorMsg.equals("")) {
                    Toast.makeText(mactivity, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (chatType.equals("group")) {
                    if (senderId.equals(user_Id)) {
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    } else {
                        group = senderId + "," + receiverId;
                        groupList = new ArrayList<>(Arrays.asList(group.split(",")));
                        groupList.remove(groupList.indexOf(user_Id));
                        String s = groupList.toString().replaceAll(", ", ",");
                        receiverId = s.substring(1, s.length() - 1).trim();
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    }
                } else {
                    if (receiverId.equals(user_Id)) {
                        params.put(RECEIVER_ID, senderId);
                    } else {
                        params.put(RECEIVER_ID, receiverId);
                    }
                }

                params.put(FILE_TYPE, "image");
                params.put(SENDER_ID, user_Id);
                params.put(CHAT_ID, chatId);
                params.put(TITLE, "message");
                //  params.put(MESSAGE, message);
                params.put(IS_READ, "0");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(FILE, new DataPart(imageName, AppHelper.getFileDataFromDrawable(mactivity, bit), "image/jpeg"));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mactivity);

        int socketTimeout = 60000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        multipartRequest.setRetryPolicy(policy);

        requestQueue.add(multipartRequest);
        //     VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }

    public void getChatMsgs(final String chat_Id) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MESSAGE_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            if (ChatAdapter.sharedAudioList.size() > 0) {
                                ChatAdapter.sharedAudioList.clear();
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        if (rlNoMsg.getVisibility() == View.VISIBLE && rlTxtContent.getVisibility() == View.VISIBLE) {
                            rlNoMsg.setVisibility(View.GONE);
                            rlTxtContent.setVisibility(View.GONE);
                        }

                        chatList.clear();
//                        audioDetailsList.clear();
//                        sharedAudioList.clear();
                        String usrId = userId;
                        if (rlChatPlayer.getVisibility() == View.VISIBLE) {
                            try {
                                rlChatPlayer.setVisibility(View.GONE);
                                flSeekbar.setVisibility(View.GONE);
                                if (ChatAdapter.mp != null) {
                                    ChatAdapter.mp.stop();
                                    ChatAdapter.mp.release();
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }

                        cAdapter.notifyDataSetChanged();
                        JSONObject jsonObject;
                        JSONArray resultArray, audiosDetailsArray, sharedAudiosArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                resultArray = result.getJSONArray("message LIst");
                                if (resultArray.length() > 0) {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        Message message = new Message();
                                        JSONObject chatJson = resultArray.getJSONObject(i);
                                        message.setId(chatJson.getString("id"));
                                        message.setSenderId(chatJson.getString("senderID"));
                                        message.setProfilePic(chatJson.getString("sender_pic"));
                                        message.setMessage(chatJson.getString("message"));
                                        message.setFileType(chatJson.getString("file_type"));
                                        message.setFile(chatJson.getString("file_url"));
                                        message.setFileId(chatJson.getString("file_ID"));
                                        message.setIsRead(chatJson.getString("isread"));
                                        message.setCreatedAt(chatJson.getString("sendat"));
                                        message.setRecCount((chatJson.getString("Rec_count")));
                                        if (!chatJson.get("Audioshared").equals(null) && !chatJson.get("Audioshared").equals("")) {
                                            message.setAudioDetails(chatJson.getJSONArray("Audioshared"));
                                        }
                                        if (chatJson.getString("isread").equals("0") && (!chatJson.getString("senderID").equals(usrId))) {
                                            readStatus(chatJson.getString("id"), chatJson.getString("chatID"));
                                        }
                                        chatList.add(i, message);
                                    }
                                    recyclerViewChat.smoothScrollToPosition(chatList.size() - 1);
                                    rlNoMsg.setVisibility(View.GONE);
                                    rlTxtContent.setVisibility(View.GONE);
                                } else {
                                    tvRecieverName.setText(" " + receiverName);
                                    Picasso.with(ivRecieverProfilePic.getContext()).load(receiverImage).into(ivRecieverProfilePic);
                                    rlNoMsg.setVisibility(View.VISIBLE);
                                    rlTxtContent.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "Parse error";
                        } else if (error == null) {

                        }

                        if (!errorMsg.equals("")) {
                            Toast.makeText(mactivity, errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CHAT_ID_, chat_Id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mactivity);
        requestQueue.add(stringRequest);
    }

    public void readStatus(final String msgIds, final String chatIds) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, READ_STATUS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String str = response;
//                Toast.makeText(ChatActivity.this, str + "readStatus", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                String errorMsg = "";
                if (error instanceof TimeoutError) {
                    errorMsg = "Internet connection timed out";
                } else if (error instanceof NoConnectionError) {
                    errorMsg = "There is no connection";
                } else if (error instanceof AuthFailureError) {
                    errorMsg = "AuthFailureError";
                } else if (error instanceof ServerError) {
                    errorMsg = "We are facing problem in connecting to server";
                } else if (error instanceof NetworkError) {
                    errorMsg = "We are facing problem in connecting to network";
                } else if (error instanceof ParseError) {
                    errorMsg = "Parse error";
                } else if (error == null) {

                }

                if (!errorMsg.equals("")) {
                    Toast.makeText(mactivity, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                try {
                    params.put("messageID", msgIds);
                    params.put("chatID", chatIds);
                    params.put("user_id", userId);
                    if (chatType.equals("group")) {
                        params.put("chat_type", "group");
                    } else {
                        params.put("chat_type", "single");
                    }
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mactivity);
        requestQueue.add(stringRequest);
    }

}
