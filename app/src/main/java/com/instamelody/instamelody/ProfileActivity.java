package com.instamelody.instamelody;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
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
import com.crashlytics.android.Crashlytics;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Fragments.BioFragment;
import com.instamelody.instamelody.Fragments.ProfileActivityFragment;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserDetails;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.app.Config;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.Const;
import com.instamelody.instamelody.utils.NotificationUtils;
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
import io.fabric.sdk.android.Fabric;

import static com.instamelody.instamelody.Adapters.JoinInstrumentListAdp.count;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.FOLLOWERS;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;
import static com.instamelody.instamelody.utils.Const.ServiceType.TOTAL_COUNT;
import static com.instamelody.instamelody.utils.Const.ServiceType.USERS_BIO;
import static com.instamelody.instamelody.utils.Const.ServiceType.USER_CHAT_ID;

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
    private String limit="limit";

    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    Button btnAudio, btnActivity, btnBio, btnCancel;
    RelativeLayout rlPartProfile, rlFragmentActivity, rlFragmentBio, rlSearch, rlFollow, tab1, rlMessage;
    ImageView ivBackButton, ivHomeButton, ivAudio_feed, ivDiscover, ivMessage, ivProfile, ivSearchProfile, userCover, ivToMelody;
    public static ImageView ivFilterProfile;
    ImageView ivFollow, ivUnfollow;
    CircleImageView userProfileImageInProf;
    TextView tvNameInProf, tvUserNameInProf, tv_records, tv_fans, tv_following, message_count;
    String Name, userName, profilePic, coverPic, followStatus, receiverId, chat_id;
    String userId, showProfileUserId;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    String artistName, Instruments, BPM;
    EditText subEtFilterName, subEtFilterInstruments, subEtFilterBPM;
    public static SearchView search1;
    ProgressDialog progressDialog;
    //    LongOperation myTask = null;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    UserDetails userDetails;
    RecyclerView rv;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    int totalCount = 0;
    LinearLayoutManager linearLayoutManager;
    private Activity mActivity;
    private final int count=10;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private String msgUnsuccess="No record found.";
    public static final int PROFILE_TO_MESSANGER = 105;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mActivity=ProfileActivity.this;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
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
        ivProfile.setEnabled(false);
        userProfileImageInProf = (CircleImageView) findViewById(R.id.userProfileImageInProf);
        userCover = (ImageView) findViewById(R.id.userCover);
        tvNameInProf = (TextView) findViewById(R.id.tvNameInProf);
        tvUserNameInProf = (TextView) findViewById(R.id.tvUserNameInProf);
        tv_records = (TextView) findViewById(R.id.tv_records);
        tv_fans = (TextView) findViewById(R.id.tv_fans);
        tv_following = (TextView) findViewById(R.id.tv_following);
        message_count = (TextView) findViewById(R.id.message_count);

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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    getTotalCount();
                }
            }
        };

        if (getIntent() != null && getIntent().hasExtra("showProfileUserId")) {
            showProfileUserId = getIntent().getStringExtra("showProfileUserId");
            AppHelper.sop("showProfileUserId======" + showProfileUserId);
        } else {
            if (loginSharedPref.getString("userId", null) != null) {
                showProfileUserId = loginSharedPref.getString("userId", null);
            } else if (fbPref.getString("userId", null) != null) {
                showProfileUserId = fbPref.getString("userId", null);
            } else if (twitterPref.getString("userId", null) != null) {
                showProfileUserId = twitterPref.getString("userId", null);
            }
        }

        getChatId(userId, showProfileUserId);

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
        adapter = new RecordingsCardAdapter(ProfileActivity.this, recordingList, recordingsPools);
        if (rv != null) {
            rv.setAdapter(adapter);
        }

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
                search1.setQuery("",false);

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
                ivFilterProfile.setVisibility(View.VISIBLE);
                ivSearchProfile.setVisibility(View.VISIBLE);
                List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (android.support.v4.app.Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                }
                rlPartProfile.setVisibility(View.VISIBLE);

                getFragmentManager().popBackStack();

                if (getFragmentManager().findFragmentById(R.id.activity_profile) != null) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().
                            findFragmentById(R.id.activity_profile)).commit();
                }
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
                ivSearchProfile.setVisibility(View.VISIBLE);
                ivFilterProfile.setVisibility(View.GONE);

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
                ivFilterProfile.setVisibility(View.GONE);
                ivSearchProfile.setVisibility(View.GONE);


                BioFragment bioFragment = new BioFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();

                Bundle bundle = new Bundle();
                bundle.putSerializable("user_detail", userDetails);
                bundle.putString("show_Profile_UserId", showProfileUserId);
                bioFragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.activity_profile, bioFragment);
                fragmentTransaction.commit();
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (RecordingsCardAdapter.mp != null && RecordingsCardAdapter.mp.isPlaying()) {
                        try {
                            RecordingsCardAdapter.mp.reset();
                            RecordingsCardAdapter.mp.release();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        finish();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }


            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("senderId", userId);
                editor.putString("receiverId", receiverId);
                editor.putString("chatId", chat_id);
                editor.putString("receiverName", Name);
                editor.putString("chatType", "single");
                editor.commit();
                Intent i = new Intent(ProfileActivity.this, ChatActivity.class);
                i.putExtra("clickFromProfile", "click");
                startActivityForResult(i, PROFILE_TO_MESSANGER);
                //  startActivity(i);
            }
        });
    }

    public String getUserId() {
        return showProfileUserId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();
        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();*/
        sharePrefClearProfile();
    }

    public void fetchUserBio() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USERS_BIO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=bio=" + response);
                        String records, fans, followers;
                        String str = response;
                        JSONObject jsonObject;
                        JSONArray jsonArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals(SUCCESS)) {
                                jsonArray = jsonObject.getJSONArray(KEY_RESULT);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    userDetails = new UserDetails();
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
                                    followers = userJson.getString("following");
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
                                    receiverId = userJson.getString("id");
                                    chat_id = userJson.getString("chat_id");
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
                                    if (userJson.has("followers")) {
                                        userDetails.setFollowers(userJson.getString("followers"));
                                    }
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
                if (userId != null) {
                    params.put(MY_ID, userId);
                }
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("Param=bio==" + params + "\nURL====" + USERS_BIO);
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
                        AppHelper.sop("Response===" + response);
                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;
                        TabHost.TabSpec spec;
                        final TabHost host = (TabHost) findViewById(R.id.tabHostProfile);
                        host.setup();

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals(SUCCESS)) {
//                                myTask = new LongOperation();
//                                myTask.execute();
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
//                                fetchRecordings();
                                recordingList.clear();
                                recordingsPools.clear();
                                callApi();
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
                AppHelper.sop("Param===" + params + "\nURL====" + GENERE);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordings() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=fetchRecordings="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        tv_records.setText("" + recordingList.size());
                        isLoading=false;
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        isLoading=false;
                        isLastPage=false;
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, showProfileUserId);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("Param===" + params + "\nURL====" + RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
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
                params.put(USER_ID, showProfileUserId);
                params.put(FOLLOWER_ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
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
                rv = new RecyclerView(ProfileActivity.this);
                rv.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(mActivity);
                //rv.setLayoutManager(layoutManager);
                //lm = new LinearLayoutManager(getActivity());
                //lm = (LinearLayoutManager) rv.getLayoutManager();
                rv.setLayoutManager(linearLayoutManager);
                rv.setItemAnimator(new DefaultItemAnimator());
//                rv.addOnScrollListener(recyclerViewOnScrollListener);
                rv.setAdapter(adapter);
                linearLayoutManager = (LinearLayoutManager) rv.getLayoutManager();

                rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                        AppHelper.sop("onScrolled==visibleItemCount"+visibleItemCount+"=totalItemCount="+
//                                totalItemCount+ "=firstVisibleItemPosition="+firstVisibleItemPosition);
                        if (!isLoading && !isLastPage) {
//                            AppHelper.sop("isLoading==isLastPage");

                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                                    firstVisibleItemPosition >= 0 && totalItemCount >= count) {
                                if(AppHelper.checkNetworkConnection(mActivity)){
                                    isLoading=true;
                                    callApi();
                                }
                            }
                        }
                    }
                });

                return rv;
            }
        };
    }

    private void callApi() {
        if (strName == null && strSearch == null) {
            fetchRecordings();
        } else if (strSearch != null) {
            fetchSearchData();
        } else if (strArtist != null) {
            fetchRecordingsFilterArtist();
        } else if (strInstruments != null && strName.equals("# of Instruments")) {
            fetchRecordingsFilterInstruments();
        } else if (strBPM != null && strName.equals("BPM")) {
            fetchRecordingsFilterBPM();
        } else {
            fetchRecordingsFilter();
        }
    }


    public void fetchRecordingsFilter() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=filter="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading=false;
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading=false;
                        isLastPage=false;
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
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=filter="+params+"\nURL="+RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchSearchData() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        SharedPreferences searchPref = this.getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=search="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading=false;
                        if (!progressDialog.isShowing()){
                            progressDialog.show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading=false;
                        isLastPage=false;
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
                        if (!progressDialog.isShowing()){
                            progressDialog.show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, "Myrecording");
                params.put(KEY_SEARCH, strSearch);
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=filter="+params+"\nURL="+RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterArtist() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=artist="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading=false;
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading=false;
                        isLastPage=false;
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
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, artistName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=artist="+params+"\nURL="+RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterInstruments() {
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=instrument="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading=false;
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading=false;
                        isLastPage=false;
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
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, Instruments);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instrument="+params+"\nURL="+RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterBPM() {

        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=instrument="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (recordingList.size()<=0){
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getApplicationContext()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        isLoading=false;

                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading=false;
                        isLastPage=false;
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
                        if (progressDialog!=null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ID, userId);
                params.put(KEY, "Myrecording");
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, BPM);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instrument="+params+"\nURL="+RECORDINGS);
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
        try{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            if (RecordingsCardAdapter.mp != null && RecordingsCardAdapter.mp.isPlaying()) {
                try {
                    RecordingsCardAdapter.mp.reset();
                    RecordingsCardAdapter.mp.reset();
                    RecordingsCardAdapter.mp.release();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }catch (Throwable e){
            e.printStackTrace();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult=requestCode=" + requestCode + "=resultCode=" + resultCode);
        if (requestCode == RecordingsCardAdapter.REQUEST_RECORDING_COMMENT) {
            if (resultCode == RESULT_OK) {
                recordingList.clear();
                recordingsPools.clear();
                callApi();
            }
            else {
                SharedPreferences socialStatusPref = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);;
                if (socialStatusPref.getBoolean(Const.REC_SHARE_STATUS, false)) {
                    SharedPreferences.Editor socialStatusPrefEditor = getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
                    socialStatusPrefEditor.putBoolean(Const.REC_SHARE_STATUS, false);
                    socialStatusPrefEditor.apply();
                    recordingList.clear();
                    recordingsPools.clear();
                    callApi();
                }
            }
        }
        if (requestCode == PROFILE_TO_MESSANGER) {
            fetchUserBio();
        }
    }

    private void getChatId(final String user_id, final String reciever_id) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CHAT_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(context, " Shubz" + response, Toast.LENGTH_LONG).show();

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String chat_Id = jsonObject.getString("chatID");
                            String receiverName = "";
                            if (jsonObject.has("receiverName")){
                                receiverName = jsonObject.getString("receiverName");
                            }


                            if (chat_Id.equals("0")) {
                                chat_Id = "";
                            }

                            SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", chat_Id);
                            editor.putString("receiverName", receiverName);
                            editor.commit();

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
//                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderID", user_id);
                params.put("receiverID", reciever_id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
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

}
