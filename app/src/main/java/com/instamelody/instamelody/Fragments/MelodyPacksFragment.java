package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
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
import com.google.gson.JsonObject;
import com.instamelody.instamelody.Adapters.MelodyCardListAdapter;
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.ChatActivity;
import com.instamelody.instamelody.MelodyActivity;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.MelodyCard;
import com.instamelody.instamelody.Models.MelodyInstruments;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Models.UserMelodyCard;
import com.instamelody.instamelody.Models.UserMelodyPlay;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.SignInActivity;
import com.instamelody.instamelody.StudioActivity;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.MELODY;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MelodyPacksFragment extends Fragment {


    RecyclerView.Adapter adapter;
    private ArrayList<MelodyCard> melodyList = new ArrayList<>();
    ArrayList<MelodyInstruments> instrumentList = new ArrayList<>();
    String KEY_GENRE_NAME = "name";
    String KEY_FLAG = "flag";
    String KEY_GENRE_ID = "id";
    String KEY_RESPONSE = "response";//JSONArray
    String users_id = "users_id";
    String KEY = "key";
    String GENRE = "genere";
    String genreString = "1";
    String limit = "limit";

    String USER_ID = "users_id";
    String USERS_ID = "users_id";
    private String KEY_SEARCH = "search";
    private String USER_NAME = "username";
    private String COUNT = "count";

    String KEY_MSG = "msg";
    String packName;
    String packId = "0";
    String strName, strSearch, strArtist, strInstruments, strBPM;
    String userId;

    private String ID = "id";
    private String STATION = "station";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String SAVE_MELODY = "save_melody";
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    TabHost host = null;
    ProgressDialog progressDialog;
    LinearLayoutManager linearLayoutManager;
    int post = 0;
    Activity mActivity;
    RecyclerView rv;
    private String msgUnsuccess = "No record found.";
    private final int count = 7;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        editor = getActivity().getSharedPreferences("FileType", MODE_PRIVATE).edit();
        SharedPreferences filterPref = getActivity().getSharedPreferences("FilterPref", MODE_PRIVATE);
        strName = filterPref.getString("stringFilter", null);
        SharedPreferences searchPref = getActivity().getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);
        SharedPreferences filterPrefArtist = getActivity().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE);
        strArtist = filterPrefArtist.getString("stringFilterArtist", null);
        SharedPreferences FilterInstruments = getActivity().getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE);
        strInstruments = FilterInstruments.getString("stringFilterInstruments", null);
        SharedPreferences FilterBPM = getActivity().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE);
        strBPM = FilterBPM.getString("stringFilterBPM", null);

        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
            //MelodyUser=userId;
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
            //MelodyUser=userId;
        }
        fetchGenreNames();
        adapter = new MelodyCardListAdapter(melodyList, getActivity());
        if (rv != null) {
            rv.setAdapter(adapter);
        }
        getDataApi();
        //   new LongOperation().execute();


        String joinRecordingId;
        SharedPreferences fromJoin = getApplicationContext().getSharedPreferences("clickPositionJoin", MODE_PRIVATE);
        joinRecordingId = fromJoin.getString("instrumentsPos", null);
        if (StudioActivity.joinRecordingId != null || joinRecordingId != null) {
            try {
                SharedPreferences.Editor FilterPref1 = getApplicationContext().getSharedPreferences("FromHomeToMelody", MODE_PRIVATE).edit();
                FilterPref1.clear();
                FilterPref1.apply();
                StudioActivity.melodyPackId = null;
                //  Toast.makeText(getActivity(), "" + StudioActivity.joinRecordingId, Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }


    }

    void getDataApi() {
        if (strName == null && strSearch == null) {
            fetchMelodyPacks();
        } else if (strSearch != null) {
            fetchMelodySearchData();
        } else if (strArtist != null) {
            fetchMelodyFilterArtist();
        } else if (strInstruments != null && strName.equals("# of Instruments")) {
            fetchMelodyFilterInstruments();
        } else if (strBPM != null && strName.equals("BPM")) {
            fetchMelodyFilterBPM();
        } else {
            fetchMelodyFilter();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_melody_packs, container, false);
        mActivity = getActivity();
        return view;
    }

    public void fetchGenreNames() {

        progressDialog.show();
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            AppHelper.sop("response==" + response);
                            JSONObject jsonObject, genreJson;
                            JSONArray jsonArray;
                            String titleString;
                            TabHost.TabSpec spec;
                            try {
                                host = (TabHost) getActivity().findViewById(R.id.tabHostMelodyPacks);
                                host.setup();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }


                            try {
                                jsonObject = new JSONObject(response);
                                if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                    jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Genres genres = new Genres();
                                        genreJson = jsonArray.getJSONObject(i);
                                        titleString = genreJson.getString(KEY_GENRE_NAME);
                                        genres.setName(titleString);
                                        genres.setId(genreJson.getString(KEY_GENRE_ID));
                                        genresArrayList.add(genres);
                                        try {
                                            spec = host.newTabSpec(titleString);
                                            spec.setIndicator(titleString);
                                            spec.setContent(createTabContent());
                                            host.addTab(spec);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                    @Override
                                    public void onTabChanged(String arg0) {
                                        packName = arg0;
                                        int currentTab = host.getCurrentTab();
                                        AppHelper.sop("Tab=change=" + arg0 + "=currentTab=" + currentTab);
                                        if (currentTab == 0) {
                                            packId = "0";
                                        } else {
                                            packId = (genresArrayList.get(currentTab)).getId();
                                        }
                                        melodyList.clear();
                                        instrumentList.clear();
                                        editor.putString("File_Tyoe","admin_melody");
                                        editor.commit();
                                        fetchMelodyPacks();
                                        if (currentTab == 6) {
                                            if (TextUtils.isEmpty(userId)) {
                                                Toast.makeText(mActivity, "Log in to see your melody.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(mActivity, SignInActivity.class);
                                                startActivity(intent);
                                            } else {
                                                melodyList.clear();
                                                instrumentList.clear();

                                                editor.putString("File_Tyoe","user_melody");
                                                editor.commit();
                                                fetchMelodyPacks();
                                            }
                                        } else {
                                            melodyList.clear();
                                            instrumentList.clear();
                                            editor.putString("File_Tyoe","admin_melody");
                                            editor.commit();
                                            fetchMelodyPacks();
                                        }
                                    }
                                });
                            } catch (NullPointerException e) {
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
//                            errorMsg = "AuthFailureError";
                            } else if (error instanceof ServerError) {
                                errorMsg = "We are facing problem in connecting to server";
                            } else if (error instanceof NetworkError) {
                                errorMsg = "We are facing problem in connecting to network";
                            } else if (error instanceof ParseError) {
//                            errorMsg = "ParseError";
                            }
                            try {
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                                Log.d("Error", errorMsg);
                            } catch (Throwable throwable) {
                                Log.d("Error", throwable.toString());
                            }
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(SAVE_MELODY, "saverecording");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params==" + params + "\nURL==" + GENERE);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            int socketTimeout = 30000; // 30 seconds. You can change it
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public void fetchMelodyPacks() {
        progressDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=fetchMelodyPacks==" + response);
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("unsuccess")) {
                                String str = jsonObject.getString(KEY_MSG);
//                                if (str.equals("No pack found")) {
                                str = "Sorry, no " + packName + " melody available.";
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
//                                }
                            } else {
                                isLastPage = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        try {
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                        } catch (Throwable throwable) {
                            Log.d("Fetch Melody Packs Error", throwable.toString());
                        }
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (packId.equals("7") && userId != null) {
                    params.put(USER_ID, userId);
                    params.put(FILE_TYPE, "user_melody");
                    params.put(limit, melodyList.size() + "");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else if (userId != null) {
                    params.put(users_id, userId);
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(limit, melodyList.size() + "");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else {
                    params.put(GENRE, packId);
                    params.put(FILE_TYPE, "admin_melody");
                    params.put(limit, melodyList.size() + "");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                }
                try {
                    SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                    String userId = loginSharedPref.getString("userId", null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                if (userId != null) {
////                    params.put(USER_ID, userId);
//                }
                AppHelper.sop("params=fetchMelodyPacks==" + params + "\nURL==" + MELODY);
                return params;
            }


        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilter() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response==" + response);
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData1", response);
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("unsuccess")) {

                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;

                            } else {
                                isLastPage = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(limit, melodyList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\nURL==" + MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchMelodySearchData() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=search=" + response);
                        String successMsg = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(successMsg);
                            String flag = jsonObject.getString("flag");

                            if (flag.equals("unsuccess")) {
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        Toast.makeText(getActivity(), "" + response, Toast.LENGTH_SHORT).show();
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }

                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(FILE_TYPE, "admin_melody");
                params.put(KEY_SEARCH, strSearch);
                params.put(limit, melodyList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=search=" + params + "\nURL==" + MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterArtist() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        AppHelper.sop("response=artist=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }

                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, strArtist);
                params.put(FILTER, "extrafilter");
                params.put(limit, melodyList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=artist=" + params + "\nURL==" + MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterInstruments() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=instru=" + response);
                        String rs = response.toString();
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();

                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }
                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, strInstruments);
                params.put(FILTER, "extrafilter");
                params.put(limit, melodyList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instru=" + params + "\nURL==" + MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchMelodyFilterBPM() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MELODY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String rs = response.toString();
                        AppHelper.sop("response=BPM=" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(rs);
                            String flag = jsonObject.getString("flag");
//                            Toast.makeText(getActivity(), "" + flag, Toast.LENGTH_SHORT).show();
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(getApplicationContext(), ""+response, Toast.LENGTH_SHORT).show();
                        if (melodyList.size() <= 0) {
                            melodyList.clear();
                            instrumentList.clear();
                        }

                        new ParseContents(getActivity()).parseMelodyPacks(response, melodyList, instrumentList);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        isLoading = false;
                        isLastPage = false;
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
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put(ID, userId);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "admin_melody");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, strBPM);
                params.put(FILTER, "extrafilter");
                params.put(limit, melodyList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=BPM=" + params + "\nURL==" + MELODY);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                rv = new RecyclerView(getActivity());
                rv.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(getActivity());
//                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
//                rv.setLayoutManager(lm);
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
                                if (AppHelper.checkNetworkConnection(mActivity)) {
                                    isLoading = true;
                                    getDataApi();
                                }
                            }
                        }
                    }
                });

                return rv;
            }
        };
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter!=null){
            ((MelodyCardListAdapter)adapter).killMediaPlayer();
        }
    }*/

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            ((MelodyCardListAdapter)adapter).killMediaPlayer();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult==called=" + "requestCode==" + requestCode + "=resultCode=" + resultCode + "=data=" + data);
        if (MelodyCardListAdapter.REQUEST_MELODY_COMMENT == requestCode) {
            if (resultCode == mActivity.RESULT_OK) {
                melodyList.clear();
                instrumentList.clear();
                getDataApi();
                AppHelper.sop("onActivityResult==called=" + "resultCode==" + resultCode);
            }

            else {
                SharedPreferences socialStatusPref = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);
                if (socialStatusPref.getBoolean(Const.REC_SHARE_STATUS, false)) {
                    SharedPreferences.Editor socialStatusPrefEditor = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
                    socialStatusPrefEditor.putBoolean(Const.REC_SHARE_STATUS, false);
                    socialStatusPrefEditor.apply();
                    melodyList.clear();
                    instrumentList.clear();
                    getDataApi();
                }
            }

        }
    }
}