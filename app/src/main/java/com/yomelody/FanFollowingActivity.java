package com.yomelody;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.FOLLOWERS;
import static com.yomelody.utils.Const.ServiceType.FOLLOWER_LIST;
import static com.yomelody.utils.Const.ServiceType.USER_LIST;

public class FanFollowingActivity extends AppCompatActivity {

    private Activity mActivity;
    private ProgressDialog progressDialog;
    private RecyclerView rvfan;
    private FansAdapter fansAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private String userId="";
    private String type="";
    private ArrayList<JSONObject> fansFollowerList = new ArrayList<>();
    private ImageView ivBackButton, ivHomeButton;
    private TextView appBarMainText;
    private ImageView searchFanIv;
    private SearchView searchFanFollowing;
    private Button btnCancel;
    private String strSearch = "";
    private String showProfileUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fan_following);
        mActivity=FanFollowingActivity.this;
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

        if (getIntent()!=null){
            if (getIntent().hasExtra("type")){
                type=getIntent().getStringExtra("type");
                showProfileUserId=getIntent().getStringExtra("showProfileUserId");
                appBarMainText.setText(getIntent().getStringExtra("header"));
            }
        }

        fansAdapter=new FansAdapter();
        rvfan.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        rvfan.setLayoutManager(linearLayoutManager);
        rvfan.setItemAnimator(new DefaultItemAnimator());
        rvfan.setAdapter(fansAdapter);

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
                getFans();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        getFans();
    }

    void getFans() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FOLLOWER_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response=getFans=" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, "No record found.", Toast.LENGTH_SHORT).show();
//                                isLastPage = true;
                            } else {
//                                isLastPage = false;
                                if (flag.equalsIgnoreCase("success")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                                    fansFollowerList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        fansFollowerList.add(jsonArray.getJSONObject(i));
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
                        fansAdapter.notifyDataSetChanged();
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
                params.put("user_id", showProfileUserId);
                params.put("type", type);
                if (!TextUtils.isEmpty(strSearch)){
                    params.put("search", strSearch);
                }
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=getFans=" + params + "\nURL==" + FOLLOWER_LIST);
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

    class FansAdapter extends RecyclerView.Adapter<FansAdapter.MyViewHolder> {

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
            try {
                holder.nameTv.setText(fansFollowerList.get(position).getString("name"));
                holder.userNameTv.setText("@"+fansFollowerList.get(position).getString("username"));
                Picasso.with(mActivity)
                        .load(fansFollowerList.get(position).getString("profilepic"))
                        .placeholder(R.drawable.artist)
                        .error(R.drawable.artist)
                        .into(holder.userIv);

                holder.followChk.setVisibility(View.GONE);
                holder.msgIv.setVisibility(View.GONE);
                holder.userIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mActivity, ProfileActivity.class);
                            intent.putExtra("showProfileUserId",
                                    fansFollowerList.get(position).getString("id"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.userLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mActivity, ProfileActivity.class);
                            intent.putExtra("showProfileUserId",
                                    fansFollowerList.get(position).getString("id"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return fansFollowerList.size();
        }
    }
}
