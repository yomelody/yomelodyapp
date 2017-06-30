package com.instamelody.instamelody;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Adapters.MessengerAdapter;
import com.instamelody.instamelody.Models.Chat;
import com.instamelody.instamelody.Parse.ParseContents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shubahansh Jaiswal on 12/29/2016.
 */

public class MessengerActivity extends AppCompatActivity {

    ImageView discover, message, profile, audio_feed, ivBackButton, ivHomeButton, ivNewMessage;
    MessengerAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout rlNoMsg;
    ArrayList<Chat> chatList = new ArrayList<>();
    String CONVERSATION_LIST_URL = " http://35.165.96.167//api//UserConversation.php";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String USER_ID = "userid";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        ivNewMessage = (ImageView) findViewById(R.id.ivNewMessage);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        profile = (ImageView) findViewById(R.id.profile);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        rlNoMsg = (RelativeLayout) findViewById(R.id.rlNoMsg);

        rlNoMsg.setVisibility(View.GONE);

        SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        if (userId != null) {
            getChats(userId);

        } else {
            Toast.makeText(getApplicationContext(), "Log in to Chat", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMessenger);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MessengerAdapter(chatList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        ivNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                intent.putExtra("Previous", "Messenger");
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StationActivity.class);
                startActivity(intent);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void getChats(final String user_Id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CONVERSATION_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(MessengerActivity.this, " Shubz" + response, Toast.LENGTH_LONG).show();

                        chatList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());

                        String recName = "";
                        String profilePic = "";
                        String chat_id = "";
                        String rcvrId = "";
                        String sndrId = "";

                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                rlNoMsg.setVisibility(View.GONE);
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Chat chat = new Chat();
                                    JSONObject commentJson = jsonArray.getJSONObject(i);
                                    chat.setId(commentJson.getString("id"));
                                    chat.setSenderID(commentJson.getString("senderID"));
                                    chat.setSenderName(commentJson.getString("sender_name"));
                                    chat.setReceiverID(commentJson.getString("receiverID"));
                                    chat.setReceiverName(commentJson.getString("receiver_name"));
                                    chat.setCoverPick(commentJson.getString("coverPick"));
                                    chat.setUserProfileImage(commentJson.getString("profilePick"));
                                    chat.setMessage(commentJson.getString("message"));
                                    chat.setChatID(commentJson.getString("chatID"));
                                    chat.setIsRead(commentJson.getString("isread"));
                                    chat.setSendAt(commentJson.getString("sendat"));
                                    chatList.add(chat);
                                }
                            } else {
                                rlNoMsg.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
//                        editor.putString("receiverName", recName);
//                        editor.putString("senderId", sndrId);
//                        editor.putString("receiverId", rcvrId);
//                        editor.putString("chatId", chat_id);
//                        editor.putString("receiverImage", profilePic);
//                        editor.commit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, user_Id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getChats(userId);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getChats(userId);
    }

}
