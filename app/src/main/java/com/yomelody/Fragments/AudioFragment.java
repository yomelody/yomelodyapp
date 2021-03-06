package com.yomelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.yomelody.Adapters.RecordingsCardAdapter;
import com.yomelody.Models.Genres;
import com.yomelody.Models.RecordingsModel;
import com.yomelody.Models.RecordingsPool;
import com.yomelody.Parse.ParseContents;
import com.yomelody.R;
import com.yomelody.StationActivity;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.GENERE;
import static com.yomelody.utils.Const.ServiceType.STATION_RECORDINGS;

/**
 * Created by Saurabh Singh on 4/18/2017.
 */

public class AudioFragment extends Fragment {

    ArrayList<RecordingsModel> recordingList = new ArrayList<RecordingsModel>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    ArrayList<Genres> genresArrayList = new ArrayList<>();
    private String ID = "id";
    private String KEY = "key";
    private String STATION = "station";
    private String GENRE = "genere";
    private String FILE_TYPE = "file_type";
    private String FILTER_TYPE = "filter_type";
    private String FILTER = "filter";
    private String KEY_SEARCH = "search";
    private String USER_NAME = "username";
    private String COUNT = "count";
    LinearLayoutManager linearLayoutManager;

    private String limit = "limit";
    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";


    RecyclerView.Adapter adapter;

    String userId;

    ProgressDialog progressDialog;
    // LongOperation myTask = null;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    TabHost host = null;
    public static RecyclerView rv;
    RecyclerView.LayoutManager lm;
    private String msgUnsuccess = "No record found.";
    Activity mActivity;
    private final int count = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    public AudioFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_audio, container, false);
        mActivity = getActivity();
        StationActivity.ivFilter.setVisibility(View.VISIBLE);
//        rv = (RecyclerView) view.findViewById(R.id.recyclerViewAudio);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

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
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }
        adapter = new RecordingsCardAdapter(mActivity, recordingList, recordingsPools, AudioFragment.this);
        if (rv != null) {
            rv.setAdapter(adapter);
        }

        fetchGenreNames();
//        callApi();

    }

    public void callApi() {

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

    public void clearArrayList(){
        recordingList.clear();
        recordingsPools.clear();
    }

    public void fetchGenreNames() {

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GENERE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonObject, genreJson;
                        JSONArray jsonArray;
                        String titleString;
                        TabHost.TabSpec spec;
                        try {
                            host = (TabHost) getActivity().findViewById(R.id.tabHostAudio);
                            host.setup();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                //   myTask = new LongOperation();
                                //   myTask.execute();
                                callApi();
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
                                    try {
                                        SharedPreferences.Editor editorGenre = getActivity().getSharedPreferences("prefGenreName", MODE_PRIVATE).edit();
                                        editorGenre.putString("GenreName", titleString);
                                        editorGenre.apply();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                ClearSharedPref();
                            }
                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }

                        try {
                            host.setCurrentTab(0);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

//                        try {
//                            if (host.getCurrentTab() == 0) {
////                            Toast.makeText(getActivity(), "All " + host.getCurrentTab(), Toast.LENGTH_SHORT).show();
//                                if (strName == null && strSearch == null) {
//                                    fetchRecordings();
//                                } else if (strSearch != null) {
//                                    fetchSearchData();
//                                } else if (strArtist != null) {
//                                    fetchRecordingsFilterArtist();
//                                } else if (strInstruments != null && strName.equals("# of Instruments")) {
//                                    fetchRecordingsFilterInstruments();
//                                } else if (strBPM != null && strName.equals("BPM")) {
//                                    fetchRecordingsFilterBPM();
//                                } else {
//                                    fetchRecordingsFilter();
//                                }
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        }

                        try {
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

//                                Toast.makeText(getActivity(), "beta: " + genreString, Toast.LENGTH_SHORT).show();
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
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void fetchRecordings() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=fetchRecordings=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);

                        try {
                            adapter.notifyDataSetChanged();
                            ClearSharedPref();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
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
               /* params.put(KEY, STATION);
                params.put(GENRE, genreString);*/
                if (userId != null) {
                    params.put(ID, userId);
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put(limit, recordingList.size() + "");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else {
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put(limit, recordingList.size() + "");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                }
                AppHelper.sop("params=fetchRecordings=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);


    }


    public void fetchRecordingsFilter() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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

                        AppHelper.sop("response=filter=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }

                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        ClearSharedPref();
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
                if (userId != null) {
                    params.put(ID, userId);
                } else {
                    params.put(ID, "");
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=filter=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchSearchData() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage = true;
                            } else {
                                isLastPage = false;
                            }
                            strSearch="";
                            ClearSharedPref();

                        } catch (JSONException e) {
                            ClearSharedPref();
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=search=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }

                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        ClearSharedPref();
                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ClearSharedPref();
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
                if (userId != null) {
                    params.put(ID, userId);
                } else {
                    params.put(ID, "");
                }
                params.put(KEY, STATION);
                params.put(KEY_SEARCH, strSearch);
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=search=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterArtist() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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

                        AppHelper.sop("response=artist=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        ClearSharedPref();
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
                if (userId != null) {
                    params.put(ID, userId);
                } else {
                    params.put(ID, "");
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, strArtist);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=artist=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterInstruments() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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

                        AppHelper.sop("response=instrument=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }

                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        ClearSharedPref();
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
                if (userId != null) {
                    params.put(ID, userId);
                } else {
                    params.put(ID, "");
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, strInstruments);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instrument=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    public void fetchRecordingsFilterBPM() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, STATION_RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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

                        AppHelper.sop("response=BPM=" + response);
                        if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }

                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);
                        adapter.notifyDataSetChanged();
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        ClearSharedPref();
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
                if (userId != null) {
                    params.put(ID, userId);
                } else {
                    params.put(ID, "");
                }
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, strBPM);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size() + "");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=BPM=" + params + "\nURL==" + STATION_RECORDINGS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);

        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().findViewById(R.id.activity_station);
    }

    private TabHost.TabContentFactory createTabContent() {
        return new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                rv = new RecyclerView(getActivity());

                rv.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(getActivity());
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

                                if (AppHelper.checkNetworkConnection(mActivity)) {
                                    isLoading = true;
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

    void ClearSharedPref() {
        try {

            SharedPreferences.Editor FilterPref = mActivity.getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
            FilterPref.clear();
            FilterPref.apply();
            SharedPreferences.Editor SearchPref = mActivity.getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
            SearchPref.clear();
            SearchPref.apply();
            SharedPreferences.Editor FilterPrefArtist = mActivity.getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
            FilterPrefArtist.clear();
            FilterPrefArtist.apply();
            SharedPreferences.Editor FilterPrefInstruments = mActivity.getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
            FilterPrefInstruments.clear();
            FilterPrefInstruments.apply();
            SharedPreferences.Editor FilterPrefBPM = mActivity.getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
            FilterPrefBPM.clear();
            FilterPrefBPM.apply();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("onActivityResult==called=" + "requestCode==" + requestCode + "=resultCode=" + resultCode + "=data=" + data);
        if (RecordingsCardAdapter.REQUEST_RECORDING_COMMENT == requestCode) {
            if (resultCode == mActivity.RESULT_OK) {
                recordingList.clear();
                recordingsPools.clear();
                callApi();
                AppHelper.sop("onActivityResult==called=" + "resultCode==" + resultCode);
            } else {
                SharedPreferences socialStatusPref = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE);
                if (socialStatusPref.getBoolean(Const.REC_SHARE_STATUS, false)) {
                    SharedPreferences.Editor socialStatusPrefEditor = mActivity.getSharedPreferences(Const.SOCIAL_STATUS_PREF, MODE_PRIVATE).edit();
                    socialStatusPrefEditor.putBoolean(Const.REC_SHARE_STATUS, false);
                    socialStatusPrefEditor.apply();
                    recordingList.clear();
                    recordingsPools.clear();
                    callApi();
                }
            }

        }
        if (RecordingsCardAdapter.REQUEST_PROFILE_COMMENT == requestCode) {
            if (resultCode == mActivity.RESULT_OK) {
                recordingList.clear();
                recordingsPools.clear();
                callApi();
                AppHelper.sop("onActivityResult==called=" + "resultCode==" + resultCode);
            }
        }
        if (RecordingsCardAdapter.REQUEST_JOIN_COMMENT == requestCode) {
            if (resultCode == mActivity.RESULT_OK) {
                recordingList.clear();
                recordingsPools.clear();
                callApi();
                AppHelper.sop("onActivityResult==called=" + "resultCode==" + resultCode);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter!=null){
            ((RecordingsCardAdapter)adapter).releaseMediaPlayer();
        }
    }

}