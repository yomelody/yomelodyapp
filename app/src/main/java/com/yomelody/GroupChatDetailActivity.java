package com.yomelody;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.yomelody.Fragments.viewImageFragment;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.ImageCompressor;
import com.yomelody.utils.VolleyMultipartRequest;
import com.yomelody.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.yomelody.utils.Const.ServiceType.ADD_MEMBER;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.FOLLOWER_LIST;
import static com.yomelody.utils.Const.ServiceType.Group_Members;
import static com.yomelody.utils.Const.ServiceType.REMOVE_MEMBER;
import static com.yomelody.utils.Const.ServiceType.UPDATE_GROUP;

public class GroupChatDetailActivity extends AppCompatActivity {

    private Activity mActivity;
    private ProgressDialog progressDialog;
    private RecyclerView groupRv;
    private GroupAdapter groupAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private ImageView ivBackButton, ivHomeButton;
    private String userId="";
    private String chatId="";
    private String groupName="";
    private ImageView groupImg;
    private TextView memberCountTv,doneTv, editTv, updateTv;
    private EditText tvUserName;
    private ArrayList<JSONObject> groupList = new ArrayList<>();
    private LinearLayout addGroupLl;
    private final int TAKE_CAMERA_PHOTO = 101;
    private final int PICK_GALLERY_IMAGE = 102;
    private final int PERMISSION_READ_STORAGE = 201;
    private final int PERMISSION_CAMERA = 202;
    private final int REQUEST_TAKE_PHOTO = 1;
    private final int REQUEST_ADD_MEMBER = 2;
    int updateGroupFlag = 0;
    private Uri imageToUploadUri;
    private String sendGroupImageName = "";
    private Bitmap groupImageBitmap;
    private LinearLayout groupLl;
    private String adminId="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_detail);
        mActivity=GroupChatDetailActivity.this;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        groupRv = findViewById(R.id.groupRv);
        ivBackButton = findViewById(R.id.ivBackButton);
        ivHomeButton = findViewById(R.id.ivHomeButton);
        groupImg = findViewById(R.id.groupImg);
        tvUserName = findViewById(R.id.tvUserName);
        memberCountTv = findViewById(R.id.memberCountTv);
        addGroupLl = findViewById(R.id.addGroupLl);
        editTv = findViewById(R.id.editTv);
        doneTv = findViewById(R.id.doneTv);
        updateTv = findViewById(R.id.updateTv);
        groupLl = findViewById(R.id.groupLl);

        groupImg.setEnabled(false);
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
            Picasso.with(mActivity)
                    .load(getIntent().getStringExtra("group_img"))
                    .placeholder(getResources().getDrawable(R.drawable.loading))
                    .error(getResources().getDrawable(R.drawable.no_image))
                    .into(groupImg);

            chatId = getIntent().getStringExtra("chat_id");
            groupName = getIntent().getStringExtra("group_name");
            tvUserName.setText(getIntent().getStringExtra("group_name")+"@");
            getGroupMembers();
        }

        groupAdapter=new GroupAdapter();
        groupRv.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mActivity);
        groupRv.setLayoutManager(linearLayoutManager);
        groupRv.setItemAnimator(new DefaultItemAnimator());
        groupRv.setAdapter(groupAdapter);

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

        addGroupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity,AddMemberActivity.class);
                intent.putExtra("chat_id",chatId);
                startActivityForResult(intent,REQUEST_ADD_MEMBER);
            }
        });

        doneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        groupImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                    alertDialog.setTitle("Choose Option...");
                    alertDialog.setMessage("Select your options: Camera or Gallery");
                    alertDialog.setIcon(R.drawable.profile_bold);
                    alertDialog.setPositiveButton("select file", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showFileChooser();
                        }
                    });
                    alertDialog.setNegativeButton("Open Camera", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //uploadImage();
                            try {
                                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(photoCaptureIntent, REQUEST_TAKE_PHOTO);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    alertDialog.show();
                    setPermissions();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTv.setVisibility(View.VISIBLE);
                editTv.setVisibility(View.GONE);
                tvUserName.setEnabled(true);
                groupImg.setEnabled(true);
                groupLl.setAlpha(1);

            }
        });

        updateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTv.setVisibility(View.GONE);
                editTv.setVisibility(View.VISIBLE);
                tvUserName.setEnabled(false);
                groupImg.setEnabled(false);
                groupLl.setAlpha(0.5f);
                groupName = tvUserName.getText().toString().trim().replace("@", "");
                updateGroup(chatId, groupName);
            }
        });

    }

    /*public boolean checkPermissions() {
        int flag = 1;
        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            flag = 0;
        } else if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            flag = 0;
        } else if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            flag = 0;
        }

        if (flag == 0) {
            return false;
        } else {
            return true;
        }
    }*/

    public void setPermissions() {
        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            }
        } else if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermissions();
                }
                break;
            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermissions();
                }
                break;
        }
    }*/

    void getGroupMembers() {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Group_Members,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            AppHelper.sop("response=getGroupMembers=" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                Toast.makeText(mActivity, "No record found.", Toast.LENGTH_SHORT).show();
//                                isLastPage = true;
                            } else {
//                                isLastPage = false;
                                if (flag.equalsIgnoreCase("success")) {

                                    JSONObject json=jsonObject.getJSONObject("response");
                                    if (json.getString("admin_id").equalsIgnoreCase(userId)){
                                        addGroupLl.setVisibility(View.VISIBLE);
                                    }else {
                                        addGroupLl.setVisibility(View.GONE);
                                    }
                                    JSONArray jsonArray = json.getJSONArray("group_members");
                                    groupList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        groupList.add(jsonArray.getJSONObject(i));
                                    }
                                    adminId=json.getString("admin_id");
                                    memberCountTv.setText(""+groupList.size()+" Members");
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
                        groupAdapter.notifyDataSetChanged();
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
                params.put("chat_id", chatId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params=" + params + "\nURL==" + Group_Members);
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

    class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.MyViewHolder> {

        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView userIv;
            TextView nameTv,userNameTv;
            TextView removeTv;
            Button adminBtn;
            LinearLayout userLl;

            public MyViewHolder(View mView) {
                super(mView);
                userIv = mView.findViewById(R.id.userIv);
                removeTv = mView.findViewById(R.id.removeTv);
                nameTv = mView.findViewById(R.id.nameTv);
                userNameTv = mView.findViewById(R.id.userNameTv);
                adminBtn = mView.findViewById(R.id.adminBtn);
                userLl = mView.findViewById(R.id.userLl);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mActivity)
                    .inflate(R.layout.row_group_member, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                holder.nameTv.setText(groupList.get(position).getString("name"));
                holder.userNameTv.setText("@"+groupList.get(position).getString("username"));
                Picasso.with(mActivity)
                        .load(groupList.get(position).getString("profilepic"))
                        .placeholder(R.drawable.artist)
                        .error(R.drawable.artist)
                        .into(holder.userIv);

                holder.adminBtn.setVisibility(View.GONE);
                holder.removeTv.setVisibility(View.GONE);

                if (adminId.equalsIgnoreCase(groupList.get(position).getString("user_id"))){
                    holder.adminBtn.setVisibility(View.VISIBLE);
                }
                if (adminId.equalsIgnoreCase(userId) &&
                        !adminId.equalsIgnoreCase(groupList.get(position).getString("user_id"))){
                    holder.removeTv.setVisibility(View.VISIBLE);
                }

                holder.userIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mActivity, ProfileActivity.class);
                            intent.putExtra("showProfileUserId",
                                    groupList.get(position).getString("id"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                /*holder.userLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(mActivity, ProfileActivity.class);
                            intent.putExtra("showProfileUserId",
                                    groupList.get(position).getString("id"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });*/

                holder.removeTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
                            alertDialog.setTitle("Alert!");
                            alertDialog.setMessage("Are you sure you want to remove "+
                                    groupList.get(position).getString("name") +" from this group?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        removeMemberApi(groupList.get(position).getString("user_id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        }catch (Exception e){
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
            return groupList.size();
        }
    }

    void removeMemberApi(final String memberId) {
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REMOVE_MEMBER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            AppHelper.sop("response=removeMemberApi=" + response);
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                Toast.makeText(mActivity,jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                getGroupMembers();
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
                AppHelper.sop("params=" + params + "\nURL==" + REMOVE_MEMBER);
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

    public void updateGroup(final String chatingId, final String groupName) {
        progressDialog.show();
        if (groupImageBitmap == null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_GROUP,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    AppHelper.sop("response=updateGroup="+response);
                    JSONObject jsonObject, result;
                    try {
                        jsonObject = new JSONObject(response);
                        if (jsonObject.getString("flag").equals("Success")) {
                            result = jsonObject.getJSONObject("response");
                            SharedPreferences.Editor profileImageEditor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                            profileImageEditor.putString("receiverName", result.getString("groupName"));
                            profileImageEditor.commit();
                            Toast.makeText(mActivity, "Group update successful", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressDialog.dismiss();
                }
            })

            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("groupName", groupName);
                    params.put("chatID", chatingId);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params="+params+"\nURL="+UPDATE_GROUP);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        } else {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPDATE_GROUP,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            String str = new String(response.data);
                            progressDialog.dismiss();
                            AppHelper.sop("response=updateGroup="+str);
                            JSONObject jsonObject, result;
                            try {
                                jsonObject = new JSONObject(str);
                                if (jsonObject.getString("flag").equals("Success")) {
                                    result = jsonObject.getJSONObject("response");
//                                    groupImage = result.getString("url");
//                                    receiverName = result.getString("groupName");
                                    SharedPreferences.Editor profileImageEditor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                                    profileImageEditor.putString("receiverName", result.getString("groupName"));
                                    profileImageEditor.putString("groupImage", result.getString("url"));
                                    profileImageEditor.commit();
                                    Toast.makeText(mActivity, "Group update successful", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    progressDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("groupName", groupName);
                    params.put("chatID", chatingId);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    AppHelper.sop("params="+params+"\nURL="+UPDATE_GROUP);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    sendGroupImageName=groupName+"_img";
                    params.put("groupPic", new DataPart(sendGroupImageName, AppHelper.getFileDataFromDrawable(mActivity, groupImageBitmap), "image/jpeg"));
                    AppHelper.sop("params="+params+"\nURL="+UPDATE_GROUP);
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_GALLERY_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_TAKE_PHOTO == requestCode && resultCode == RESULT_OK) {
//            Bitmap bitmap = null;
            groupImageBitmap = (Bitmap) data.getExtras().get("data");
//            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            groupImg.setImageBitmap(groupImageBitmap);
//            userProfileImageUpdate.setImageDrawable(mDrawable);
        }

        else if (requestCode == PICK_GALLERY_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri filePath = null;
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                groupImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
                //Setting the Bitmap to ImageView
                groupImg.setImageBitmap(groupImageBitmap);
//                userProfileImageUpdate.setImageDrawable(mDrawable);
                AppHelper.sop("filePath===" + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_ADD_MEMBER){
            if (resultCode == RESULT_OK){
                getGroupMembers();
            }
        }
    }

}
