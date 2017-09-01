package com.instamelody.instamelody;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
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
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.BioFragment;
import com.instamelody.instamelody.Fragments.ProfileActivityFragment;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.Parse.ParseContents;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.FOLLOWERS;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;
import static com.instamelody.instamelody.utils.Const.ServiceType.USERS_BIO;

/**
 * Created by Saurabh Singh on 01/09/2017
 */

public class ProfileActivity extends AppCompatActivity {

    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String KEY_RESULT = "result";
    String genreString = "1";
    String USER_ID = "user_id";
    String MY_ID = "my_id";
    String FOLLOWER_ID = "followerID";
    private String ID = "id";
    private String KEY = "key";
    String PASSED = "passed";
    String SUCCESS = "success";
    private String GENRE = "genere";
    private String STATION = "station";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String KEY_SEARCH = "search";
    private String USER_NAME = "username";
    private String COUNT = "count";

    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    Button btnAudio, btnActivity, btnBio, btnCancel;
    RelativeLayout rlPartProfile, rlFragmentActivity, rlFragmentBio, rlSearch, rlFollow, tab1, rlMessage;
    ImageView ivBackButton, ivHomeButton, ivAudio_feed, ivDiscover, ivMessage, ivProfile, ivSearchProfile, userCover, ivToMelody, ivFilterProfile;
    ImageView ivFollow, ivUnfollow;
    CircleImageView userProfileImageInProf;
    TextView tvNameInProf, tvUserNameInProf, tv_records, tv_fans, tv_following;
    String Name, userName, profilePic, coverPic, followStatus;
    String userId, showProfileUserId;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    String artistName, Instruments, BPM;
    EditText subEtFilterName, subEtFilterInstruments, subEtFilterBPM;
    SearchView search1;
    ProgressDialog progressDialog;
    LongOperation myTask = null;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharePrefClearProfile();

        SharedPreferences filterPref = this.getSharedPreferences("FilterPref", MODE_PRIVATE);
        strName = filterPref.getString("stringFilter", null);
        SharedPreferences filterPrefArtist = this.getSharedPreferences("FilterPrefArtist", MODE_PRIVATE);
        strArtist = filterPrefArtist.getString("stringFilterArtist", null);
        SharedPreferences FilterInstruments = this.getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE);
        strInstruments = FilterInstruments.getString("stringFilterInstruments", null);
        SharedPreferences FilterBPM = this.getSharedPreferences("FilterPrefBPM", MODE_PRIVATE);
        strBPM = FilterBPM.getString("stringFilterBPM", null);

        search1 = (SearchView) findViewById(R.id.searchOnProf);
        btnAudio = (Button) findViewById(R.id.btnAudio);
        btnActivity = (Button) findViewById(R.id.btnActivity);
        btnBio = (Button) findViewById(R.id.btnBio);
        rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        ivSearchProfile = (ImageView) findViewById(R.id.ivSearchProfile);
        ivFilterProfile = (ImageView) findViewById(R.id.ivFilterProfile);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ivToMelody = (ImageView) findViewById(R.id.ivToMelody);
        rlPartProfile = (RelativeLayout) findViewById(R.id.rlPartProfile);
        rlFragmentActivity = (RelativeLayout) findViewById(R.id.rlFragmentActivity);
        rlFragmentBio = (RelativeLayout) findViewById(R.id.rlFragmentBio);
        rlMessage = (RelativeLayout) findViewById(R.id.rlMessage);
        rlFollow = (RelativeLayout) findViewById(R.id.rlFollow);
        ivUnfollow = (ImageView) findViewById(R.id.ivUnfollow);
        ivFollow = (ImageView) findViewById(R.id.ivFollow);
        tab1 = (RelativeLayout) findViewById(R.id.tab1);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewProfile);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivAudio_feed = (ImageView) findViewById(R.id.audio_feed);
        ivDiscover = (ImageView) findViewById(R.id.discover);
        ivMessage = (ImageView) findViewById(R.id.message);
        ivProfile = (ImageView) findViewById(R.id.profile);
        userProfileImageInProf = (CircleImageView) findViewById(R.id.userProfileImageInProf);
        userCover = (ImageView) findViewById(R.id.userCover);
        tvNameInProf = (TextView) findViewById(R.id.tvNameInProf);
        tvUserNameInProf = (TextView) findViewById(R.id.tvUserNameInProf);
        tv_records = (TextView) findViewById(R.id.tv_records);
        tv_fans = (TextView) findViewById(R.id.tv_fans);
        tv_following = (TextView) findViewById(R.id.tv_following);

        Bundle bundle = getIntent().getExtras();
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

        if (bundle != null) {
            showProfileUserId = bundle.getString("showProfileUserId");
        } else {
            if (loginSharedPref.getString("userId", null) != null) {
                showProfileUserId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                showProfileUserId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                showProfileUserId = twitterPref.getString("userId", null);
            }
        }


        if (showProfileUserId != null) {
            fetchUserBio();
            fetchGenreNames();
            fetchRecordings();

        } else {
            Toast.makeText(getApplicationContext(), "Log in to view your Profile", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
        }
        adapter = new RecordingsCardAdapter(this, recordingList, recordingsPools);

        ivToMelody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, MelodyActivity.class);
                startActivity(i);
            }
        });

        ivSearchProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                searchMenuItem.setVisible(position == 0);
                rlSearch.setVisibility(View.INVISIBLE);
                search1.setVisibility(View.VISIBLE);
                ((EditText) search1.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setHintTextColor(getResources().getColor(R.color.colorSearch));
                btnCancel.setVisibility(View.VISIBLE);
            }
        });

        ivFilterProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Filter Audio");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ProfileActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Latest");
                arrayAdapter.add("Trending");
                arrayAdapter.add("Favorites");
                arrayAdapter.add("Artist");
                arrayAdapter.add("# of Instruments");
                arrayAdapter.add("BPM");

                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strName = arrayAdapter.getItem(which);
                        if (strName.equals("Artist")) {
                            openDialog();
                        } else if (strName.equals("# of Instruments")) {
                            openDialogInstruments();
                        } else if (strName.equals("BPM")) {
                            openDialogBPM();
                        } else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(ProfileActivity.this);
                            builderInner.setMessage(strName);
                            SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                            editorFilterString.putString("stringFilter", strName);
                            editorFilterString.apply();
                            SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                            editorSearchString.clear();
                            editorSearchString.apply();
                            sharePrefClearProfile();
                            builderInner.setTitle("Your Selected Item is");
                            fetchRecordingsFilter();
                        }
                    }
                });
                builderSingle.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSearch.setVisibility(View.VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);

            }
        });

        search1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rlSearch.setVisibility(View.VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                search1.isSubmitButtonEnabled();
                String searchContent = search1.getQuery().toString();
                SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                editorSearchString.putString("stringSearch", searchContent);
                editorSearchString.apply();
                fetchSearchData();
                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.clear();
                editorFilterString.apply();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnBio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (android.support.v4.app.Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
                rlPartProfile.setVisibility(View.VISIBLE);
                getFragmentManager().popBackStack();


                /*AudioFragment af = new AudioFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_profile, af).commit();*//*
                rlPartProfile.setVisibility(View.VISIBLE);
                getFragmentManager().popBackStack();*/
            }
        });

        btnActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnActivity.setBackgroundColor(Color.parseColor("#FFFFFF"));
                rlPartProfile.setVisibility(View.GONE);

                ProfileActivityFragment pactf = new ProfileActivityFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.activity_profile, pactf);
                fragmentTransaction.commit();
            }
        });

        btnBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnActivity.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnAudio.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnBio.setBackgroundColor(Color.parseColor("#FFFFFF"));
                rlPartProfile.setVisibility(View.GONE);

                BioFragment bioFragment = new BioFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_profile, bioFragment);
                fragmentTransaction.commit();
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RecordingsCardAdapter.mp != null && RecordingsCardAdapter.mp.isPlaying()) {
                    try {
                        RecordingsCardAdapter.mp.stop();
                        RecordingsCardAdapter.mp.reset();
                        RecordingsCardAdapter.mp.release();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                finish();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        ivDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        ivAudio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, StationActivity.class);
                startActivity(i);
            }
        });

        rlFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String followCount;
                followCount = tv_following.getText().toString().trim();
                int count = Integer.parseInt(followCount);

                if (ivFollow.getVisibility() == View.VISIBLE) {
                    ivFollow.setVisibility(View.GONE);
                    ivUnfollow.setVisibility(View.VISIBLE);
                    rlMessage.setVisibility(View.VISIBLE);
                    count = count + 1;
                    tv_following.setText(String.valueOf(count));
                    Follow();
                } else {
                    ivUnfollow.setVisibility(View.GONE);
                    ivFollow.setVisibility(View.VISIBLE);
                    rlMessage.setVisibility(View.GONE);
                    count = count - 1;
                    tv_following.setText(String.valueOf(count));
                    Follow();
                }
            }
        });

        rlMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();
    }

    public void fetchUserBio() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, USERS_BIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String records, fans, followers;
                        String str = response;
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals(SUCCESS)) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESULT);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    UserDetails userDetails = new UserDetails();
                                    JSONObject userJson = jsonArray.getJSONObject(i);
                                    Name = userJson.getString("fname") + " " + userJson.getString("lname");
                                    if (!Name.equals("")) {
                                        tvNameInProf.setText(Name);
                                    }
                                    userName = userJson.getString("username");
                                    if (!userName.equals("")) {
                                        tvUserNameInProf.setText("@" + userName);
                                    }
                                    followStatus = userJson.getString("follow_status");
                                    if (showProfileUserId.equals(userId)) {
                                        if (rlFollow.getVisibility() == View.VISIBLE) {
                                            rlFollow.setVisibility(View.GONE);
                                        }
                                        if (rlMessage.getVisibility() == View.VISIBLE) {
                                            rlMessage.setVisibility(View.GONE);
                                        }
                                    } else {
//                                        if (rlMessage.getVisibility() == View.GONE) {
//                                            rlMessage.setVisibility(View.VISIBLE);
//                                        }
                                        if (rlFollow.getVisibility() == View.GONE) {
                                            rlFollow.setVisibility(View.VISIBLE);
                                            if (!followStatus.equals("")) {
                                                if (followStatus.equals("0")) {
                                                    ivFollow.setVisibility(View.VISIBLE);
                                                    rlMessage.setVisibility(View.GONE);
                                                } else if (followStatus.equals("1")) {
                                                    ivUnfollow.setVisibility(View.VISIBLE);
                                                    rlMessage.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                    records = userJson.getString("records");
                                    if (!records.equals("")) {
                                        tv_records.setText(records);
                                    } else {
                                        tv_records.setText(0);
                                    }
                                    fans = userJson.getString("fans");
                                    if (!fans.equals("")) {
                                        tv_fans.setText(fans);
                                    } else {
                                        tv_fans.setText(0);
                                    }
                                    followers = userJson.getString("followers");
                                    if (!followers.equals("")) {
                                        tv_following.setText(followers);
                                    } else {
                                        tv_following.setText(0);
                                    }

                                    profilePic = userJson.getString("profilepic");
                                    userProfileImageInProf.setVisibility(View.VISIBLE);
                                    Picasso.with(ProfileActivity.this).load(profilePic).into(userProfileImageInProf);
                                    coverPic = userJson.getString("coverpic");
                                    Picasso.with(ProfileActivity.this).load(coverPic).into(userCover);

                                    userDetails.setId(userJson.getString("id"));
                                    userDetails.setUsername(userJson.getString("username"));
                                    userDetails.setFname(userJson.getString("fname"));
                                    userDetails.setLname(userJson.getString("lname"));
                                    userDetails.setEmail(userJson.getString("email"));
                                    userDetails.setMobile(userJson.getString("mobile"));
                                    userDetails.setDob(userJson.getString("dob"));
                                    userDetails.setLogintype(userJson.getString("logintype"));
                                    userDetails.setLogin_with(userJson.getString("login_with"));
                                    userDetails.setProfilepic(userJson.getString("profilepic"));
                                    userDetails.setCoverpic(userJson.getString("coverpic"));
                                    userDetails.setRegisterdate(userJson.getString("registerdate"));
                                    userDetails.setFollowers(userJson.getString("followers"));
                                    userDetails.setFans(userJson.getString("fans"));
                                    userDetails.setRecords(userJson.getString("records"));
                                    userDetails.setDevicetoken(userJson.getString("devicetoken"));
                                    userDetails.setDiscrisption(userJson.getString("discrisption"));

//                                    if (userJson.getString("id") == userId) {
//                                        SharedPreferences prefUserDetails = getApplicationContext().getSharedPreferences("prefUserDetails", MODE_PRIVATE);
//                                        SharedPreferences.Editor prefsEditor = prefUserDetails.edit();
//                                        Gson gson = new Gson();
//                                        String json = gson.toJson(userDetails); // myObject - instance of MyObject
//                                        prefsEditor.putString("UserDetails", json);
//                                        prefsEditor.commit();

//                                        to use it use this
//                                        Gson gson = new Gson();
//                                        String json = mPrefs.getString("MyObject", "");
//                                        MyObject obj = gson.fromJson(json, MyObject.class);
//                                    }
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
                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY, PASSED);
                params.put(USER_ID, showProfileUserId);
                params.put(MY_ID, userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchGenreNames() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;
                        TabHost.TabSpec spec;
                        final TabHost host = (TabHost) findViewById(R.id.tabHostProfile);
                        host.setup();

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals(SUCCESS)) {
                                myTask = new LongOperation();
                                myTask.execute();
                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Genres genres = new Genres();
                                    genreJson = jsonArray.getJSONObject(i);
                                    titleString = genreJson.getString(KEY_GENRE_NAME);
                                    genres.setName(titleString);
                                    genres.setId(genreJson.getString(KEY_GENRE_ID));
                                    genresArrayList.add(genres);
                                    spec = host.newTabSpec(titleString);
                                    spec.setIndicator(titleString);
                                    spec.setContent(createTabContent());
                                    host.addTab(spec);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (host.getCurrentTab() == 0) {
//                            Toast.makeText(getActivity(), "All " + host.getCurrentTab(), Toast.LENGTH_SHORT).show();
                        }

                        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                            @Override
                            public void onTabChanged(String arg0) {
                                genreString = arg0;
                                int currentTab = host.getCurrentTab();
                                if (currentTab == 0) {
                                    genreString = "";
                                } else {
                                    genreString = genresArrayList.get(currentTab).getId();
                                }
//                                genreString = String.valueOf(currentTab).trim();
                                fetchRecordings();
//                                Toast.makeText(getActivity(), "beta: " + genreString, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordings() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();
                        Log.d("ReturnData", response);
                        recordingList.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void Follow() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FOLLOWERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String str = response;
//                        Toast.makeText(getApplicationContext(), "" + str, Toast.LENGTH_SHORT).show();
//                        Log.d("ReturnData", response);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals(SUCCESS)) {
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
                            errorMsg = "ParseError";
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(KEY, PASSED);
                params.put(USER_ID, showProfileUserId);
                params.put(FOLLOWER_ID, userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                RecyclerView rv = new RecyclerView(ProfileActivity.this);
                rv.setHasFixedSize(true);
                RecyclerView.LayoutManager lm = new LinearLayoutManager(ProfileActivity.this);
                rv.setLayoutManager(lm);
                rv.setItemAnimator(new DefaultItemAnimator());
                rv.setAdapter(adapter);
                return rv;
            }
        };
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {

            try {
                //Getting data from server
                String filename = "myfile";
                String outputString = "Hello world!";
                URL aurl = new URL(RECORDINGS);
                URLConnection connection = aurl.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();
                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(aurl.openStream(), 8192);
                try {
                    FileOutputStream outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(outputString.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileInputStream inputStream = openFileInput(filename);
                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }
                    r.close();
                    inputStream.close();
                    Log.d("File", "File contents: " + total);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
        }
    }

    public void fetchRecordingsFilter() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
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
                        }
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchSearchData() {

        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successMsg = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(successMsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        Log.d("ReturnDataS", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
//                params.put(KEY, STATION);
                params.put(KEY_SEARCH, strSearch);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterArtist() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, artistName);
                params.put(FILTER, "extrafilter");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterInstruments() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, Instruments);
                params.put(FILTER, "extrafilter");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterBPM() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData4", response);
                        recordingList.clear();
                        recordingsPools.clear();
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String errorMsg = "";
                        if (error instanceof TimeoutError) {
                            errorMsg = "Internet connection timed out";
                        } else if (error instanceof NoConnectionError) {
//                            errorMsg = "There is no connection";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "We are facing problem in connecting to server";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "We are facing problem in connecting to network";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
//                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, BPM);
                params.put(FILTER, "extrafilter");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterName = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(this);
        builder2.setTitle("ArtistName");
        builder2.setMessage("Choose Artist Name to Search Artist");
        builder2.setView(subView);

        TextView title = new TextView(this);
        title.setText("ArtistName");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder2.setCustomTitle(title);

        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                artistName = subEtFilterName.getText().toString().trim();
                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", artistName);
                editorFilterArtist.apply();
                fetchRecordingsFilterArtist();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);

            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);
            }
        });

        builder2.show();
    }

    private void openDialogInstruments() {
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterInstruments = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("Number of Instruments");
        builder3.setMessage("Give Instruments Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("Instruments");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                Instruments = subEtFilterInstruments.getText().toString().trim();
                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                editorFilterInstruments.apply();
                fetchRecordingsFilterInstruments();


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    private void openDialogBPM() {
        LayoutInflater inflater = LayoutInflater.from(ProfileActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterBPM = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("BPM");
        builder3.setMessage("Give BPM Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("BPM");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                BPM = subEtFilterBPM.getText().toString().trim();
                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.putString("stringFilterBPM", BPM);
                editorFilterBPM.apply();

                fetchRecordingsFilterBPM();


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    public void sharePrefClearProfile() {
        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();
        SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
        editorFilterArtist.clear();
        editorFilterArtist.apply();
        SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
        editorFilterInstruments.clear();
        editorFilterInstruments.apply();
        SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
        editorFilterBPM.clear();
        editorFilterBPM.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (RecordingsCardAdapter.mp != null && RecordingsCardAdapter.mp.isPlaying()) {
            try {
                RecordingsCardAdapter.mp.stop();
                RecordingsCardAdapter.mp.reset();
                RecordingsCardAdapter.mp.release();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

    }
}
