package com.yomelody.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.yomelody.ChatActivity;
import com.yomelody.Models.Users;
import com.yomelody.ProfileActivity;
import com.yomelody.R;
import com.yomelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.FOLLOWERS;
import static com.yomelody.utils.Const.ServiceType.USER_CHAT_ID;
import static com.yomelody.utils.Const.ServiceType.USER_LIST;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private final int REQUEST_PROFILE=187;
    private Activity mActivity;
    private View view;
    private ProgressDialog progressDialog;
    private RecyclerView rvUser;
    private UsersAdapter mUsersAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private String userId="";
    private ArrayList<Users> usersList = new ArrayList<Users>();
    private String strSearch="";
    private String showProfileUserId="";

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        mActivity = getActivity();
        progressDialog = new ProgressDialog(mActivity);
//        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        rvUser = view.findViewById(R.id.rvUser);

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

        SharedPreferences searchPref = getActivity().getSharedPreferences("SearchPref", MODE_PRIVATE);
        strSearch = searchPref.getString("stringSearch", null);

        mUsersAdapter=new UsersAdapter();
        rvUser.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        rvUser.setLayoutManager(linearLayoutManager);
        rvUser.setItemAnimator(new DefaultItemAnimator());
        rvUser.setAdapter(mUsersAdapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getUsers();
    }

    void getUsers() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response=getUsers=" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, "No record found.", Toast.LENGTH_SHORT).show();
//                                isLastPage = true;
                            } else {
//                                isLastPage = false;
                                if (flag.equalsIgnoreCase("success")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    usersList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Users mUsers = new Users();
                                        JSONObject userJson = jsonArray.getJSONObject(i);
                                        mUsers.setId(userJson.getString("id"));
                                        mUsers.setUserName("@"+userJson.getString("user_name"));
                                        mUsers.setName(userJson.getString("name"));
                                        mUsers.setProfilepic(userJson.getString("profilepic"));
                                        mUsers.setFollowStatus(userJson.getString("follow_status"));

                                        usersList.add(mUsers);
                                    }
                                }

                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }


                        /*if (recordingList.size() <= 0) {
                            recordingList.clear();
                            recordingsPools.clear();
                        }*/
                        mUsersAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
//                        isLoading = false;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        isLoading = false;
//                        isLastPage = false;

                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                params.put("user_id", userId);
                if (!TextUtils.isEmpty(strSearch)){
                    params.put("search", strSearch);
                }
                AppHelper.sop("params=getUsers=" + params + "\nURL==" + USER_LIST);
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

    class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView userIv,msgIv;
            TextView nameTv,userNameTv;
            CheckBox followChk;
            LinearLayout userLl;

            public MyViewHolder(View mView) {
                super(mView);
                userIv = mView.findViewById(R.id.userIv);
                msgIv = mView.findViewById(R.id.msgIv);
                nameTv = mView.findViewById(R.id.nameTv);
                userNameTv = mView.findViewById(R.id.userNameTv);
                followChk = mView.findViewById(R.id.followChk);
                userLl = mView.findViewById(R.id.userLl);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.row_user, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.nameTv.setText(usersList.get(position).getName());
            holder.userNameTv.setText(usersList.get(position).getUserName());
            Picasso.with(mActivity)
                    .load(usersList.get(position).getProfilepic())
                    .placeholder(R.drawable.artist)
                    .error(R.drawable.artist)
                    .into(holder.userIv);

            holder.followChk.setOnCheckedChangeListener(null);

            if (usersList.get(position).getFollowStatus().equalsIgnoreCase("1")){
                holder.followChk.setChecked(true);
                holder.msgIv.setVisibility(View.VISIBLE);
            }else {
                holder.followChk.setChecked(false);
                holder.msgIv.setVisibility(View.GONE);
            }

            holder.followChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    AppHelper.sop("buttonView="+buttonView+"=isChecked="+isChecked);
                    showProfileUserId = usersList.get(position).getId();
                    if (isChecked){
                        holder.msgIv.setVisibility(View.VISIBLE);
                        followApi(position);
                    }
                    else if (!isChecked){
                        holder.msgIv.setVisibility(View.GONE);
                        followApi(position);
                    }

                }
            });

            holder.msgIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProfileUserId = usersList.get(position).getId();
                    getChatId();
                }
            });

            holder.userIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    intent.putExtra("showProfileUserId", usersList.get(position).getId());
                    startActivityForResult(intent,REQUEST_PROFILE);
                }
            });

            holder.userLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    intent.putExtra("showProfileUserId", usersList.get(position).getId());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }
    }

    void followApi(final int position) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, FOLLOWERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=followApi="+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
//                                getUsers();
                                JSONObject json = jsonObject.getJSONObject("response");
                                usersList.get(position).setFollowStatus(""+json.getInt("follow_status"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", showProfileUserId);
                params.put("followerID", userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=followApi="+params+"\nURL="+FOLLOWERS);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);
    }


    void getChatId() {
        progressDialog.show();
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, USER_CHAT_ID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response=getChatId="+response);
                        progressDialog.dismiss();
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            String chat_Id = jsonObject.getString("chatID");
                            String receiverName = "";
                            if (jsonObject.has("receiverName")) {
                                receiverName = jsonObject.getString("receiverName");
                            }


                            if (chat_Id.equals("0")) {
                                chat_Id = "";
                            }

                            SharedPreferences.Editor editor = mActivity.getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            editor.putString("chatId", chat_Id);
                            editor.putString("receiverName", receiverName);

                            editor.putString("senderId", userId);
                            editor.putString("receiverId", showProfileUserId);
                            editor.putString("chatType", "single");
                            editor.commit();
                            Intent i = new Intent(mActivity, ChatActivity.class);
//                            i.putExtra("commingForm", "ProfileActivity");
//                            i.putExtra("clickFromProfile", "click");
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("senderID", userId);
                params.put("receiverID", showProfileUserId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=getChatId="+params+"\nURL="+USER_CHAT_ID);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppHelper.sop("requestCode="+requestCode+"=resultCode="+resultCode+"=data="+data);
        if (requestCode==REQUEST_PROFILE){

        }
    }
}
