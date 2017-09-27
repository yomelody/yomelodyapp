package com.instamelody.instamelody;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.instamelody.instamelody.Adapters.ContactsAdapter;
import com.instamelody.instamelody.Models.Contacts;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.utils.AppHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.CONTACT_LIST;

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
    String userId = "";
    public static RelativeLayout rlNoContacts;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        mActivity=ContactsActivity.this;
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

        getContacts();

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOK = (Button) findViewById(R.id.btnOK);

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        profile = (ImageView) findViewById(R.id.profile);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        rlNoContacts = (RelativeLayout) findViewById(R.id.rlNoContacts);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewContacts);
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
                if(getIntent()!=null && getIntent().hasExtra("Previous")){
                    caller = getIntent().getStringExtra("Previous");
                }
                AppHelper.sop("caller===="+caller);
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
                }else if (caller.equals("studioActivity")) {
                    /*intent = new Intent(mActivity, StudioActivity.class);
                    startActivity(intent);*/
                } else if (caller.equals("Messenger")){
                    intent = new Intent(mActivity, MessengerActivity.class);
                    startActivity(intent);
                }

                finish();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("from", "ContactsActivity");
                startActivity(intent);
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
                String caller = "";
                if(getIntent()!=null && getIntent().hasExtra("Previous")){
                    caller = getIntent().getStringExtra("Previous");
                }
                AppHelper.sop("caller===="+caller);
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
                }else if (caller.equals("studioActivity")) {
                    /*intent = new Intent(mActivity, StudioActivity.class);
                    startActivity(intent);*/
                } else if (caller.equals("Messenger")){
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

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
        editor.putString("receiverId", "");
        editor.putString("receiverName", "");
        editor.putString("receiverImage", "");
        editor.putString("chatId", "");
        editor.commit();
        String caller = "";
        if(getIntent()!=null && getIntent().hasExtra("Previous")){
            caller = getIntent().getStringExtra("Previous");
        }
        AppHelper.sop("caller===="+caller);
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
        }else if (caller.equals("studioActivity")) {
            /*intent = new Intent(mActivity, StudioActivity.class);
            startActivity(intent);*/
        } else if (caller.equals("Messenger")){
            intent = new Intent(mActivity, MessengerActivity.class);
            startActivity(intent);
        }

        finish();
    }

    public void getContacts() {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CONTACT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        String str = response;
//                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();

                        contactList.clear();
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount());
                        new ParseContents(getApplicationContext()).parseContacts(contactList, response);
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
