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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.yomelody.Adapters.ContactsAdapter;
import com.yomelody.Models.Contacts;
import com.yomelody.Models.PhoneBookContact;
import com.yomelody.Parse.ParseContents;
import com.yomelody.Services.LogoutService;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;
import com.yomelody.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yomelody.Adapters.ContactsAdapter.Count;
import static com.yomelody.Adapters.ContactsAdapter.recId;
import static com.yomelody.utils.Const.PUSH_NOTIFICATION;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.BASE_URL;
import static com.yomelody.utils.Const.ServiceType.CONTACT_LIST;
import static com.yomelody.utils.Const.ServiceType.CREATE_GROUP;
import static com.yomelody.utils.Const.ServiceType.TOTAL_COUNT;
import static com.yomelody.utils.Const.ServiceType.USER_CHAT_ID;

/**
 * Created by Shubhansh Jaiswal on 04/05/17.
 */

public class ContactsActivity extends AppCompatActivity {

    //    ImageView discover, message, profile, audio_feed, ivBackButton, ivHomeButton, ivNewMessage;
    ContactsAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<Contacts> contactList = new ArrayList<>();
    public static Button btnCancel, btnOK;
    ImageView discover, message, profile, audio_feed, ivBackButton, ivHomeButton;
    TextView message_count;
    String userId = "";
    public static RelativeLayout rlNoContacts;
    Activity mActivity;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;
    private ImageView phoneContactIv;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mActivity = ContactsActivity.this;
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

//        getContacts();

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOK = (Button) findViewById(R.id.btnOK);

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        profile = (ImageView) findViewById(R.id.profile);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        rlNoContacts = (RelativeLayout) findViewById(R.id.rlNoContacts);
        message_count = (TextView) findViewById(R.id.message_count);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewContacts);
        phoneContactIv = (ImageView) findViewById(R.id.phoneContactIv);
        search = (EditText) findViewById(R.id.search);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ContactsAdapter(getApplicationContext(), contactList);
        recyclerView.setAdapter(adapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
                editor.putString("chatId", "");
                editor.commit();
                String caller = "";
                if (getIntent() != null && getIntent().hasExtra("Previous")) {
                    caller = getIntent().getStringExtra("Previous");
                }
                AppHelper.sop("caller====" + caller);
                Intent intent;
                if (caller.equals("chat")) {
                    intent = new Intent(mActivity, ChatActivity.class);
                    startActivity(intent);
                } else if (caller.equals("station")) {
                    intent = new Intent(mActivity, StationActivity.class);
                    startActivity(intent);
                } else if (caller.equals("profile")) {
                    intent = new Intent(mActivity, ProfileActivity.class);
                    startActivity(intent);
                } else if (caller.equals("discover")) {
                    intent = new Intent(mActivity, DiscoverActivity.class);
                    startActivity(intent);
                } else if (caller.equals("studioActivity")) {
                    /*intent = new Intent(mActivity, StudioActivity.class);
                    startActivity(intent);*/
                } else if (caller.equals("Messenger")) {
                    intent = new Intent(mActivity, MessengerActivity.class);
                    startActivity(intent);
                } else if (caller.equals("JoinActivity")) {
                    intent = new Intent(mActivity, JoinActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!userId.equals("") && Count == 1) {
                    getChatId(userId, recId);
                } else if (!userId.equals("") && Count > 1) {
                    createGroup(recId);
                }

            }
        });

        profile.setOnClickListener(new View.OnClickListener()

        {
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

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
                editor.putString("chatId", "");
                editor.commit();
                Count=0;
                String caller = "";
                if (getIntent() != null && getIntent().hasExtra("Previous")) {
                    caller = getIntent().getStringExtra("Previous");
                }
                AppHelper.sop("caller====" + caller);
                Intent intent;
                if (caller.equals("chat")) {
                    intent = new Intent(mActivity, ChatActivity.class);
                    startActivity(intent);
                } else if (caller.equals("station")) {
                    intent = new Intent(mActivity, StationActivity.class);
                    startActivity(intent);
                } else if (caller.equals("profile")) {
                    intent = new Intent(mActivity, ProfileActivity.class);
                    startActivity(intent);
                } else if (caller.equals("discover")) {
                    intent = new Intent(mActivity, DiscoverActivity.class);
                    startActivity(intent);
                } else if (caller.equals("studioActivity")) {
                    /*intent = new Intent(mActivity, StudioActivity.class);
                    startActivity(intent);*/
                } else if (caller.equals("Messenger")) {
                    intent = new Intent(mActivity, MessengerActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
                editor.putString("chatId", "");
                editor.commit();
                Count=0;
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PUSH_NOTIFICATION)) {

                   /* sender_name = intent.getStringExtra("sender_name");
                    // Toast.makeText(context, "Brod :"+chatId_new + "Old :"+chatId , Toast.LENGTH_SHORT).show();
                    if (chatId_new.equals(chatId)) {
                        if (chatType.equals("single")) {
                            tvUserName.setText(sender_name);
                        }
                        getChatMsgs(chatId_new);
                    }*/

                }
            }
        };

        phoneContactIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity,PhoneBookActivity.class);
                startActivity(intent);
            }
        });
    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<Contacts> filteredList = new ArrayList<>();

                for (int i = 0; i < contactList.size(); i++) {

                    final String text = contactList.get(i).getfName().toLowerCase();
                    if (text.contains(query)) {
                        filteredList.add(contactList.get(i));
                    }
                }

                adapter = new ContactsAdapter(getApplicationContext(), filteredList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();  // data set changed
            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
            editor.putString("receiverId", "");
            editor.putString("receiverName", "");
            editor.putString("receiverImage", "");
            editor.putString("chatId", "");
            editor.commit();
            Count = 0;
            String caller = "";
            if (getIntent() != null && getIntent().hasExtra("Previous")) {
                caller = getIntent().getStringExtra("Previous");
            }
            AppHelper.sop("caller====" + caller);
            Intent intent;
            if (caller.equals("chat")) {
                intent = new Intent(mActivity, ChatActivity.class);
                startActivity(intent);
            } else if (caller.equals("station")) {
                intent = new Intent(mActivity, StationActivity.class);
                startActivity(intent);
            } else if (caller.equals("profile")) {
                intent = new Intent(mActivity, ProfileActivity.class);
                startActivity(intent);
            } else if (caller.equals("discover")) {
                intent = new Intent(mActivity, DiscoverActivity.class);
                startActivity(intent);
            } else if (caller.equals("studioActivity")) {
            /*intent = new Intent(mActivity, StudioActivity.class);
            startActivity(intent);*/
            } else if (caller.equals("Messenger")) {
                intent = new Intent(mActivity, MessengerActivity.class);
                startActivity(intent);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        finish();
    }

    public void getContacts() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CONTACT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=getContacts="+response);
//                        String str = response;
//                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                        try {
                            contactList.clear();
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(adapter.getItemCount());
                            new ParseContents(getApplicationContext()).parseContacts(contactList, response);
                            addTextListener();
                        }catch (Exception ex){
                            ex.printStackTrace();
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
                            errorMsg = "ParseError";
                        } else {
                            errorMsg = "something went wrong";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("myid", userId);
                params.put("key", "passed");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=getContacts="+params+"\nURL="+CONTACT_LIST);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTotalCount();
        getContacts();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
       // startService(new Intent(this, LogoutService.class));
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
    private void getChatId(final String user_id, final String reciever_id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CHAT_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(context, " Shubz" + response, Toast.LENGTH_LONG).show();
                        AppHelper.sop("response=getChatId="+response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String chat_Id = jsonObject.getString("chatID");

                            String receiverName;
                            String group_pick = "";

                            if (jsonObject.has("receiverName")) {
                                receiverName = jsonObject.getString("receiverName");
                            } else {
                                receiverName = "New Group";
                            }

                            if (jsonObject.has("group_pick")) {
                                group_pick = jsonObject.getString("group_pick");
                            }

                            if (chat_Id.equals("0")) {
                                chat_Id = "";
                            }

                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", chat_Id);
                            editor.putString("receiverName", receiverName);
                            editor.putString("groupImage", group_pick);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("commingForm", "ContactsActivity");
                            intent.putExtra("from", "ContactsActivity");
                            startActivity(intent);

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
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderID", user_id);
                params.put("receiverID", reciever_id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=getChatId="+params+"\nURL="+USER_CHAT_ID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    private void createGroup(final String recId) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CREATE_GROUP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(context, " Shubz" + response, Toast.LENGTH_LONG).show();
                        AppHelper.sop("response=createGroup="+response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONObject res = jsonObject.getJSONObject("response");
                            String chat_Id = res.getString("chat_id");

                            String groupName = res.getString("groupName");
                            String group_pick = "";


                            if (res.has("groupPic")) {
                                group_pick = BASE_URL + res.getString("groupPic");
                            }

                            if (chat_Id.equals("0")) {
                                chat_Id = "0";
                            }

                            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", chat_Id);
                            editor.putString("receiverName", groupName);
                            editor.putString("groupImage", group_pick);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.putExtra("from", "ContactsActivity");
                            startActivity(intent);

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
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usersId", recId);
                params.put("senderID", userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=createGroup="+params+"\nURL=="+CREATE_GROUP);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
