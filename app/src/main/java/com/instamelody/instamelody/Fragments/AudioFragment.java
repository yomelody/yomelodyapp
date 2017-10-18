package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.instamelody.instamelody.Adapters.RecordingsCardAdapter;
import com.instamelody.instamelody.Models.AudioModel;
import com.instamelody.instamelody.Models.Genres;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsModelMore;
import com.instamelody.instamelody.Models.RecordingsPool;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StationActivity;
import com.instamelody.instamelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.GENERE;
import static com.instamelody.instamelody.utils.Const.ServiceType.RECORDINGS;

/**
 * Created by Saurabh Singh on 4/18/2017.
 */

public class AudioFragment extends Fragment {

    ArrayList<AudioModel> audioList = new ArrayList<>();
    ArrayList<RecordingsModel> recordingList = new ArrayList<RecordingsModel>();
    ArrayList<RecordingsModelMore> recordingListMore = new ArrayList<RecordingsModelMore>();
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

    String recordingId, addedBy, recordingTopic, userName, dateAdded, likeCount, playCount, commentCount, shareCount, profileUrl, coverUrl, genre, recordings;
    private String limit="limit";
    String KEY_GENRE_NAME = "name";
    String KEY_GENRE_ID = "id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";//JSONArray
    String genreString = "1";


    RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    private static ArrayList<RecordingsModel> data;
    ProgressDialog pDialog;
    String userId;
    String userIdNormal, userIdFb, userIdTwitter;
    int statusNormal, statusFb, statusTwitter;
    ProgressDialog progressDialog;
    // LongOperation myTask = null;
    String strName, strSearch, strArtist, strInstruments, strBPM;
    TabHost host = null;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    LinearLayoutManager mLayoutManager;
    public static RecyclerView rv;
    int counter = 0;
    RecyclerView.LayoutManager lm;
    int post = 0;
    private String msgUnsuccess="No record found.";
    Activity mActivity;
    private final int count=10;
    private boolean isLoading=false;
    private boolean isLastPage=false;


    public AudioFragment() {

    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_audio, container, false);
        mActivity = getActivity();
        StationActivity.ivFilter.setVisibility(View.VISIBLE);
        rv = (RecyclerView) view.findViewById(R.id.recyclerViewAudio);

        /*//setRetainInstance(true);
        //final  LinearLayoutManager linearLayoutManager = ((LinearLayoutManager)rv.getLayoutManager());

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(view.getContext());
        rv.setLayoutManager(mLayoutManager);

        rv.setLayoutManager(mLayoutManager);
        //adapter = new RecordingsCardAdapter();
        rv.setAdapter(adapter);
*/

        /*rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //int visibleItemCount        = lm.getChildCount();
                //int totalItemCount          = lm.getItemCount();
                int firstVisibleItemPosition= rv.getVerticalScrollbarPosition();
                *//*if (((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition() == LAST_POSITION) {
                    // code here
                }*//*


                //int findFirstCompletelyVisibleItemPosition=rv.(((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                //int findLastVisibleItemPosition=mLayoutManager.findLastVisibleItemPosition();
                //int findLastCompletelyVisibleItemPosition=mLayoutManager.findLastCompletelyVisibleItemPosition();

                //Toast.makeText(getActivity(), "findFirstVisibleItemPosition position>>> " + String.valueOf(visibleItemCount), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "findFirstCompletelyVisibleItemPosition position>>> " + String.valueOf(totalItemCount), Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "findLastVisibleItemPosition position>>> " + String.valueOf(firstVisibleItemPosition), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "findLastCompletelyVisibleItemPosition position>>> " + String.valueOf(findLastCompletelyVisibleItemPosition), Toast.LENGTH_SHORT).show();
                counter = counter + 1;
                if (dy > 0) {
                    if (counter >= 20) {

                        //Toast.makeText(getActivity(), "Hi comment>>> " + String.valueOf(counter), Toast.LENGTH_SHORT).show();

                    }
                } //else {
                //int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                //if (pastVisibleItems == 0) {
                //Toast.makeText(getContext(), "Top most item", Toast.LENGTH_SHORT).show();
                //}
                //}
            }
        });*/


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        // fetchGenreNames();
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
        adapter = new RecordingsCardAdapter(mActivity, recordingList, recordingsPools);
        fetchGenreNames();
//        callApi();

    }

    private void callApi() {
        if (rv != null) {
            rv.setAdapter(adapter);
        }

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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                        AppHelper.sop("response=fetchRecordings="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                    params.put(limit, recordingList.size()+"");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);

                } else {
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put(limit, recordingList.size()+"");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                }
                AppHelper.sop("params=fetchRecordings="+params+"\nURL=="+RECORDINGS);
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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=filter="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=filter="+params+"\nURL=="+RECORDINGS);
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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=search="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(KEY_SEARCH, strSearch);
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=search="+params+"\nURL=="+RECORDINGS);
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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=artist="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(USER_NAME, strArtist);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=artist="+params+"\nURL=="+RECORDINGS);
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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=instrument="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, "Instruments");
                params.put(COUNT, strInstruments);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=instrument="+params+"\nURL=="+RECORDINGS);
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
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(getActivity(), msgUnsuccess, Toast.LENGTH_SHORT).show();
                                isLastPage=true;
                            }
                            else {
                                isLastPage=false;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AppHelper.sop("response=BPM="+response);
                        if (recordingList.size()<=0){
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
                        isLoading=false;
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
                params.put(ID, userId);
                params.put(KEY, STATION);
                params.put(GENRE, genreString);
                params.put(FILE_TYPE, "user_recording");
                params.put(FILTER_TYPE, strName);
                params.put(COUNT, strBPM);
                params.put(FILTER, "extrafilter");
                params.put(limit, recordingList.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=BPM="+params+"\nURL=="+RECORDINGS);
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
                //rv = new RecyclerView(getActivity());

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
                                isLoading=true;
                                if(AppHelper.checkNetworkConnection(mActivity)){
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

    /*private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            try {
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                post = linearLayoutManager.findLastVisibleItemPosition();
                //Toast.makeText(getActivity(), "111111   >>>" + String.valueOf(post), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), String.valueOf(recordingList.size()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "post "+String.valueOf(post+1), Toast.LENGTH_SHORT).show();
                if (post + 1 == recordingList.size() && recordingList.size() >= 10) {
                    // Toast.makeText(getActivity(), "post ", Toast.LENGTH_SHORT).show();
                    new FetchActivityDetails().execute(String.valueOf(recordingList.size() + 10));

                    //adapter.notifyDataSetChanged();
                    //linearLayoutManager.scrollToPosition(post+1);

                    //rv.setAdapter(adapter);
                    //adapter.notifyItemInserted(recordingList.size()-1);
                    //Toast.makeText(getActivity(), "list "+String.valueOf(recordingList.size()), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex ){
                ex.printStackTrace();
            }


        }
    };*/

    /*private class FetchActivityDetails extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                running = true;

                progressDialog = ProgressDialog.show(getActivity(), "Processing...", "Please Wait...");
                progressDialog.setCancelable(false);
            } catch (Throwable e) {
                e.printStackTrace();
            }


        }

        boolean running;
        ProgressDialog progressDialog;

        protected String doInBackground(String... params) {

            try {

                final int Pos = Integer.parseInt(params[0]);
                fetchRecordingsMore(Pos);
                *//*int i = 3;
                while(running){*//*
                try {
                    Thread.sleep(7000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    *//*if(i-- == 0){
                        running = false;
                    }*//*

                //}
                return null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //progressDialog.setMessage(String.valueOf(values[0]));
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            //Toast.makeText(getActivity(), "1111", Toast.LENGTH_SHORT).show();
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            //Toast.makeText(getActivity(), "2222", Toast.LENGTH_SHORT).show();


        }
    }*/

    /*public void fetchRecordingsMore(final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, RECORDINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                        Toast.makeText(getActivity(), ""+response, Toast.LENGTH_SHORT).show();

                        Log.d("ReturnData", response);

                        recordingList.clear();
                        //recordingsPools.clear();
                        new ParseContents(getActivity()).parseAudio(response, recordingList, recordingsPools);

                        try {
                            //recordingList.addAll(recordingListMore);
                            //rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            ClearSharedPref();
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
               *//* params.put(KEY, STATION);
                params.put(GENRE, genreString);*//*
                if (userId != null) {
                    params.put(ID, userId);
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put("limit", String.valueOf(position));
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                } else {
                    params.put(KEY, STATION);
                    params.put(GENRE, genreString);
                    params.put("limit", String.valueOf(position));
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                }
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


    }*/

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
            }

        }
    }

}