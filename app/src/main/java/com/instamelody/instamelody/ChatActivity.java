package com.instamelody.instamelody;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.instamelody.instamelody.Adapters.ChatAdapter;
import com.instamelody.instamelody.Adapters.RecentImagesAdapter;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.RecentImagesModel;
import com.instamelody.instamelody.utils.NotificationUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.isExternalStorageEmulated;
import static android.os.Environment.isExternalStorageRemovable;
import static com.instamelody.instamelody.utils.Const.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.SHARED_PREF;
import static com.instamelody.instamelody.utils.Const.ServiceType.CHAT;
import static com.instamelody.instamelody.utils.Const.ServiceType.MESSAGE_LIST;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 */

public class ChatActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 2;
    private final int requestCode = 20;
    Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 201;
    private static final String TAG = MainActivity.class.getSimpleName();

    ArrayList<Message> chatList = new ArrayList<>();// list of messages

    String CHECK_FILE_URL = "http://35.165.96.167/api/ShareAudioChat.php";
    String DEVICE_TYPE = "device_type";
    String SENDER_ID = "senderID";
    String RECEIVER_ID = "receiverID";
    String CHAT_ID = "chat_id";
    String CHAT_ID_ = "chatID";
    String IS_READ = "isread";
    String TITLE = "title";
    String MESSAGE = "message";

    ArrayList<RecentImagesModel> fileArray = new ArrayList<>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    EditText etMessage;

    ImageView ivBackButton, ivHomeButton, ivAdjust, ivCamera, tvImgChat, ivNewChat;
    TextView tvSend;
    RecyclerView recycleImage, recyclerViewChat;
    LayoutInflater inflater;
    RelativeLayout rlMessage;
    String deviceToken;
    RecentImagesAdapter riAdapter;
    ChatAdapter cAdapter;
    public static TextView tvUserName;
    String KEY_FLAG = "flag";
    String userId, receiverId, receiverName, packId, packType, receiverImage;
    static String chatId;
    RelativeLayout rlNoMsg, rlTxtContent, rlInviteButton;
    ImageView ivRecieverProfilePic;
    TextView tvRecieverName;
    String username = "";
    String parent;
    String senderId = "";

    @TargetApi(18)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        parent = getIntent().getStringExtra("from");
        fileArray.clear();
        getGalleryImages();

        SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
            username = loginSharedPref.getString("userName", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
            username = fbPref.getString("UserName", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
            username = twitterPref.getString("userName", null);
        }

//        SharedPreferences chatPrefs = getSharedPreferences("MessengerData", MODE_PRIVATE);
//        id= chatPrefs.getString("id", null);
//        senderId = chatPrefs.getString("senderId", null);
//        senderName = chatPrefs.getString("senderName", null);
//        receiverId = chatPrefs.getString("receiverId", null);
//        receiverName = chatPrefs.getString("receiverName", null);
//        coverPic = chatPrefs.getString("coverPic", null);
//        profilePic = chatPrefs.getString("profilePic", null);
//        message = chatPrefs.getString("message", null);
//        chatId = chatPrefs.getString("chatId", null);
//        isRead = chatPrefs.getString("isRead", null);
//        sendAt = chatPrefs.getString("sendAt", null);

        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
        senderId = prefs.getString("senderId", null);
        receiverId = prefs.getString("receiverId", null);
        receiverName = prefs.getString("receiverName", null);
        receiverImage = prefs.getString("receiverImage", null);
        chatId = prefs.getString("chatId", null);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(receiverName);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String message = bundle.getString("chat_id");
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }

        SharedPreferences packPref = getSharedPreferences("PackData", MODE_PRIVATE);
        packId = packPref.getString("PackId", null);
        packType = packPref.getString("PackType", null);

//        checkFile(packId, packType);
//        final String temp = packPref.getString("PackPresent", null);

        getChatMsgs(chatId);

//        if (temp.equals("true")) {
//            sendMessage("hello", userId, temp);
//        }

        etMessage = (EditText) findViewById(R.id.etMessage);
        etMessage.setHintTextColor(Color.parseColor("#7B888F"));
        inflater = LayoutInflater.from(ChatActivity.this);
        tvSend = (TextView) findViewById(R.id.tvSend);
        ivAdjust = (ImageView) findViewById(R.id.ivAdjust);
        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        rlMessage = (RelativeLayout) findViewById(R.id.rlMessage);
        rlNoMsg = (RelativeLayout) findViewById(R.id.rlNoMsg);
        ivRecieverProfilePic = (ImageView) findViewById(R.id.ivRecieverProfilePic);
        tvRecieverName = (TextView) findViewById(R.id.tvRecieverName);
        rlTxtContent = (RelativeLayout) findViewById(R.id.rlTxtContent);
        ivNewChat = (ImageView) findViewById(R.id.ivNewChat);
        rlInviteButton = (RelativeLayout) findViewById(R.id.rlInviteButton);

        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        lm.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(lm);
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setItemViewCacheSize(10);
        recyclerViewChat.setDrawingCacheEnabled(true);
        recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
        cAdapter = new ChatAdapter(getApplicationContext(), chatList);
        recyclerViewChat.setAdapter(cAdapter);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received
//                    String message = intent.getStringExtra("message");
                    String imageUrl = intent.getStringExtra("imageUrl");
                    String chatId = intent.getStringExtra("chatId");
                    getChatMsgs(chatId);
                    if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                        Bitmap bitmap = getBitmapFromURL(imageUrl);
                        if (bitmap != null) {
                            tvImgChat.setImageBitmap(bitmap);
                        } else {
                            Toast.makeText(getApplicationContext(), "No Image!!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };

        SharedPreferences token = this.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        deviceToken = token.getString("regId", null);
        //if token is not null
//        if (deviceToken != null) {
//            //displaying the token
//            tvRegId.setText(deviceToken);
//        } else {
//            //if token is null that means something wrong
//            tvRegId.setText("Token not generated");
//        }

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ivAdjust.setVisibility(View.GONE);
                tvSend.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etMessage.getText().toString().length() == 0) {
                    ivAdjust.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);
                }
            }
        });

        ivNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
//                Log.d("rcvrimg", "receiverImage 2 has no value");
                editor.putString("chatId", "");
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                intent.putExtra("Previous", "Chat");
                startActivity(intent);
            }
        });

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
//                Log.d("rcvrimg", "receiverImage 3 has no value");
                editor.putString("chatId", "");
                editor.commit();

                finish();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
//                Log.d("rcvrimg", "receiverImage 4 has no value");
                editor.putString("chatId", "");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        rlInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
                editor.putString("receiverId", "");
                editor.putString("receiverName", "");
                editor.putString("receiverImage", "");
//                Log.d("rcvrimg", "receiverImage 5 has no value");
                editor.putString("chatId", "");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), ContactsActivity.class);
                intent.putExtra("Previous", "Chat");
                startActivity(intent);
            }
        });

        tvSend.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                if (userId != null) {
                    String message = etMessage.getText().toString().trim();
                    etMessage.getText().clear();
                    ivAdjust.setVisibility(View.VISIBLE);
                    tvSend.setVisibility(View.GONE);

                    sendMessage(message, userId/*, temp*/);
//                    int messageCount = Integer.parseInt(tvMessageCount.getText().toString().trim()) + 1;
//                    tvMessageCount.setText(String.valueOf(messageCount));
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                } else {
                    Toast.makeText(getApplicationContext(), "Log in to Chat", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                /**
                 *  Make a RecyclerView in DialogBox which gets images from gallery
                 */
                LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = mInflater.inflate(R.layout.custom_dialog_view, null, false);
                final Dialog alertDialog = new Dialog(ChatActivity.this);

                if (checkPermissions()) {
                    recycleImage = (RecyclerView) customView.findViewById(R.id.recyclerViewDialogBox);
                    RelativeLayout rlBtnPhotoLibrary = (RelativeLayout) customView.findViewById(R.id.rlBtnPhotoLibrary);
                    RelativeLayout rlBtnTakePhotoOrVideo = (RelativeLayout) customView.findViewById(R.id.rlBtnTakeFromLibrary);
                    RelativeLayout rlBtnCancel = (RelativeLayout) customView.findViewById(R.id.rlBtnCancel);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(customView);
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
                    wmlp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                    wmlp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;

                    recycleImage.setHasFixedSize(true);
                    RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    recycleImage.setLayoutManager(lm);
                    recycleImage.setHasFixedSize(true);
                    recycleImage.setItemViewCacheSize(10);
                    recycleImage.setDrawingCacheEnabled(true);
                    recycleImage.setItemAnimator(new DefaultItemAnimator());
                    riAdapter = new RecentImagesAdapter(fileArray, getApplicationContext());
                    recycleImage.setAdapter(riAdapter);

                    rlBtnPhotoLibrary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                            alertDialog.cancel();
                        }
                    });

                    rlBtnTakePhotoOrVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, requestCode);
                            alertDialog.cancel();
                        }
                    });

                    rlBtnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                        }
                    });
                    alertDialog.show();

                } else {
                    alertDialog.cancel();
                    setPermissions();
                }
            }
        });

        ivAdjust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
        editor.putString("receiverId", "");
        editor.putString("receiverName", "");
        editor.putString("receiverImage", "");
        editor.putString("chatId", "");
        editor.commit();

        finish();
        Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
        startActivity(intent);
    }

    /**
     * Downloading image and showing as a message
     */

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            ivAdjust.setVisibility(View.VISIBLE);
//            tvCancel.setVisibility(View.GONE);
//            ivAdjust.invalidate();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK && null != data) {
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri selectedImageUri = data.getData();
            String selectedImagePath = selectedImageUri.getPath();
            Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();

            String ExternalStorageDirectoryPath = Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath();

            String targetPath = ExternalStorageDirectoryPath + "/Pictures";
            Toast.makeText(getApplicationContext(), ExternalStorageDirectoryPath, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getChatMsgs(chatId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMsgs(chatId);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

   /* public void checkFile(final String pack_id, final String pack_type) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CHECK_FILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                SharedPreferences.Editor editor = getSharedPreferences("PackData", MODE_PRIVATE).edit();
                                editor.putString("PackPresent", "True");
                                editor.commit();
                            } else {
                                SharedPreferences.Editor editor = getSharedPreferences("PackData", MODE_PRIVATE).edit();
                                editor.putString("PackPresent", "False");
                                editor.commit();
                                Toast.makeText(ChatActivity.this, "Failed to send this file", Toast.LENGTH_SHORT).show();
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
                params.put("file", pack_id);
                params.put("file_type", pack_type);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }*/

    public void getChatMsgs(final String chat_Id) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MESSAGE_LIST,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        chatList.clear();
                        cAdapter.notifyDataSetChanged();
                        JSONObject jsonObject;

                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                JSONArray resultArray = result.getJSONArray("message LIst");
                                if (resultArray.length() > 0) {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        Message message = new Message();
                                        JSONObject chatJson = resultArray.getJSONObject(i);
                                        String id = chatJson.getString("id");
                                        message.setId(id);

                                        String senderID = chatJson.getString("senderID");
//                                        String receiverID = chatJson.getString("receiverID");
//                                        SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
//                                        editor.putString("receiverId", senderID);
//                                        editor.putString("temp", receiverID);
//                                        editor.commit();

                                        message.setSenderId(senderID);
                                        String sendat = chatJson.getString("sendat");
                                        message.setCreatedAt(sendat);
                                        String messagef = chatJson.getString("message");
                                        message.setMessage(messagef);
                                        String profilePic = chatJson.getString("sender_pic");
                                        message.setProfilePic(profilePic);
                                        chatList.add(message);
                                    }
                                } else {
                                    tvRecieverName.setText(" " + receiverName);
                                    Picasso.with(ivRecieverProfilePic.getContext()).load(receiverImage).into(ivRecieverProfilePic);
                                    rlNoMsg.setVisibility(View.VISIBLE);
                                    rlTxtContent.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()

                {
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
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CHAT_ID_, chat_Id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void sendMessage(final String message, final String user_Id/*, final String packAvailable*/) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, CHAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String str = response;
                        Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                        getChatMsgs(chatId);
//                        cAdapter.notifyDataSetChanged();
//                        recyclerViewChat.smoothScrollToPosition(cAdapter.getItemCount());

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
                        Toast.makeText(getApplicationContext(), "chat api error response " + errorMsg, Toast.LENGTH_SHORT).show();
                        Log.d("Error", errorMsg);
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (receiverId.equals(user_Id)) {
                    params.put(RECEIVER_ID, senderId);
                    params.put(SENDER_ID, user_Id);
                } else {
                    params.put(RECEIVER_ID, receiverId);
                    params.put(SENDER_ID, user_Id);
                }
                params.put(CHAT_ID, chatId);
                params.put(TITLE, "message");
                params.put(MESSAGE, message);
                params.put(IS_READ, "0");

                /*if (packAvailable.equals("True")) {
                    params.put("file", packId);
                    params.put("file_type", packType);
                }*/
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public String checkIt(String response) {
        JSONObject jsonObject;
        String value = "0";
        try {
            jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("1")) {
                value = "1";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    public void getGalleryImages() {

        if (isExternalStorageRemovable()) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String ExternalStoragePath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
                File targetDirector = new File(ExternalStoragePath);

                if (targetDirector.listFiles() != null) {
                    File[] files = targetDirector.listFiles();
                    int last;
                    int length = files.length;
                    last = length - 1;
                    if (length > 15) {
                        length = 15;
                        last = length;
                    }
                    File file;

                    for (int i = 0; i < length; i++) {
                        RecentImagesModel rim = new RecentImagesModel();
                        file = files[last];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
                            rim.setName(file.getName());
                            rim.setFilepath(file.getAbsolutePath().toString().trim());
                            last = last - 1;
                        }
                        fileArray.add(rim);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to read External storage", Toast.LENGTH_LONG).show();
            }
        }

        if (isExternalStorageEmulated()) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String InternalStoragePath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
                File targetDirector = new File(InternalStoragePath);

                if (targetDirector.listFiles() != null) {
                    File[] files = targetDirector.listFiles();
                    int last;
                    int length = files.length;
                    last = length - 1;
                    if (length > 15) {
                        length = 15;
                        last = length;
                    }
                    File file;
                    for (int i = 0; i < length; i++) {
                        RecentImagesModel rim = new RecentImagesModel();
                        file = files[last];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
                            rim.setName(file.getName());
                            rim.setFilepath(file.getAbsolutePath().toString().trim());
                            last = last - 1;
                        }
                        fileArray.add(rim);
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to read Internal storage", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean checkPermissions() {
        if ((ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void setPermissions() {
        if (ContextCompat.checkSelfPermission(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            } else {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    checkPermissions();
                }
                break;
        }
    }
}