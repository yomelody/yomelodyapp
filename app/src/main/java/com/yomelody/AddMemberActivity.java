package com.yomelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.yomelody.Fragments.UsersFragment;
import com.yomelody.Models.Users;
import com.yomelody.utils.AppHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.yomelody.utils.Const.ServiceType.ADD_MEMBER;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.FOLLOWER_LIST;
import static com.yomelody.utils.Const.ServiceType.Group_Members;
import static com.yomelody.utils.Const.ServiceType.USER_LIST;

public class AddMemberActivity extends AppCompatActivity {

    private Activity mActivity;
    private ProgressDialog progressDialog;
    private RecyclerView rvfan;
    private AddMemberAdapter addMemberAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private String userId="";
    private ImageView ivBackButton, ivHomeButton;
    private TextView appBarMainText;
    private ImageView searchFanIv;
    private SearchView searchFanFollowing;
    private Button btnCancel;
    private String strSearch = "";
    private String showProfileUserId = "";
    private ArrayList<Users> usersList = new ArrayList<Users>();
    private String chatId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_following);
        mActivity=AddMemberActivity.this;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        rvfan = findViewById(R.id.rvfan);
        ivBackButton = findViewById(R.id.ivBackButton);
        ivHomeButton = findViewById(R.id.ivHomeButton);
        appBarMainText = findViewById(R.id.appBarMainText);
        searchFanIv = findViewById(R.id.searchFanIv);
        searchFanFollowing = findViewById(R.id.searchFanFollowing);
        btnCancel = findViewById(R.id.btnCancel);



        appBarMainText.setText("Users");
        SharedPreferences loginSharedPref = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        if (getIntent() != null) {
            chatId = getIntent().getStringExtra("chat_id");
        }

        addMemberAdapter=new AddMemberAdapter();
        rvfan.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        rvfan.setLayoutManager(linearLayoutManager);
        rvfan.setItemAnimator(new DefaultItemAnimator());
        rvfan.setAdapter(addMemberAdapter);

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchFanIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFanFollowing.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                ((EditText) searchFanFollowing.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setHintTextColor(getResources().getColor(R.color.colorSearch));
                ((EditText) searchFanFollowing.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setTextColor(getResources().getColor(R.color.colorSearch));

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFanFollowing.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            }
        });

        searchFanFollowing.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFanFollowing.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                searchFanFollowing.isSubmitButtonEnabled();
                strSearch = searchFanFollowing.getQuery().toString();
                /*SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                editorSearchString.putString("stringSearch", searchContent);
                editorSearchString.apply();*/
                AppHelper.sop("searchContent=="+strSearch);
                searchFanFollowing.setQuery("", false);
                getUsers();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
                        addMemberAdapter.notifyDataSetChanged();
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

    class AddMemberAdapter extends RecyclerView.Adapter<AddMemberAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView userIv,msgIv;
            TextView nameTv,userNameTv;
            CheckBox followChk;
            LinearLayout userLl,followLl;
            RelativeLayout mainRv;

            public MyViewHolder(View mView) {
                super(mView);
                userIv = mView.findViewById(R.id.userIv);
                msgIv = mView.findViewById(R.id.msgIv);
                nameTv = mView.findViewById(R.id.nameTv);
                userNameTv = mView.findViewById(R.id.userNameTv);
                followChk = mView.findViewById(R.id.followChk);
                userLl = mView.findViewById(R.id.userLl);
                followLl = mView.findViewById(R.id.followLl);
                mainRv = mView.findViewById(R.id.mainRv);
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

            holder.followLl.setVisibility(View.GONE);

            holder.userIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    intent.putExtra("showProfileUserId", usersList.get(position).getId());
                    startActivity(intent);
                }
            });

            /*holder.userLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ProfileActivity.class);
                    intent.putExtra("showProfileUserId", usersList.get(position).getId());
                    startActivity(intent);
                }
            });*/

            holder.mainRv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("Are you sure you want to add "+
                            usersList.get(position).getName() +" to this group?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            addMemberApi(usersList.get(position).getId()+"");
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }
    }

    void addMemberApi(final String memberId) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_MEMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            AppHelper.sop("response=addMemberApi=" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                Toast.makeText(mActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("chat_id", chatId);
                params.put("member_id", memberId);
                params.put("login_id", userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=" + params + "\nURL==" + ADD_MEMBER);
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

}
