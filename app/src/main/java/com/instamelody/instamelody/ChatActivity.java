package com.instamelody.instamelody;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import com.instamelody.instamelody.Adapters.ChatAdapter;
import com.instamelody.instamelody.Adapters.RecentImagesAdapter;
import com.instamelody.instamelody.Models.AudioDetails;
import com.instamelody.instamelody.Models.Message;
import com.instamelody.instamelody.Models.RecentImagesModel;
import com.instamelody.instamelody.Models.SharedAudios;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.ImageCompressor;
import com.instamelody.instamelody.utils.NotificationUtils;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.isExternalStorageEmulated;
import static android.os.Environment.isExternalStorageRemovable;
import static com.instamelody.instamelody.Adapters.ChatAdapter.AudioOperator;
import static com.instamelody.instamelody.Adapters.ChatAdapter.playingAudio;
import static com.instamelody.instamelody.Adapters.ChatAdapter.tvNum;
import static com.instamelody.instamelody.utils.Const.PUSH_NOTIFICATION;
import static com.instamelody.instamelody.utils.Const.SHARED_PREF;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.CHAT;
import static com.instamelody.instamelody.utils.Const.ServiceType.MESSAGE_LIST;
import static com.instamelody.instamelody.utils.Const.ServiceType.UPDATE_GROUP;

/**
 * Created by Shubhansh Jaiswal on 17/01/17.
 */

public class ChatActivity extends AppCompatActivity {

    public static TextView tvUserName, tvNamePlayer, tvUserNamePlayer, tvAudioNamePlayer, tvNumPlayer;
    public static ImageView ivPausePlayer, ivPlayPlayer, userProfileImagePlayer;
    public static RelativeLayout rlChatPlayer;
    public static FrameLayout flPlayPausePlayer;
    ImageView ivClose;
    EditText etMessage, etGroupName;
    ImageView ivBackButton, ivHomeButton, ivAdjust, ivCamera, ivNewChat, ivRecieverProfilePic, ivSelectedImage, ivGroupImage;
    TextView tvSend, tvRecieverName, tvDone, tvEdit, tvUpdate;
    RecyclerView recycleImage, recyclerViewChat;
    RelativeLayout rlNoMsg, rlTxtContent, rlInviteButton, rlMessage, rlSelectedImage, rlUserName, rlPrevPlayer, rlNextPlayer, rlUpdateGroup;

    RecentImagesAdapter riAdapter;
    ChatAdapter cAdapter;
    LayoutInflater inflater;
    Bitmap sendImageBitmap, groupImageBitmap;
    private Uri imageToUploadUri;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int TAKE_CAMERA_PHOTO = 101;
    private static final int PICK_GALLERY_IMAGE = 102;
    private static final int PERMISSIONS_READ_STORAGE = 201;
    ArrayList<Message> chatList = new ArrayList<>();// list of messages
    ArrayList<SharedAudios> sharedAudioList = new ArrayList<>();// list of shared audios
    ArrayList<AudioDetails> audioDetailsList = new ArrayList<>();// list of shared audio details
    public static ArrayList<RecentImagesModel> fileInfo = new ArrayList<>();
    ArrayList<String> imageFileList = new ArrayList<>();
    ArrayList<String> groupList = new ArrayList<>();
    String CHECK_FILE_URL = "http://35.165.96.167/api/ShareAudioChat.php";
    String SENDER_ID = "senderID";
    String RECEIVER_ID = "receiverID";
    String CHAT_ID = "chat_id";
    String CHAT_ID_ = "chatID";
    String IS_READ = "isread";
    String FILE_TYPE = "file_type";
    String FILE = "file";
    String TITLE = "title";
    String MESSAGE = "message";
    String KEY_FLAG = "flag";
    String userId, chatId, receiverId, receiverName, packId, packType, receiverImage, groupImage, deviceToken, parent;
    String username = "";
    String senderId = "";
    String chatType = "";
    String group = "";
    String sendImageName = "";
    String sendGroupImageName = "";
    String flagFileType = "0"; // 0 = null, 1 = image file, 2 = station audio file , 3 = admin_melody audio file
    int updateGroupFlag = 0;

    @TargetApi(18)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        rlUserName = (RelativeLayout) findViewById(R.id.rlUserName);
        rlSelectedImage = (RelativeLayout) findViewById(R.id.rlSelectedImage);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        ivSelectedImage = (ImageView) findViewById(R.id.ivSelectedImage);
        rlChatPlayer = (RelativeLayout) findViewById(R.id.rlChatPlayer);
        tvNamePlayer = (TextView) findViewById(R.id.tvNamePlayer);
        tvUserNamePlayer = (TextView) findViewById(R.id.tvUserNamePlayer);
        tvAudioNamePlayer = (TextView) findViewById(R.id.tvAudioNamePlayer);
        tvNumPlayer = (TextView) findViewById(R.id.tvNumPlayer);
        rlPrevPlayer = (RelativeLayout) findViewById(R.id.rlPrevPlayer);
        rlNextPlayer = (RelativeLayout) findViewById(R.id.rlNextPlayer);
        ivPausePlayer = (ImageView) findViewById(R.id.ivPausePlayer);
        ivPlayPlayer = (ImageView) findViewById(R.id.ivPlayPlayer);
        flPlayPausePlayer = (FrameLayout) findViewById(R.id.flPlayPausePlayer);
        userProfileImagePlayer = (ImageView) findViewById(R.id.userProfileImagePlayer);
        rlUpdateGroup = (RelativeLayout) findViewById(R.id.rlUpdateGroup);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvEdit = (TextView) findViewById(R.id.tvEdit);
        tvUpdate = (TextView) findViewById(R.id.tvUpdate);
        ivGroupImage = (ImageView) findViewById(R.id.ivGroupImage);
        etGroupName = (EditText) findViewById(R.id.etGroupName);

        SharedPreferences selectedImagePos = getApplicationContext().getSharedPreferences("selectedImagePos", MODE_PRIVATE);
        if (selectedImagePos.getString("pos", null) != null) {
            String pos = selectedImagePos.getString("pos", null);
            int position = Integer.parseInt(pos);
            SharedPreferences.Editor editor = getSharedPreferences("selectedImagePos", MODE_PRIVATE).edit();
            editor.putString("pos", null);
            editor.apply();
            sendImageName = fileInfo.get(position).getName();
            sendImageBitmap = fileInfo.get(position).getBitmap();
            rlSelectedImage.setVisibility(View.VISIBLE);
            ivSelectedImage.setVisibility(View.VISIBLE);
            ivSelectedImage.setImageBitmap(sendImageBitmap);
            flagFileType = "1";
        }

//        parent = getIntent().getStringExtra("from");

        imageFileList.clear();
        fileInfo.clear();
        getGalleryImages();

        SharedPreferences audioShareData = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE);
        if (audioShareData.getString("recID", null) != null) {
            flagFileType = "2";
            sendMessage("", userId);
        }

        SharedPreferences prefs = getSharedPreferences("ContactsData", MODE_PRIVATE);
        senderId = prefs.getString("senderId", null);
        receiverId = prefs.getString("receiverId", null);
        RemoveNullValue();
        receiverName = prefs.getString("receiverName", null);
        receiverImage = prefs.getString("receiverImage", null);
        chatId = prefs.getString("chatId", null);
        chatType = prefs.getString("chatType", null);
        groupImage = prefs.getString("groupImage", null);
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
        getChatMsgs(chatId);

//        groupImageBitmap = ((BitmapDrawable) ivGroupImage.getDrawable()).getBitmap();
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
        recyclerViewChat.setItemViewCacheSize(10);
        recyclerViewChat.setDrawingCacheEnabled(true);
        recyclerViewChat.setItemAnimator(new DefaultItemAnimator());
        cAdapter = new ChatAdapter(getApplicationContext(), chatList, audioDetailsList, sharedAudioList);
        recyclerViewChat.setAdapter(cAdapter);

        SharedPreferences token = this.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        deviceToken = token.getString("regId", null);
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
                    sendMessage(message, userId);
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    rlSelectedImage.setVisibility(View.GONE);
                    ivClose.setVisibility(View.GONE);

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
                            startActivityForResult(galleryIntent, PICK_GALLERY_IMAGE);
                            alertDialog.cancel();
                        }
                    });

                    rlBtnTakePhotoOrVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Date d = new Date();
                            CharSequence s = DateFormat.format("yyyyMMdd_hhmmss", d.getTime());
                            File f = new File(Environment.getExternalStorageDirectory(), "IMG_" + s.toString() + ".jpg");
                            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            imageToUploadUri = Uri.fromFile(f);
                            startActivityForResult(chooserIntent, TAKE_CAMERA_PHOTO);
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

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagFileType = "0";
                rlSelectedImage.setVisibility(View.GONE);
            }
        });

        flPlayPausePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.rlChatPlayer.setVisibility(View.VISIBLE);
                if (ivPlayPlayer.getVisibility() == View.VISIBLE) {
                    ivPlayPlayer.setVisibility(View.GONE);
                    ivPausePlayer.setVisibility(View.VISIBLE);
                } else {
                    ivPausePlayer.setVisibility(View.GONE);
                    ivPlayPlayer.setVisibility(View.VISIBLE);
                }
                SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                String audioUrl = sharedAudios.getRecordingUrl();
                Picasso.with(userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).into(userProfileImagePlayer);
                ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                ChatActivity.tvNumPlayer.setText(tvNum.getText().toString().trim());
                AudioOperator(audioUrl);
            }
        });

        rlPrevPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlayPlayer.setVisibility(View.GONE);
                ivPausePlayer.setVisibility(View.VISIBLE);
                playingAudio = playingAudio - 1;
                String str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
                tvNum.setText(str);
                SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                String audioUrl = sharedAudios.getRecordingUrl();
                Picasso.with(userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).into(userProfileImagePlayer);
                ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                ChatActivity.tvNumPlayer.setText(str);
                AudioOperator(audioUrl);
            }
        });

        rlNextPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivPlayPlayer.setVisibility(View.GONE);
                ivPausePlayer.setVisibility(View.VISIBLE);
                playingAudio = playingAudio + 1;
                String str = "(" + (playingAudio + 1) + " of " + String.valueOf(sharedAudioList.size()) + ")";
                tvNum.setText(str);
                SharedAudios sharedAudios = sharedAudioList.get(playingAudio);
                String audioUrl = sharedAudios.getRecordingUrl();
                Picasso.with(userProfileImagePlayer.getContext()).load(sharedAudios.getProfileUrl()).into(userProfileImagePlayer);
                ChatActivity.tvNamePlayer.setText(sharedAudios.getName());
                ChatActivity.tvUserNamePlayer.setText(sharedAudios.getUserName());
                ChatActivity.tvNumPlayer.setText(str);
                AudioOperator(audioUrl);
            }
        });

        rlUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chatType.equals("group")) {
                    rlUpdateGroup.setVisibility(View.VISIBLE);
                    Picasso.with(ivGroupImage.getContext()).load(groupImage).into(ivGroupImage);
                    etGroupName.setText(receiverName);
                }
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlUpdateGroup.setVisibility(View.GONE);
            }
        });

        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivGroupImage.setEnabled(true);
                etGroupName.setEnabled(true);
                tvEdit.setVisibility(View.GONE);
                tvUpdate.setVisibility(View.VISIBLE);
            }
        });

        ivGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatActivity.this);
                    alertDialog.setTitle("Select your option");
                    alertDialog.setPositiveButton("Open Gallery", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            updateGroupFlag = 1;
                            Intent getIntent = new Intent(Intent.ACTION_PICK);
                            getIntent.setType("image/*");
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, PICK_GALLERY_IMAGE);
                        }
                    });
                    alertDialog.setNegativeButton("Open Camera", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            updateGroupFlag = 1;
                            Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            Date d = new Date();
                            CharSequence s = DateFormat.format("yyyyMMdd_hhmmss", d.getTime());
                            File f = new File(Environment.getExternalStorageDirectory(), "IMG_" + s.toString() + ".jpg");
                            chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            imageToUploadUri = Uri.fromFile(f);
                            startActivityForResult(chooserIntent, TAKE_CAMERA_PHOTO);
                        }
                    });
                    alertDialog.show();
                } else {
                    setPermissions();
                }
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = etGroupName.getText().toString().trim();
                ivGroupImage.setEnabled(false);
                etGroupName.setEnabled(false);
                tvUpdate.setVisibility(View.GONE);
                tvEdit.setVisibility(View.VISIBLE);
                updateGroup(chatId, groupName);
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

        if (requestCode == TAKE_CAMERA_PHOTO && resultCode == Activity.RESULT_OK) {
            if (imageToUploadUri != null) {
                Uri selectedImage = imageToUploadUri;
                sendImageName = imageToUploadUri.getPath();
                getContentResolver().notifyChange(selectedImage, null);
                ImageCompressor ic = new ImageCompressor(getApplicationContext());
                sendImageBitmap = ic.compressImage(sendImageName);
                if (sendImageBitmap != null) {
                    rlSelectedImage.setVisibility(View.VISIBLE);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                    ivSelectedImage.setImageBitmap(sendImageBitmap);
                    flagFileType = "1";
                } else {
                    Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Error while capturing Image", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == PICK_GALLERY_IMAGE && resultCode == RESULT_OK && null != data) {
            if (updateGroupFlag == 1) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String img_Decodable_Str = cursor.getString(columnIndex);
                    cursor.close();
                    sendGroupImageName = img_Decodable_Str.substring(img_Decodable_Str.lastIndexOf("/") + 1);
                    ImageCompressor ic = new ImageCompressor(getApplicationContext());
                    groupImageBitmap = ic.compressImage(img_Decodable_Str);
                    ivGroupImage.setImageBitmap(groupImageBitmap);
                    updateGroupFlag = 0;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String img_Decodable_Str = cursor.getString(columnIndex);
                    cursor.close();
                    sendImageName = img_Decodable_Str.substring(img_Decodable_Str.lastIndexOf("/") + 1);
                    rlSelectedImage.setVisibility(View.VISIBLE);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                    ImageCompressor ic = new ImageCompressor(getApplicationContext());
                    sendImageBitmap = ic.compressImage(img_Decodable_Str);
                    ivSelectedImage.setImageBitmap(sendImageBitmap);
                    flagFileType = "1";
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChatMsgs(chatId);
        imageFileList.clear();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        String str = String.valueOf(imageFileList.size());
//        Toast.makeText(this, "onPause" + str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getChatMsgs(chatId);
//        imageFileList.clear();
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
                        audioDetailsList.clear();
                        sharedAudioList.clear();
                        cAdapter.notifyDataSetChanged();
                        JSONObject jsonObject;
                        JSONArray resultArray, audiosDetailsArray, sharedAudiosArray;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.getString("flag").equals("success")) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                resultArray = result.getJSONArray("message LIst");
                                if (resultArray.length() > 0) {
                                    for (int i = 0; i < resultArray.length(); i++) {
                                        Message message = new Message();
                                        JSONObject chatJson = resultArray.getJSONObject(i);
                                        message.setId(chatJson.getString("id"));
                                        message.setSenderId(chatJson.getString("senderID"));
                                        message.setProfilePic(chatJson.getString("sender_pic"));
                                        message.setMessage(chatJson.getString("message"));
                                        message.setFileType(chatJson.getString("file_type"));
                                        message.setFile(chatJson.getString("file_url"));
                                        message.setCreatedAt(chatJson.getString("sendat"));

                                        if (!chatJson.get("Audioshared").equals(null) && !chatJson.get("Audioshared").equals("")) {
                                            audiosDetailsArray = chatJson.getJSONArray("Audioshared");
                                            if (audiosDetailsArray.length() > 0) {
                                                for (int j = 0; j < audiosDetailsArray.length(); j++) {
                                                    AudioDetails audioDetails = new AudioDetails();
                                                    JSONObject detailsJson = audiosDetailsArray.getJSONObject(j);
                                                    audioDetails.setRecordingId(detailsJson.getString("recording_id"));
                                                    audioDetails.setAddedBy(detailsJson.getString("added_by"));
                                                    audioDetails.setRecordingTopic(detailsJson.getString("recording_topic"));
                                                    audioDetails.setName(detailsJson.getString("name"));
                                                    audioDetails.setUserName(detailsJson.getString("user_name"));

                                                    if (!detailsJson.get("recordings").equals(null)) {
                                                        sharedAudiosArray = detailsJson.getJSONArray("recordings");
                                                        if (audiosDetailsArray.length() > 0) {
                                                            for (int k = 0; k < sharedAudiosArray.length(); k++) {
                                                                SharedAudios sharedAudios = new SharedAudios();
                                                                JSONObject audioJson = sharedAudiosArray.getJSONObject(k);
                                                                sharedAudios.setAddedById(audioJson.getString("added_by_id"));
                                                                sharedAudios.setUserName(audioJson.getString("user_name"));
                                                                sharedAudios.setName(audioJson.getString("name"));
                                                                sharedAudios.setProfileUrl(audioJson.getString("profile_url"));
                                                                sharedAudios.setDateAdded(audioJson.getString("date_added"));
                                                                sharedAudios.setDuration(audioJson.getString("duration"));
                                                                sharedAudios.setRecordingUrl(audioJson.getString("recording_url"));
                                                                sharedAudioList.add(sharedAudios);
                                                            }
                                                        }
                                                    }
                                                    audioDetailsList.add(audioDetails);
                                                }
                                            }
                                        }
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
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(CHAT_ID_, chat_Id);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void sendMessage(final String message, final String user_Id) {

        if (flagFileType.equals("1")) {
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, CHAT,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            String str = new String(response.data);
//                            Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                            getChatMsgs(chatId);
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
                        errorMsg = "Parse error";
                    }

                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
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

                    params.put(FILE_TYPE, "image");
                    params.put(SENDER_ID, user_Id);
                    params.put(CHAT_ID, chatId);
                    params.put(TITLE, "message");
                    params.put(MESSAGE, message);
                    params.put(IS_READ, "0");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put(FILE, new DataPart(sendImageName, AppHelper.getFileDataFromDrawable(getBaseContext(), sendImageBitmap), "image/jpeg"));
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHAT, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String str = response;
//                    Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
                    getChatMsgs(chatId);
                    SharedPreferences.Editor editor = getSharedPreferences("audioShareData", MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

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
                        errorMsg = "Parse error";
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
                    if (flagFileType.equals("2")) {
                        String recID;
                        SharedPreferences audioShareData = getApplicationContext().getSharedPreferences("audioShareData", MODE_PRIVATE);
                        recID = audioShareData.getString("recID", null);
                        params.put(FILE, recID);
                        params.put(FILE_TYPE, "station");

                    } else if (flagFileType.equals("3")) {
                        params.put(FILE_TYPE, "admin_melody");
                    }
                    params.put(SENDER_ID, user_Id);
                    params.put(CHAT_ID, chatId);
                    params.put(TITLE, "message");
                    params.put(MESSAGE, message);
                    params.put(IS_READ, "0");
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }

    public void getGalleryImages() {

        if (isExternalStorageRemovable()) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String ExternalStoragePath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
                File targetDirector = new File(ExternalStoragePath);

                int length;
                String file, filename;
                if (targetDirector.listFiles() != null) {
                    File[] files = targetDirector.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.toLowerCase().endsWith(".jpg");
                        }
                    });

                    length = files.length;
                    if (files.length > 9) {
                        for (int i = length - 1; i >= length - 10; i--) {
                            RecentImagesModel rim = new RecentImagesModel();
                            file = files[i].toString();
                            rim.setFilepath(file);
                            filename = file.substring(file.lastIndexOf("/") + 1);
                            rim.setName(filename);
                            fileInfo.add(rim);
                        }
                    } else {
                        for (int i = length - 1; i >= 0; i--) {
                            RecentImagesModel rim = new RecentImagesModel();
                            file = files[i].toString();
                            rim.setFilepath(file);
                            filename = file.substring(file.lastIndexOf("/") + 1);
                            rim.setName(filename);
                            fileInfo.add(rim);
                        }
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to read External storage", Toast.LENGTH_LONG).show();
                }
            }
        }
        if (isExternalStorageEmulated()) {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                String ExternalStorageDirectoryPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String InternalStoragePath = ExternalStorageDirectoryPath + "/DCIM/Camera/";
                File targetDirector = new File(InternalStoragePath);

                int length;
                String file, filename;
                if (targetDirector.listFiles() != null) {
                    File[] files = targetDirector.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.toLowerCase().endsWith(".jpg");
                        }
                    });

                    length = files.length;
                    if (files.length > 9) {
                        for (int i = length - 1; i >= length - 10; i--) {
                            RecentImagesModel rim = new RecentImagesModel();
                            file = files[i].toString();
                            rim.setFilepath(file);
                            filename = file.substring(file.lastIndexOf("/") + 1);
                            rim.setName(filename);
                            fileInfo.add(rim);
                        }
                    } else {
                        for (int i = length - 1; i >= 0; i--) {
                            RecentImagesModel rim = new RecentImagesModel();
                            file = files[i].toString();
                            rim.setFilepath(file);
                            filename = file.substring(file.lastIndexOf("/") + 1);
                            rim.setName(filename);
                            fileInfo.add(rim);
                        }
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to read Internal storage", Toast.LENGTH_LONG).show();
                }
            } else {
//                Toast.makeText(getApplicationContext(), "Failed to read device storage", Toast.LENGTH_LONG).show();
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
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_STORAGE);
            } else {
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_READ_STORAGE:
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

    public void updateGroup(final String chatingId, final String groupName) {
        if (groupImageBitmap == null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_GROUP, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String str = response;
//                            Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
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
                        errorMsg = "Parse error";
                    } else {
                        errorMsg = error.toString();
                    }

                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("groupName", groupName);
                    params.put("chatID", chatingId);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
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
//                            Toast.makeText(ChatActivity.this, str + "chat api response", Toast.LENGTH_SHORT).show();
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
                        errorMsg = "Parse error";
                    } else {
                        errorMsg = error.toString();
                    }

                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.d("Error", errorMsg);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("groupName", groupName);
                    params.put("chatID", chatingId);
                    params.put(AuthenticationKeyName, AuthenticationKeyValue);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("groupPic", new DataPart(sendGroupImageName, AppHelper.getFileDataFromDrawable(getBaseContext(), groupImageBitmap), "image/jpeg"));
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }
}