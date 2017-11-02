package com.instamelody.instamelody;

import android.*;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.DateValidator;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.Multipart;

import static android.R.attr.id;
import static android.R.attr.maxDate;
import static android.R.attr.minDate;
import static android.R.attr.negativeButtonText;
import static com.facebook.internal.FacebookRequestErrorClassification.KEY_NAME;
import static com.instamelody.instamelody.utils.Const.SHARED_PREF;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.REGISTER;
import static com.instamelody.instamelody.utils.Const.ServiceType.UPLOAD_FILE;

/**
 * Created by Saurabh Singh on 12/26/2016.
 */

public class SignUpActivity extends AppCompatActivity {


    //String KEY = "key";
    String KEY_FNAME = "f_name";
    String KEY_LNAME = "l_name";
    String KEY_EMAIL = "email";
    String KEY_USERNAME = "username";
    String KEY_PASSWORD = "password";
    String KEY_DOB = "dob";
    String KEY_PHONE = "phone";
    String KEY_USER_TYPE = "usertype";
    String KEY_APPID = "appid";
    String KEY_DEVICE_TOKEN_SIGN_UP = "device_token";
    String KEY_DEVICE_TYPE = "device_type";
    private int PICK_IMAGE_REQUEST = 1;
    private String FILE_TYPE = "file_type";
    private String FILE1 = "file1";
    private String USER_ID = "user_id";
    private String KEY_IMAGE = "encodedImage";
    private Bitmap bitmap;
    private final int requestCode = 20;
    private static final int PERMISSION_READ_STORAGE = 201;
    private static final int PERMISSION_CAMERA = 202;

    public String profilepic2;
    String DeviceToken;

    String flag, id, username1, fname, lname, jemail, coverpic, followers, fans, records, dob1;
    EditText etfirstname, etlastname, etemail,
            etusername, etpassword, etphone, etConfirmPassWord, etDOB;
    public View customView;
    RecyclerView recyclerView;
    LayoutInflater inflater;
    Button btnsignup, btnClearFN, btnClearLN, btnClearEmail, btnClearUserName, btnClearPass, btnClearDOB, btnClearPhone, btnClearConfirmPass;
    TextView tvsignin, tvDone, errorFname, errorEmail, errorUserName, errorPassword, errorConfirmPass, errorDOB, errorPhone, tvDob;
    CircleImageView userProfileImage;

    String fbName, fbLastName, fbUserName, fbId, fbEmail, Name, userName, profilePic, fbBirthDay;
    int statusFb, statusTwitter;
    String text = "";
    DatePickerDialog dpd;
    String formatedDate;
    String date;
    int day, month, year;
    ProgressDialog progressDialog;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mActivity=SignUpActivity.this;
        progressDialog = new ProgressDialog(SignUpActivity.this);
        etfirstname = (EditText) findViewById(R.id.etFirstName);
        etlastname = (EditText) findViewById(R.id.etLastName);
        etemail = (EditText) findViewById(R.id.etEmail);
        etusername = (EditText) findViewById(R.id.etUsername);
        etpassword = (EditText) findViewById(R.id.etPassWord);
        etConfirmPassWord = (EditText) findViewById(R.id.etConfirmPassWord);
        tvDob = (TextView) findViewById(R.id.tvDob);
        //etDOB = (EditText) findViewById(R.id.tvDob);
        etphone = (EditText) findViewById(R.id.etPhone);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvsignin = (TextView) findViewById(R.id.tvSignIn);
        btnsignup = (Button) findViewById(R.id.buttonSignUp);
        userProfileImage = (CircleImageView) findViewById(R.id.userProfileImage);
        btnClearFN = (Button) findViewById(R.id.btnClearFN);
        btnClearLN = (Button) findViewById(R.id.btnClearLN);
        btnClearEmail = (Button) findViewById(R.id.btnClearEmail);
        btnClearUserName = (Button) findViewById(R.id.btnClearUserName);
        btnClearPass = (Button) findViewById(R.id.btnClearPass);
        btnClearConfirmPass = (Button) findViewById(R.id.btnClearConfirmPass);
        btnClearDOB = (Button) findViewById(R.id.btnClearDOB);
        btnClearPhone = (Button) findViewById(R.id.btnClearPhone);
        inflater = LayoutInflater.from(SignUpActivity.this);
        customView = inflater.inflate(R.layout.custom_dialog_view, null);
        errorEmail = (TextView) findViewById(R.id.errorEmail);
        errorPassword = (TextView) findViewById(R.id.errorPassword);
        errorFname = (TextView) findViewById(R.id.errorFname);
        errorUserName = (TextView) findViewById(R.id.errorUname);
        errorConfirmPass = (TextView) findViewById(R.id.errorConfirmPass);
        errorDOB = (TextView) findViewById(R.id.errorDOB);
        errorPhone = (TextView) findViewById(R.id.errorPhone);

        SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
        fbName = fbPref.getString("FbName", null);
        fbLastName = fbPref.getString("FbLastName", null);
        fbUserName = fbPref.getString("userName", null);
        fbId = fbPref.getString("fbId", null);
        fbEmail = fbPref.getString("FbEmail", null);
        fbBirthDay = fbPref.getString("Birthday", null);
        statusFb = fbPref.getInt("status", 0);

        if (statusFb == 1) {
            etfirstname.setText(fbName);
            etlastname.setText(fbLastName);
            etusername.setText(fbUserName);
            etDOB.setText(fbBirthDay);
            etemail.setText(fbEmail);
        }
        if (fbId != null) {
            Picasso.with(SignUpActivity.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImage);
        }

        SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
        Name = twitterPref.getString("Name", null);
        userName = twitterPref.getString("userName", null);
        profilePic = twitterPref.getString("ProfilePic", null);
        statusTwitter = twitterPref.getInt("status", 0);

        if (statusTwitter == 1) {
            etfirstname.setText(Name);
            etusername.setText(userName);
        }

        if (profilePic != null) {
            Picasso.with(SignUpActivity.this).load(profilePic).into(userProfileImage);
        }

        SharedPreferences fcmPref = getApplicationContext().getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        DeviceToken = fcmPref.getString("regId", null);


        tvDob.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
        });

        tvDob.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View v) {
                                         final Calendar c = Calendar.getInstance();
                                         final DatePickerDialog dpd = new DatePickerDialog(SignUpActivity.this,
                                                 new DatePickerDialog.OnDateSetListener() {

                                                     @Override
                                                     public void onDateSet(DatePicker view, int year,
                                                                           int monthOfYear, int dayOfMonth) {

                                                         tvDob.setText("Date of Birth:    " + dayOfMonth + " | " + (monthOfYear + 1) + " | " + year);
                                                     }
                                                 }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                                         dpd.getDatePicker().setMaxDate(new Date().getTime());
                                         dpd.show();
                                         etphone.setFocusableInTouchMode(true);
                                         etphone.requestFocus();
//                                         alertDialog.show();

                                         day = dpd.getDatePicker().getDayOfMonth();
                                         month = dpd.getDatePicker().getMonth();
                                         year = dpd.getDatePicker().getYear();


                                         SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                         formatedDate = sdf.format(new Date(year, month, day));
                                     }
                                 }
        );


        etfirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorFname.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etemail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorEmail.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorUserName.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorPassword.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfirmPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorConfirmPass.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etConfirmPassWord.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    final Calendar c = Calendar.getInstance();

                    dpd = new DatePickerDialog(SignUpActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                                    tvDob.setText("Date of Birth:    " + dayOfMonth + " | " + (monthOfYear + 1) + " | " + year);


                                }
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                    dpd.getDatePicker().setMaxDate(new Date().getTime());
                    dpd.show();

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    etphone.setFocusableInTouchMode(true);
                    etphone.requestFocus();

                    return true;
                }
                return false;
            }
        });


        etConfirmPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorDOB.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvDob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorDOB.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorPhone.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnsignup.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             if (validateForm()) {
                                                 signUp();
                                             }
                                         }
                                     }

        );

        tvDone.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View view) {
                                          Intent intent = new Intent(mActivity, HomeActivity.class);
                                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                          startActivity(intent);
                                      }
                                  }

        );

        tvsignin.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                            startActivity(i);
                                        }
                                    }

        );

        btnClearFN.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {
                                              etfirstname.getText().clear();
                                              errorFname.setVisibility(View.GONE);
                                          }
                                      }

        );

        btnClearLN.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View view) {
                                              etlastname.getText().clear();
                                          }
                                      }
        );

        btnClearEmail.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View view) {
                                                 etemail.getText().clear();
                                                 errorEmail.setVisibility(View.GONE);
                                             }
                                         }
        );

        btnClearUserName.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View view) {
                                                    etusername.getText().clear();
                                                    errorUserName.setVisibility(View.GONE);
                                                }
                                            }
        );

        btnClearPass.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                etpassword.getText().clear();
                                                errorPassword.setVisibility(View.GONE);
                                            }
                                        }
        );

        btnClearConfirmPass.setOnClickListener(new View.OnClickListener()

                                               {
                                                   @Override
                                                   public void onClick(View v) {
                                                       etConfirmPassWord.getText().clear();
                                                       errorConfirmPass.setVisibility(View.GONE);
                                                   }
                                               }
        );

        btnClearDOB.setOnClickListener(new View.OnClickListener()

                                       {
                                           @Override
                                           public void onClick(View view) {
                                               tvDob.setText("Date of Birth:    " + "Day  |" + "  Month  |" + "  Year  ");
                                               errorDOB.setVisibility(View.GONE);
                                           }
                                       }
        );

        btnClearPhone.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View view) {
                                                 etphone.getText().clear();
                                                 errorPhone.setVisibility(View.GONE);
                                             }
                                         }
        );

        userProfileImage.buildDrawingCache();
        Bitmap bitmap = userProfileImage.getDrawingCache();
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.putExtra("BitmapImage", bitmap);
        userProfileImage.setOnClickListener(new View.OnClickListener()

                                            {
                                                @Override
                                                public void onClick(View view) {
                                                    if (view == userProfileImage) {
                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
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
                                                                    startActivityForResult(photoCaptureIntent, requestCode);
                                                                } catch (Throwable e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }
                                                        });
                                                        alertDialog.show();
                                                        setPermissions();
                                                    }
                                                }
                                            }


        );
    }

    public void setPermissions() {
        if (ContextCompat.checkSelfPermission(SignUpActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_STORAGE);
            }
        } else if (ContextCompat.checkSelfPermission(SignUpActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this, android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            } else {
                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            SharedPreferences.Editor tEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
            tEditor.clear();
            tEditor.commit();
            SharedPreferences.Editor fbeditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
            fbeditor.clear();
            fbeditor.commit();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            userProfileImage.setImageBitmap(bitmap);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validateForm() {
        boolean chk = true;
        if (etfirstname.getText().toString().trim().equals("")) {
            errorFname.setVisibility(View.VISIBLE);
            errorFname.setText("required");
            chk = false;
        }

        if (etemail.getText().toString().trim().equals("")) {
            errorEmail.setVisibility(View.VISIBLE);
            errorEmail.setText("required");
            chk = false;
        }
        if (etusername.getText().toString().trim().equals("")) {
            errorUserName.setVisibility(View.VISIBLE);
            errorUserName.setText("required");
            chk = false;
        }
        if (etpassword.getText().toString().trim().equals("")) {
            errorPassword.setVisibility(View.VISIBLE);
            errorPassword.setText("required");
            chk = false;
        }
        if (etConfirmPassWord.getText().toString().trim().equals("")) {
            errorConfirmPass.setVisibility(View.VISIBLE);
            errorConfirmPass.setText("required");
            chk = false;
        }
        if (tvDob.getText().toString().trim().equals("")) {
            errorDOB.setVisibility(View.VISIBLE);
            errorDOB.setText("required");
            chk = false;

        }
        if (String.valueOf(day) == ("") && String.valueOf(month) == ("") && String.valueOf(year) == ("")) {
            errorDOB.setVisibility(View.VISIBLE);
            errorDOB.setText("required");
            chk = false;
        }
        if (etphone.getText().toString().trim().equals("")) {
            errorPhone.setVisibility(View.VISIBLE);
            errorPhone.setText("required");
            chk = false;
        } else if (etpassword.getText().toString().trim().length() < 8) {
            errorPassword.setVisibility(View.VISIBLE);
            errorPassword.setText("At least 8 characters");
            chk = false;
        } else if (!isValidMobile(etphone.getText().toString().trim())) {
            Toast.makeText(SignUpActivity.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
            errorPhone.setVisibility(View.VISIBLE);
            errorPhone.setText("Atleast 10 digits");
            chk = false;

        } else if (!isValidMail(etemail.getText().toString().trim())) {
            errorEmail.setVisibility(View.VISIBLE);
            errorEmail.setText(" Valid email id. required.");
            chk = false;
        } else if (!etConfirmPassWord.getText().toString().equals(etpassword.getText().toString())) {
            // Toast.makeText(SignUpActivity.this, "please check your confirm password .", Toast.LENGTH_SHORT).show();
            errorConfirmPass.setVisibility(View.VISIBLE);
            errorConfirmPass.setText("Password didn't match!");
            chk = false;
        }
        return chk;
    }

    private void uploadImage(final String n) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_FILE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
//                Toast.makeText(getApplicationContext(), resultResponse, Toast.LENGTH_LONG).show();
                try {

                    JSONObject uploadImgRspns = new JSONObject(resultResponse);
                    String flag = uploadImgRspns.getString("flag");
                    JSONObject responseObject = uploadImgRspns.getJSONObject("response");
                    String picObject = responseObject.getString("profilepic");
                    profilepic2 = picObject;

                    Log.d("gamma", profilepic2);
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("uploading image response", MODE_PRIVATE).edit();
                    editor.putString("picGallery", profilepic2);
                    editor.putString("userName1", username1);
                    editor.putString("fname", fname);
                    editor.putString("lname", lname);
                    editor.putString("jemail", jemail);
                    editor.putString("id", id);
                    editor.commit();
//                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("return message", resultResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(USER_ID, n);
                params.put(FILE_TYPE, String.valueOf(1));
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put(FILE1, new DataPart("img.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), userProfileImage.getDrawable()), "image/jpeg"));
                return params;
            }
        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private void signUp() {
        final String firstname = etfirstname.getText().toString().trim();
        final String lastname = etlastname.getText().toString().trim();
        final String email = etemail.getText().toString().trim();
        final String username = etusername.getText().toString().trim();
        final String password = etpassword.getText().toString().trim();
//        final String dob = tvDob.getText().toString().trim();
        final String dob = tvDob.getText().toString().trim();
        String a = dob.replaceAll(" ", "");
        try {
            String b = a.substring(a.indexOf(":"), a.length());
            date = b.replace(":", "").replace("|", "/");
        } catch (StringIndexOutOfBoundsException siobe) {
            System.out.println("invalid input");
        }

//        final String date = b.replace(":", "").replace("|", "/");
        final String phone = etphone.getText().toString().trim();
        final String usertype = "USER";

        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppHelper.sop("response======" + response);
                        String successmsg = response;
//                        Toast.makeText(SignUpActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
                            if (flag.equals("unsuccess")) {
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(SignUpActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                            }
                            JSONObject rspns = jsonObject.getJSONObject("response");
                            id = rspns.getString("id");
                            username1 = rspns.getString("username");
                            fname = rspns.getString("f_name");
                            lname = rspns.getString("l_name");
                            jemail = rspns.getString("email");
                            /*profilepic2 = rspns.getString("profilepic");*/
                            coverpic = rspns.getString("coverpic");
                            followers = rspns.getString("followers");
                            fans = rspns.getString("fans");
                            records = rspns.getString("records");
                            dob1 = rspns.getString("dob");
                            if (flag.equals("success")) {
                                Toast.makeText(SignUpActivity.this, "Registration Success", Toast.LENGTH_SHORT).show();
                                uploadImage(id);
                                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(SignUpActivity.this, response, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String error = e.toString();
//                            Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();

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
//                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
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
                // params.put(KEY,"admin@123");
                params.put(KEY_FNAME, firstname);
                params.put(KEY_LNAME, lastname);
                params.put(KEY_EMAIL, email);
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_DOB, date);
                params.put(KEY_PHONE, phone);
                params.put(KEY_DEVICE_TOKEN_SIGN_UP, DeviceToken);
                params.put(KEY_DEVICE_TYPE, "android");
                params.put(KEY_USER_TYPE, "1");
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params==" + params + "\n URL==" + REGISTER);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", text)) {
            if (phone.length() < 10 || phone.length() > 15) {
//               if (phone.length() != 10) {
                check = false;
                //errorPhone.setError("Not Valid Number");
//                }
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }
}
