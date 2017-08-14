package com.instamelody.instamelody;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.instamelody.instamelody.Adapters.ChatAdapter;
import com.instamelody.instamelody.Adapters.RecentImagesAdapter;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.RecentImagesModel;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.ImageCompressor;
import com.instamelody.instamelody.utils.NotificationUtils;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

    private static final int CAMERA_PHOTO = 111;
    private Uri imageToUploadUri;

    Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 201;
    private static final String TAG = MainActivity.class.getSimpleName();

    ArrayList<Message> chatList = new ArrayList<>();// list of messages
    ArrayList<String> imageFileList = new ArrayList<>();
    ArrayList<RecentImagesModel> fileInfo = new ArrayList<>();
    ArrayList<String> groupList = new ArrayList<>();
    String CHECK_FILE_URL = "http://35.165.96.167/api/ShareAudioChat.php";
    String DEVICE_TYPE = "device_type";
    String SENDER_ID = "senderID";
    String RECEIVER_ID = "receiverID";
    String CHAT_ID = "chat_id";
    String CHAT_ID_ = "chatID";
    String IS_READ = "isread";
    String FILE_TYPE = "file_type";
    String FILE = "file";
    String TITLE = "title";
    String MESSAGE = "message";

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
    RelativeLayout rlNoMsg, rlTxtContent, rlInviteButton, rlSelectedImage;
    FrameLayout flClose;
    ImageView ivRecieverProfilePic, ivSelectedImage;
    TextView tvRecieverName;
    String username = "";
    String parent;
    String senderId = "";
    String chatType = "";
    String group = "";
    String flagFileType = "0"; // 0 = null, 1 = image file, 2 = station audio file , 3 = admin_melody audio file
    Bitmap sendImageBitmap;
    String sendImageName = "";

    @TargetApi(18)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        parent = getIntent().getStringExtra("from");
        imageFileList.clear();
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

        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
        senderId = prefs.getString("senderId", null);
        receiverId = prefs.getString("receiverId", null);
        RemoveNullValue();
        receiverName = prefs.getString("receiverName", null);
        receiverImage = prefs.getString("receiverImage", null);
        chatId = prefs.getString("chatId", null);
        chatType = prefs.getString("chatType", null);
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
        ivSelectedImage = (ImageView) findViewById(R.id.ivSelectedImage);
        rlSelectedImage = (RelativeLayout) findViewById(R.id.rlSelectedImage);
        flClose = (FrameLayout) findViewById(R.id.flClose);
        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerViewChat);
        recyclerViewChat.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        lm.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(lm);
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
//                    String imageUrl = intent.getStringExtra("fileUrl");
                    String chatId = intent.getStringExtra("chatId");
                    getChatMsgs(chatId);
//                    if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
//                        Bitmap bitmap = getBitmapFromURL(imageUrl);
//                        if (bitmap != null) {
//                            tvImgChat.setImageBitmap(bitmap);
//                        } else {
//                            Toast.makeText(getApplicationContext(), "No Image!!", Toast.LENGTH_LONG).show();
//                        }
//                    }
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

                    getChatMsgs(chatId);

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
                    wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    wmlp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    recycleImage.setHasFixedSize(true);
                    recycleImage.setItemViewCacheSize(10);
                    recycleImage.setDrawingCacheEnabled(true);
                    recycleImage.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    recycleImage.setItemAnimator(new DefaultItemAnimator());
                    RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                    recycleImage.setLayoutManager(lm);
                    riAdapter = new RecentImagesAdapter(fileInfo, getApplicationContext());
                    recycleImage.setAdapter(riAdapter);

                    rlBtnPhotoLibrary.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent getIntent = new Intent(Intent.ACTION_PICK);
                            getIntent.setType("image/*");
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                            alertDialog.cancel();
                        }
                    });

                    rlBtnTakePhotoOrVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Date d = new Date();
                            CharSequence s  = DateFormat.format("yyyyMMdd_hh_mm_ss", d.getTime());
                            File f = new File(Environment.getExternalStorageDirectory(), "IMG_" + s.toString() + ".jpg");
                            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            imageToUploadUri = Uri.fromFile(f);
                            startActivityForResult(chooserIntent, CAMERA_PHOTO);
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

        flClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImageBitmap.recycle();
                flagFileType = "0";
                rlSelectedImage.setVisibility(View.GONE);
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

        if (requestCode == CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
            if (imageToUploadUri != null) {
                Uri selectedImage = imageToUploadUri;
                sendImageName = imageToUploadUri.getPath();
                getContentResolver().notifyChange(selectedImage, null);
                ImageCompressor ic = new ImageCompressor(getApplicationContext());
                sendImageBitmap = ic.compressImage(sendImageName);
                if (sendImageBitmap != null) {
                    rlSelectedImage.setVisibility(View.VISIBLE);
                    flClose.setVisibility(View.VISIBLE);
                    ivSelectedImage.setImageBitmap(sendImageBitmap);
                } else {
                    Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
            }
        }

//        if (this.requestCode == requestCode && resultCode == RESULT_OK && null != data) {
////            sendImageBitmap = (Bitmap) data.getExtras().get("data");
////            rlSelectedImage.setVisibility(View.VISIBLE);
////            flClose.setVisibility(View.VISIBLE);
////            ivSelectedImage.setImageBitmap(sendImageBitmap);
//            Uri uri = data.getData();
//            String uriString = uri.toString();
//            File myFile = new File(uriString);
//            String path = myFile.getAbsolutePath();
//            sendImageName = path.substring(path.lastIndexOf("/") + 1);
//            ImageCompressor ic = new ImageCompressor(getApplicationContext());
//            sendImageBitmap = ic.compressImage(path);
//            rlSelectedImage.setVisibility(View.VISIBLE);
//            flClose.setVisibility(View.VISIBLE);
//            ivSelectedImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
//
//        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String img_Decodable_Str = cursor.getString(columnIndex);
                cursor.close();
                sendImageName = img_Decodable_Str.substring(img_Decodable_Str.lastIndexOf("/") + 1);
                rlSelectedImage.setVisibility(View.VISIBLE);
                flClose.setVisibility(View.VISIBLE);
                ImageCompressor ic = new ImageCompressor(getApplicationContext());
                sendImageBitmap = ic.compressImage(img_Decodable_Str);
                ivSelectedImage.setImageBitmap(sendImageBitmap);

//                sendImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                rlSelectedImage.setVisibility(View.VISIBLE);
//                flClose.setVisibility(View.VISIBLE);
//                ivSelectedImage.setImageBitmap(sendImageBitmap);
//                String path = selectedImageUri.toString();
//                try {
//                    File file = new File(new URI(path));
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }

//                File myFile = new File(selectedImageUri.getPath());
//                myFile.getAbsolutePath();

//                ivSelectedImage.setImageBitmap(sendImageBitmap);
//                String uriString = selectedImageUri.toString();
//                File myFile = new File(uriString);
//                String path = myFile.getAbsolutePath();
//                sendImageName = path.substring(path.lastIndexOf("/") + 1);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        flagFileType = "1";
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
//                        recyclerViewChat.smoothScrollToPosition(cAdapter.getItemCount());
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
                                        message.setSenderId(senderID);
                                        String sendAt = chatJson.getString("sendat");
                                        message.setCreatedAt(sendAt);
                                        String chatMessage = chatJson.getString("message");
                                        message.setMessage(chatMessage);
                                        String profilePic = chatJson.getString("sender_pic");
                                        message.setProfilePic(profilePic);
                                        String file = chatJson.getString("file_url");
                                        message.setFile(file);
                                        String fileType = chatJson.getString("file_type");
                                        message.setFileType(fileType);
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
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, CHAT,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String str = new String(response.data);
//                        Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
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
                Map<String, String> params = new HashMap<>();
                if (chatType.equals("group")) {
                    if (senderId.equals(user_Id)) {
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    } else {
                        group = senderId + "," + receiverId;
                        groupList = new ArrayList<>(Arrays.asList(group.split(",")));
                        groupList.remove(groupList.indexOf(user_Id));
                        String s = groupList.toString().replaceAll(", ", ",");
                        receiverId = s.substring(1, s.length() - 1).trim();
                        params.put(RECEIVER_ID, receiverId);
                        params.put("groupName", tvUserName.getText().toString().trim());
                    }
                } else {
                    if (receiverId.equals(user_Id)) {
                        params.put(RECEIVER_ID, senderId);
                    } else {
                        params.put(RECEIVER_ID, receiverId);
                    }
                }
                if (flagFileType.equals("1")) {
                    params.put(FILE_TYPE, "image");
                } else if (flagFileType.equals("2")) {
                    params.put(FILE_TYPE, "station");
                } else if (flagFileType.equals("3")) {
                    params.put(FILE_TYPE, "admin_melody");
                } else {

                }
                params.put(SENDER_ID, user_Id);
                params.put(CHAT_ID, chatId);
                params.put(TITLE, "message");
                params.put(MESSAGE, message);
                params.put(IS_READ, "0");
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put(FILE, new DataPart(sendImageName, AppHelper.getFileDataFromDrawable(getBaseContext(), sendImageBitmap), "image/jpeg"));
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
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
                    int i = files.length - 1;
                    while (imageFileList.size() < 10) {
                        File file = files[i];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
                            imageFileList.add(file.getAbsolutePath());
                        }
                        i--;
                    }

                    String file, filename;
                    int length = imageFileList.size();
                    for (int j = 0; j < length; j++) {
                        RecentImagesModel rim = new RecentImagesModel();
                        file = imageFileList.get(j);
                        rim.setFilepath(file);
                        filename = file.substring(file.lastIndexOf("/") + 1);
                        rim.setName(filename);
                        fileInfo.add(rim);
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
                    int i = files.length - 1;
                    while (imageFileList.size() < 10) {
                        File file = files[i];
                        if (file.getAbsoluteFile().toString().trim().endsWith(".jpg")) {
                            imageFileList.add(file.getAbsolutePath());
                        }
                        i--;
                    }
                }

                String file, filename;
                int length = imageFileList.size();
                for (int i = 0; i < length; i++) {
                    RecentImagesModel rim = new RecentImagesModel();
                    file = imageFileList.get(i);
                    rim.setFilepath(file);
                    filename = file.substring(file.lastIndexOf("/") + 1);
                    rim.setName(filename);
                    fileInfo.add(rim);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Failed to read Internal storage", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Unknown storage found", Toast.LENGTH_LONG).show();
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

    public void RemoveNullValue() {
        try {
            receiverId = receiverId.replaceAll(",null", "");
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }
}