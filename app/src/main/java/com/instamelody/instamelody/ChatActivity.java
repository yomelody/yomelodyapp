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
import android.text.TextUtils;
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
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.app.Config;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.os.Environment.isExternalStorageEmulated;
import static android.os.Environment.isExternalStorageRemovable;

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
    private ChatAdapter mAdapter;

    ArrayList<Message> chatList = new ArrayList<>();// list of messages
    String SEND_MESSAGE_URL = "http://35.165.96.167/api/chat.php";
    String MESSAGES_LIST_URL = "http://35.165.96.167/api/messageList.php";
    String DEVICE_TYPE = "device_type";
    String SENDER_ID = "senderID";
    String RECEIVER_ID = "receiverID";
    String CHAT_ID = "chatID";
    String IS_READ = "isread";
    String TITLE = "title";
    String MESSAGE = "message";
    String DEVICE_TOKEN = "device_token";

    ArrayList<String> fileList = new ArrayList<String>();// list of file paths
    File[] files;
    ArrayList<Message> messageArrayList;
    ArrayList<String> fileArray = new ArrayList<>();
    //    Set<String> recieverId = new HashSet<>();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    EditText etMessage;

    public View customView;
    ImageView ivBackButton, ivHomeButton, ivAdjust, ivCamera, tvImgChat, ivNewChat;
    TextView tvSend, tvMsgChat;
    RecyclerView recycleImage, recyclerViewChat;
    LayoutInflater inflater;
    RelativeLayout rlMessage;
    String deviceToken;
    RecentImagesAdapter riAdapter;
    ChatAdapter cAdapter;
    public static TextView tvUserName;
    String KEY_FLAG = "flag";
    String userId, recieverId, recieverName, chatId = "", recieverImage;
    RelativeLayout rlNoMsg, rlTxtContent;
    ImageView ivRecieverProfilePic;
    TextView tvRecieverName;

    @TargetApi(18)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvUserName = (TextView) findViewById(R.id.tvUserName);

        fileArray.clear();
        getGalleryImages();

        SharedPreferences loginSharedPref = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId", null);

        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
        recieverId = prefs.getString("receiverId", null);
        recieverName = prefs.getString("recieverName", null);
        recieverImage = prefs.getString("recieverImage", null);
        chatId = prefs.getString("chatId", null);

        getChatMsgs(chatId);

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

        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
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

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String imageUrl = intent.getStringExtra("imageUrl");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    tvMsgChat.setText(message);
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

        SharedPreferences token = this.getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
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
                editor.putString("recieverName", "");
                editor.putString("recieverImage", "");
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
                editor.putString("recieverName", "");
                editor.putString("recieverImage", "");
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
                editor.putString("recieverName", "");
                editor.putString("recieverImage", "");
                editor.putString("chatId", "");
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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

                    sendMessage(message, userId);

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

        ivAdjust.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = getSharedPreferences("ContactsData", MODE_PRIVATE).edit();
        editor.putString("receiverId", "");
        editor.putString("recieverName", "");
        editor.putString("recieverImage", "");
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
    protected void onResume() {
        super.onResume();
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void getChatMsgs(final String chat_Id) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, MESSAGES_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        chatList.clear();
                        cAdapter.notifyDataSetChanged();
//                        recyclerViewChat.smoothScrollToPosition(cAdapter.getItemCount());

                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString(KEY_FLAG).equals("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                JSONArray resultArray = result.getJSONArray("message LIst");
                                String uname = "";
                                if (resultArray.length() > 0) {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        Message message = new Message();
                                        JSONObject chatJson = resultArray.getJSONObject(i);
                                        message.setId(chatJson.getString("id"));
                                        message.setSenderId(chatJson.getString("senderID"));
                                        message.setCreatedAt(chatJson.getString("sendat"));
                                        uname = chatJson.getString("receiver_name");
                                        message.setMessage(chatJson.getString("message"));
                                        SharedPreferences loginSharedPref = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
                                        String profilePic = loginSharedPref.getString("profilePic", null);
                                        message.userProfileImage(profilePic);
                                        chatList.add(message);
                                    }

                                    List<String> groupNameList = Arrays.asList(uname.split(","));

                                    String listnames = "";
                                    if (groupNameList.size() == 1) {
                                        tvUserName.setText(groupNameList.get(0));
                                    } else {
                                        for (int i = 1; i < groupNameList.size(); i++) {
                                            listnames = listnames + ", " + groupNameList.get(i);
                                        }
                                        tvUserName.setText(listnames);
                                    }

                                } else {
                                    tvUserName.setText(recieverName);
                                    tvRecieverName.setText(" " + recieverName);
                                    Picasso.with(ivRecieverProfilePic.getContext()).load(recieverImage).into(ivRecieverProfilePic);
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
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CHAT_ID, chat_Id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void sendMessage(final String message, final String user_Id) {

        final StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_MESSAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        cAdapter.notifyDataSetChanged();
                        getChatMsgs(chatId);
                        recyclerViewChat.smoothScrollToPosition(cAdapter.getItemCount());
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

                params.put(SENDER_ID, user_Id);
                params.put(RECEIVER_ID, recieverId);
                params.put(CHAT_ID, chatId);
                params.put(TITLE, "");
                params.put(MESSAGE, message);
                params.put(IS_READ, "");

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
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
                    }
                    File file;
                    for (int i = 0; i < length; i++) {
                        file = files[last];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
                            fileArray.add(file.getAbsolutePath());
                            last = last - 1;
                        }
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
                    if (length > 30) {
                        length = 30;
                        last = length;
                    }
                    File file;
                    for (int i = 0; i < length; i++) {
                        file = files[last];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
//                            fileArray.add(0,file.getAbsolutePath());
                            fileArray.add(file.getAbsolutePath());
                            last = last - 1;
                        }
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to read Internal storage", Toast.LENGTH_LONG).show();
            }
        }
    }

//    public void getGalleryImages() {
//
//        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
//        if (isSDPresent) {
////            Yes, SD - card is present
//            String ExternalStorageDirectoryPath = Environment
//                    .getExternalStorageDirectory()
//                    .getAbsolutePath();
//            String targetPath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
////            Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
//            File targetDirector = new File(targetPath);
//
//            if (targetDirector.listFiles() != null) {
//                File[] files = targetDirector.listFiles();
//                int length = files.length;
//                if (length > 15) {
//                    length = 15;
//                }
//                File file;
//                for (int i = 0; i < length - 1; i++) {
//                    file = files[i];
//                    fileArray.add(file.getAbsolutePath());
//                }
//            }
//        } else {
//            // No, SD-card is not present
//            String InternalStorageDirectoryPath = Environment
//                    .getDataDirectory()
//                    .getAbsolutePath();
//
//            String targetPath = InternalStorageDirectoryPath + "/DCIM/Camera/";
//
//            Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();
//            File targetDirector = new File(targetPath);
//
//            if (targetDirector.listFiles() != null) {
//                File[] files = targetDirector.listFiles();
//                int length = files.length;
//                if (length > 15) {
//                    length = 15;
//                }
//                File file;
//                for (int i = 0; i < length - 1; i++) {
//                    file = files[i];
//                    fileArray.add(file.getAbsolutePath());
//                }
//            }
//        }
//    }

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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