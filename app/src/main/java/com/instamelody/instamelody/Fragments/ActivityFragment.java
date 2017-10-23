package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.instamelody.instamelody.Adapters.ActivityCardAdapter;
import com.instamelody.instamelody.Models.ActivityModel;
import com.instamelody.instamelody.R;
import com.instamelody.instamelody.StationActivity;
import com.instamelody.instamelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.instamelody.instamelody.utils.Const.ServiceType.ACTIVITY;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.RMethod.getServerDiffrenceDate;

/**
 * Created by Saurabh Singh on 12/16/2016.
 */

public class ActivityFragment extends Fragment {

    private static RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    String USER_ID = "user_id";
    String KEY_FLAG = "flag";
    String KEY_RESPONSE = "response";
    String KEY_MESSAGE = "message";
    String id = "id", userId = "";
    ProgressDialog progressDialog;
    private ArrayList<ActivityModel> arraylist;
    private Activity mActivity;
    private String msgUnsuccess="No record found.";
    LinearLayoutManager linearLayoutManager;
    private final int count=30;
    private boolean isLoading=false;
    private boolean isLastPage=false;
    private String limit="limit";

    public ActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        mActivity=getActivity();
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewActivity);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        arraylist = new ArrayList<>();
        adapter = new ActivityCardAdapter(arraylist, mActivity);
        recyclerView.setAdapter(adapter);
        String position;
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

        if (!userId.equals("") && userId != null) {
            fetchActivityData(userId);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                                fetchActivityData(userId);
                            }
                        }
                    }
                }
            });

        }
        else {
            Toast.makeText(getActivity().getBaseContext(), "Please login to see user activity", Toast.LENGTH_SHORT).show();
        }

        return view;
    }



    public void fetchActivityData(final String userId) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ACTIVITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=="+response);
                        JSONObject jsonObject;
                        JSONArray jsonArray;

                        try {
                            jsonObject = new JSONObject(response);
                            /*String flag = jsonObject.getString(KEY_FLAG);
                            String msg = jsonObject.getString(KEY_MESSAGE);*/
                            if (arraylist.size()<=0){
                                arraylist.clear();
                            }

                            if (jsonObject.getString(KEY_FLAG).equals("success")) {

                                jsonArray = jsonObject.getJSONArray(KEY_RESPONSE);

                                if (jsonArray.length()>0){
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject c = jsonArray.getJSONObject(i);
                                        arraylist.add(new ActivityModel(
                                                Integer.parseInt(c.getString("id")),
                                                c.getString("activity_name"),
                                                c.getString("topic"),
//                                            DateTime(c.getString("activity_created_time")),
                                                c.getString("ActivityTime"),
                                                c.getString("profile_pick"),
                                                c.getString("created_by_userID"),
                                                c.getString("first_user"),
                                                c.getString("second_user")
                                        ));

                                    }
                                    adapter.notifyDataSetChanged();
                                    isLastPage=false;
                                }
                                else {
                                    Toast.makeText(mActivity, msgUnsuccess, Toast.LENGTH_SHORT).show();
                                    isLastPage=true;
                                }
                            }
                            isLoading=false;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
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
                        try {
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            Log.d("Error", errorMsg);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
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
                params.put(USER_ID, userId);
                params.put(limit, arraylist.size()+"");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=="+params+"\nURL=="+ACTIVITY);
                return params;
            }
        };
        RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
        requestQueue1.add(stringRequest);
    }

    public String DateTime(String send_at) {
        String val = "";
        val = getServerDiffrenceDate(send_at);
        return val;
    }

}
