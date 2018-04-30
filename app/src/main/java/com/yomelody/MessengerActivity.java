package com.yomelody;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.yomelody.Adapters.MessengerAdapter;
import com.yomelody.Models.Chat;
import com.yomelody.Services.LogoutService;
import com.yomelody.app.Config;
import com.yomelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.yomelody.utils.Const.PUSH_NOTIFICATION;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.TOTAL_COUNT;
import static com.yomelody.utils.Const.ServiceType.USER_CONVERSATION;

/**
 * Created by Shubhansh Jaiswal on 12/29/2016.
 */

public class MessengerActivity extends AppCompatActivity {

    ImageView discover, message, profile, audio_feed, ivBackButton, ivHomeButton, ivNewMessage;
    TextView message_count;
    MessengerAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout rlNoMsg;
    ArrayList<Chat> chatList = new ArrayList<>();
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String USER_ID = "userid";
    String userId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mActivity = MessengerActivity.this;

        ivNewMessage = (ImageView) findViewById(R.id.ivNewMessage);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        profile = (ImageView) findViewById(R.id.profile);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        rlNoMsg = (RelativeLayout) findViewById(R.id.rlNoMsg);
        message_count = (TextView) findViewById(R.id.message_count);

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

        getTotalCount();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    getTotalCount();
                    getChats(userId);
                    rlNoMsg.setVisibility(View.GONE);
                }
            }
        };

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
        adapter = new MessengerAdapter(chatList, mActivity);
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
                Intent intent = new Intent(mActivity, ContactsActivity.class);
                intent.putExtra("Previous", "Messenger");
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ProfileActivity.class);
                startActivity(intent);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, StationActivity.class);
                startActivity(intent);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        /*Intent intent = new Intent(mActivity, HomeActivity.class);
        startActivity(intent);*/
    }

    public void getChats(final String user_Id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CONVERSATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
//                        Toast.makeText(MessengerActivity.this, " Shubz" + response, Toast.LENGTH_LONG).show();

                        chatList.clear();
                        adapter.notifyDataSetChanged();
                        //recyclerView.smoothScrollToPosition(adapter.getItemCount());

                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Chat chat = new Chat();
                                    JSONObject commentJson = jsonArray.getJSONObject(i);
                                    chat.setId(commentJson.getString("id"));
                                    chat.setSenderID(commentJson.getString("senderID"));
                                    chat.setSenderName(commentJson.getString("sender_name"));
                                    chat.setReceiverID(commentJson.getString("receiverID"));
                                    chat.setReceiverName(commentJson.getString("receiver_name"));
                                    chat.setCoverPic(commentJson.getString("coverPick"));
                                    chat.setProfilePic(commentJson.getString("profilePick"));
                                    chat.setMessage(commentJson.getString("message"));
                                    chat.setChatID(commentJson.getString("chatID"));
                                    chat.setChatType(commentJson.getString("chat_type"));
                                    chat.setGroupName(commentJson.getString("group_name"));
                                    chat.setGroupPic(commentJson.getString("group_pick"));
                                    chat.setIsRead(commentJson.getString("isread"));
                                    chat.setSendAt(commentJson.getString("sendat"));
                                    chat.setNewMessages(commentJson.getString("New_message"));
                                    chatList.add(chat);
                                }
                            } else {
                                rlNoMsg.setVisibility(View.VISIBLE);
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
                            Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                            error.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, user_Id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params="+params+"\nURL="+USER_CONVERSATION);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getTotalCount();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        if (userId != null) {
            getChats(userId);
            rlNoMsg.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //startService(new Intent(this, LogoutService.class));
    }

    public void getTotalCount() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TOTAL_COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                String str = jsonObject.getString("newMessage");
                                totalCount = Integer.parseInt(str);
                                if (totalCount > 0) {
                                    message_count.setText(str);
                                    message_count.setVisibility(View.VISIBLE);
                                } else {
                                    message_count.setVisibility(View.GONE);
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
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put("userid", userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult=MessengerActivity=requestCode="+requestCode+"=resultCode="+resultCode+"=data="+data);
    }
}
