package com.yomelody;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.yomelody.utils.AppHelper;
import com.yomelody.utils.VolleyMultipartRequest;
import com.yomelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.UPDATEPROFILE;
import static com.yomelody.utils.Const.ServiceType.UPLOAD_FILE;

public class Update extends AppCompatActivity {

    String KEY_FNAME = " fname";
    String KEY_LNAME = "lname";
    String KEY_EMAIL = "email";
    String KEY_USERNAME = "username";
    String KEY_PASSWORD = "password";
    String KEY_DOB = "dob";
    String KEY_PHONE = "phone";
    private String FILE_TYPE = "file_type";
    private String FILE1 = "file1";
    private String USER_ID = "user_id";

    EditText etuFirstName, etuLastName, etuEmailUpdate, etuUsername, etuPassWord, etuConfirmPassWord, etuPhone;
    Button buttonEditProfile, buttonUpdate, btnClearFNUpdate, btnClearLNUpdate, btnClearUserNameUpdate,
            btnClearPassUpdate, btnClearConfirmPassUpdate, btnClearDOBUpdate, btnClearPhoneUpdate;
    TextView tvDoneUpdate, errorFnameUpdate, errorUnameUpdate, errorPasswordUpdate, errorConfirmPassUpdate,
            errorDOBUpdate, tvDobUpdate, errorPhoneUpdate;
    String userId, firstName, lastName, userNameLogin, profilePicLogin, dob, mobile, email, date, userIdNormal, emailNormal;
    String userIdTwitter, firstNameTwitter, lastNameTwitter, emailFinalTwitter, profilePicTwitter, userNameTwitter;
    String userIdFb, firstNameFb, lastNameFb, emailFinalFb, profilePicFb, userNameFb;
    CircleImageView userProfileImageUpdate;
    int statusNormal;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private final int requestCode = 20;
    String password1;
    DatePickerDialog dpd;
    String formatedDate;
    String passwordNil = "";
    int day, month, year;
    int userIdUpdate = 0;
    String finalDate = "";
    public static ProgressDialog progressDialog;
    Activity mActivity;
    private final int PERMISSION_READ_STORAGE = 241;
    private final int PERMISSION_CAMERA = 262;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mActivity = Update.this;

        progressDialog = new ProgressDialog(Update.this);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        SharedPreferences profileEditor = getApplicationContext().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);

        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userIdNormal = loginSharedPref.getString("userId", null);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        emailNormal = loginSharedPref.getString("emailFinal", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        dob = loginSharedPref.getString("dob", null);
        mobile = loginSharedPref.getString("mobile", null);
        statusNormal = loginSharedPref.getInt("status", 0);

        SharedPreferences twitterEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = twitterEditor.getString("userId", null);
        firstNameTwitter = twitterEditor.getString("firstName", null);
        lastNameTwitter = twitterEditor.getString("lastName", null);
        userNameTwitter = twitterEditor.getString("userName", null);
        emailFinalTwitter = twitterEditor.getString("emailFinal", null);
        profilePicTwitter = twitterEditor.getString("profilePic", null);


        SharedPreferences fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);
        userIdFb = fbEditor.getString("userId", null);
        firstNameFb = fbEditor.getString("firstName", null);
        lastNameFb = fbEditor.getString("lastName", null);
        emailFinalFb = fbEditor.getString("emailFinal", null);
        profilePicFb = fbEditor.getString("profilePic", null);
        userNameFb = fbEditor.getString("userName", null);


        if (userIdNormal != null) {
            userId = userIdNormal;
        } else if (userIdFb != null) {
            userId = userIdFb;
        } else if (profileEditor.getString("updateId", null) != null) {
            userId = profileEditor.getString("updateId", null);
        } else {
            userId = userIdTwitter;
        }

        if (emailNormal != null) {
            email = emailNormal;
        } else if (emailFinalFb != null) {
            email = emailFinalFb;
        } else if (profileEditor.getString("updateEmail", null) != null) {
            email = profileEditor.getString("updateEmail", null);
        } else {
            email = emailFinalTwitter;
        }


        etuFirstName = (EditText) findViewById(R.id.etuFirstName);
        etuLastName = (EditText) findViewById(R.id.etuLastName);
        etuEmailUpdate = (EditText) findViewById(R.id.etuEmailUpdate);
        etuUsername = (EditText) findViewById(R.id.etuUsername);
        etuPassWord = (EditText) findViewById(R.id.etuPassWord);
        etuConfirmPassWord = (EditText) findViewById(R.id.etuConfirmPassWord);
        etuPhone = (EditText) findViewById(R.id.etuPhone);
        userProfileImageUpdate = (CircleImageView) findViewById(R.id.userProfileImageUpdate);
        tvDobUpdate = (TextView) findViewById(R.id.tvDobUpdate);
        errorConfirmPassUpdate = (TextView) findViewById(R.id.errorConfirmPassUpdate);
        if (userIdNormal != null) {
            etuFirstName.setText(firstName);
            etuLastName.setText(lastName);
            etuEmailUpdate.setText(email);
            etuUsername.setText(userNameLogin);
            etuPhone.setText(mobile);
            tvDobUpdate.setText(dob);
            Picasso.with(Update.this).load(profilePicLogin).into(userProfileImageUpdate);
        } else if (profileEditor.getString("updateId", null) != null) {
            etuFirstName.setText(profileEditor.getString("updateFirstName", null));
            etuLastName.setText(profileEditor.getString("updateLastName", null));
            etuEmailUpdate.setText(profileEditor.getString("updateEmail", null));
            etuUsername.setText(profileEditor.getString("updateUserName", null));
            etuPhone.setText(profileEditor.getString("updateMobile", null));
            tvDobUpdate.setText(profileEditor.getString("updateDOB", null));
            finalDate = profileEditor.getString("updateDOB", null);
            if (profileImageEditor.getString("ProfileImage", null) != null) {
                Picasso.with(Update.this).load(profileImageEditor.getString("ProfileImage", null)).into(userProfileImageUpdate);
            }
        }


        if (userIdFb != null) {
            etuFirstName.setText(firstNameFb);
            etuLastName.setText(lastNameFb);
            etuEmailUpdate.setText(emailFinalFb);
            etuUsername.setText(userNameFb);
            etuPhone.setText(mobile);
            etuFirstName.setClickable(false);
            etuFirstName.setCursorVisible(false);
            etuFirstName.setFocusableInTouchMode(false);
            etuLastName.setClickable(false);
            etuLastName.setCursorVisible(false);
            etuLastName.setFocusableInTouchMode(false);
            etuUsername.setClickable(false);
            etuUsername.setCursorVisible(false);
            etuUsername.setFocusableInTouchMode(false);
            etuEmailUpdate.setClickable(false);
            etuEmailUpdate.setCursorVisible(false);
            etuEmailUpdate.setFocusableInTouchMode(false);
            SharedPreferences fbPref = this.getSharedPreferences("MyFbPref", MODE_PRIVATE);
            String fbId = fbPref.getString("fbId", null);
            Picasso.with(Update.this).load("https://graph.facebook.com/" + fbId + "/picture").into(userProfileImageUpdate);
        } else if (userIdTwitter != null) {
            etuFirstName.setText(firstNameTwitter);
            etuLastName.setText(lastNameTwitter);
            etuEmailUpdate.setText(emailFinalTwitter);
            etuUsername.setText(userNameTwitter);
            etuEmailUpdate.setClickable(false);
            etuEmailUpdate.setCursorVisible(false);
            etuEmailUpdate.setFocusableInTouchMode(false);
            etuFirstName.setClickable(false);
            etuFirstName.setCursorVisible(false);
            etuFirstName.setFocusableInTouchMode(false);
            etuLastName.setClickable(false);
            etuLastName.setCursorVisible(false);
            etuLastName.setFocusableInTouchMode(false);
            etuUsername.setClickable(false);
            etuUsername.setCursorVisible(false);
            etuUsername.setFocusableInTouchMode(false);
            SharedPreferences twitterPref = this.getSharedPreferences("TwitterPref", MODE_PRIVATE);
            String profilePic1 = twitterPref.getString("profilePic", null);
            Picasso.with(Update.this).load(profilePic1).into(userProfileImageUpdate);
        }


        View v = findViewById(R.id.activity_update);
        Drawable d = v.getBackground();
        d.setAlpha(200);


        tvDoneUpdate = (TextView) findViewById(R.id.tvDoneUpdate);
        tvDoneUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(Update.this, SettingsActivity.class);
                startActivity(intent);*/
                AppHelper.hideSoftKeyboard(mActivity);
                finish();
            }
        });


        tvDobUpdate.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               final Calendar c = Calendar.getInstance();
                                               final DatePickerDialog dpd = new DatePickerDialog(Update.this,
                                                       new DatePickerDialog.OnDateSetListener() {

                                                           @Override
                                                           public void onDateSet(DatePicker view, int year,
                                                                                 int monthOfYear, int dayOfMonth) {

                                                               tvDobUpdate.setText("Date of Birth:    " + dayOfMonth + " | " + (monthOfYear + 1) + " | " + year);
                                                           }
                                                       }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                                               dpd.getDatePicker().setMaxDate(new Date().getTime());
                                               dpd.show();
                                               etuPhone.setFocusableInTouchMode(true);
                                               etuPhone.requestFocus();
//                                         alertDialog.show();

                                               day = dpd.getDatePicker().getDayOfMonth();
                                               month = dpd.getDatePicker().getMonth();
                                               year = dpd.getDatePicker().getYear();


                                               SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                               formatedDate = sdf.format(new Date(year, month, day));
                                           }
                                       }
        );

        etuConfirmPassWord.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {

                    final Calendar c = Calendar.getInstance();

                    dpd = new DatePickerDialog(Update.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                                    tvDobUpdate.setText("Date of Birth:    " + dayOfMonth + " | " + (monthOfYear + 1) + " | " + year);


                                }
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                    dpd.getDatePicker().setMaxDate(new Date().getTime());
                    dpd.show();

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    etuPhone.setFocusableInTouchMode(true);
                    etuPhone.requestFocus();

                    return true;
                }
                return false;
            }
        });


        buttonEditProfile = (Button) findViewById(R.id.buttonEditProfile);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);

        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonUpdate.setVisibility(View.VISIBLE);
                buttonEditProfile.setVisibility(View.GONE);

                etuFirstName.setEnabled(true);
                etuLastName.setEnabled(true);
                etuUsername.setEnabled(false);
                etuPassWord.setEnabled(true);
                etuConfirmPassWord.setEnabled(true);
                etuPhone.setEnabled(true);
                tvDobUpdate.setEnabled(true);
                etuPassWord.setHint("New Password");
                etuConfirmPassWord.setHint("Confirm Password");
                userProfileImageUpdate.setEnabled(true);

                userProfileImageUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == userProfileImageUpdate) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Update.this);
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
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            alertDialog.show();
                            setPermissions();
                        }
                    }
                });

                View v1 = findViewById(R.id.activity_update);
                Drawable d = v1.getBackground();
                d.setAlpha(255);

            }
        });

        etuConfirmPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorConfirmPassUpdate.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etuFirstName.getText().toString().trim().equals("") || etuLastName.getText().toString().trim().equals("")
                        || etuUsername.getText().toString().trim().equals("")) {
                    Toast.makeText(Update.this, "please fill remaining fields which are empty", Toast.LENGTH_SHORT).show();
                } else if (!etuConfirmPassWord.getText().toString().equals(etuPassWord.getText().toString())) {
                    // Toast.makeText(SignUpActivity.this, "please check your confirm password .", Toast.LENGTH_SHORT).show();
                    errorConfirmPassUpdate.setVisibility(View.VISIBLE);
                    errorConfirmPassUpdate.setText("Password didn't match!");
                } else {
                    /*LongOperation myTask = new LongOperation();
                    myTask.execute();*/
                    updateData();

                }
            }
        });
        setformSelection();

    }

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

    private void setformSelection() {
        etuFirstName.setSelection(etuFirstName.getText().length());
        etuLastName.setSelection(etuLastName.getText().length());
        etuUsername.setSelection(etuUsername.getText().length());
        etuPhone.setSelection(etuPhone.getText().length());
    }

    private void updateData() {
        AppHelper.hideSoftKeyboard(mActivity);
        progressDialog.show();
        final String firstname = etuFirstName.getText().toString().trim();
        final String lastname = etuLastName.getText().toString().trim();
        final String username = etuUsername.getText().toString().trim();
        final String password = etuPassWord.getText().toString().trim();
        if (password.equals("")) {
            password1 = passwordNil;
        } else {
            password1 = etuPassWord.getText().toString().trim();
        }
        if (!(tvDobUpdate.getText().toString().trim().equals(dob))) {
            final String dob = tvDobUpdate.getText().toString().trim();
            String a = dob.replaceAll(" ", "");
            try {
                String b = a.substring(a.indexOf(":"), a.length());
                finalDate = b.replace(":", "").replace("|", "/");
            } catch (StringIndexOutOfBoundsException siobe) {
                System.out.println("invalid input");
            }
        } else {
            finalDate = tvDobUpdate.getText().toString().trim();
            AppHelper.sop("else=finalDate==" + tvDobUpdate.getText().toString().trim());
        }
        final String phone = etuPhone.getText().toString().trim();
        final String usertype = "USER";

        AppHelper.hideSoftKeyboard(mActivity);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATEPROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
//                        Toast.makeText(Update.this, "Login to Proceed", Toast.LENGTH_SHORT).show();
                        AppHelper.sop("response===" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            String msg = jsonObject.getString("msg");
                            String response1 = jsonObject.getString("response");
                            JSONObject jsonObjectResponse = jsonObject.getJSONObject("response");
                            String updateId = jsonObjectResponse.getString("id");
                            String updateUserName = jsonObjectResponse.getString("username");
                            String updateEmail = jsonObjectResponse.getString("email");
                            String updateFirstName = jsonObjectResponse.getString("fname");
                            String updateLastName = jsonObjectResponse.getString("lname");
                            String updateDOB = jsonObjectResponse.getString("dob");
                            String updateMobile = jsonObjectResponse.getString("mobile");
                            if (flag.equals("success")) {
                                if (userProfileImageUpdate != null) {
                                    updateImage();
                                }


                                if (jsonObjectResponse.getString("logintype").equalsIgnoreCase("1")) {
                                    //nornal login case
                                    SharedPreferences.Editor editor = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                                    editor.putString("userName", updateUserName);
                                    editor.putString("firstName", updateFirstName);
                                    editor.putString("lastName", updateLastName);
                                    editor.putString("dob", updateDOB);
                                    editor.putString("mobile", updateMobile);
                                    editor.commit();
                                } else if (jsonObjectResponse.getString("logintype").equalsIgnoreCase("2")) {
                                    //fb login case
                                    SharedPreferences.Editor fbEditor = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE).edit();
                                    fbEditor.putString("userName", updateUserName);
                                    fbEditor.putString("firstName", updateFirstName);
                                    fbEditor.putString("lastName", updateLastName);
                                    fbEditor.putString("dob", updateDOB);
                                    fbEditor.putString("mobile", updateMobile);
                                    fbEditor.commit();
                                } else {
                                    //twitter login case
                                    SharedPreferences.Editor twitterEditor = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE).edit();
                                    twitterEditor.putString("firstName", updateFirstName);
                                    twitterEditor.putString("lastName", updateLastName);
                                    twitterEditor.putString("dob", updateDOB);
                                    twitterEditor.putString("mobile", updateMobile);
                                    twitterEditor.commit();
                                }


                                SharedPreferences.Editor profileEditor = getSharedPreferences("ProfileUpdate", MODE_PRIVATE).edit();
                                profileEditor.putString("updateId", updateId);
                                profileEditor.putString("updateUserName", updateUserName);
                                profileEditor.putString("updateEmail", updateEmail);
                                profileEditor.putString("updateFirstName", updateFirstName);
                                profileEditor.putString("updateLastName", updateLastName);
                                profileEditor.putString("updateDOB", updateDOB);
                                profileEditor.putString("updateMobile", updateMobile);
                                profileEditor.apply();


                                SharedPreferences profilePref = getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
                                if (profilePref.getString("updateId", null) != null) {
                                    etuFirstName.setText(profilePref.getString("updateFirstName", null));
                                    etuLastName.setText(profilePref.getString("updateLastName", null));
                                    etuEmailUpdate.setText(profilePref.getString("updateEmail", null));
                                    etuUsername.setText(profilePref.getString("updateUserName", null));
                                    etuPhone.setText(profilePref.getString("updateMobile", null));
                                    tvDobUpdate.setText(profilePref.getString("updateDOB", null));
                                }


                                setResult(RESULT_OK);
                                LoginManager.getInstance().logOut();
                            } else {
                                Toast.makeText(Update.this, "Please try again.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                            setformSelection();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(Update.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                // params.put(KEY,"admin@123");
                params.put(USER_ID, userId);
                params.put(KEY_FNAME, firstname);
                params.put(KEY_LNAME, lastname);
                params.put(KEY_EMAIL, email);
                params.put(KEY_USERNAME, username);
                if ((etuPassWord.getText().toString().trim().equals(""))) {
                    params.put(KEY_PASSWORD, passwordNil);
                } else
                    params.put(KEY_PASSWORD, password1);
                params.put(KEY_DOB, finalDate);
                params.put(KEY_PHONE, phone);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params===" + params + "\nURL====" + UPDATEPROFILE);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateImage() {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_FILE, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                String resultResponse = new String(response.data);
//                Toast.makeText(getApplicationContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();
                AppHelper.sop("resultResponse==" + resultResponse);
                try {
                    JSONObject jsonObject = new JSONObject(resultResponse);
                    String flag = jsonObject.getString("flag");
                    if (flag.equals("success")) {
                        String response2 = jsonObject.getString("response");
                        JSONObject jsonObjectResponse = jsonObject.getJSONObject("response");
                        String updateProfileImage = jsonObjectResponse.getString("profilepic");

                        SharedPreferences.Editor editor = getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE).edit();
                        editor.putString("profilePic", updateProfileImage);
                        editor.commit();

                        SharedPreferences.Editor profileImageEditor = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE).edit();
                        profileImageEditor.putString("ProfileImage", updateProfileImage);
                        profileImageEditor.apply();

                        SharedPreferences profileImagePref = getApplicationContext().getSharedPreferences("ProfileImage", MODE_PRIVATE);
//                    profileImagePref.getString("ProfileImage", null);
                        if (profileImagePref.getString("ProfileImage", null) != null) {
                            Picasso.with(Update.this).load(profileImagePref.getString("ProfileImage", null)).into(userProfileImageUpdate);
                        }
                        Toast.makeText(Update.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Update.this, "Profile image not updated.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(USER_ID, userId);
                params.put(FILE_TYPE, String.valueOf(1));
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                AppHelper.sop("params===" + params + "\nURL===" + UPLOAD_FILE);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                /*Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                if(ts==null){
                    ts="";
                }*/
                DataPart dataPart = new DataPart("img.jpg", AppHelper.getFileDataFromDrawable(mActivity, userProfileImageUpdate.getDrawable()), "image/jpeg");
                params.put(FILE1, dataPart);
                AppHelper.sop("getByteData=getFileName===" + dataPart.getFileName() + "=" + dataPart.getType() + "=" + dataPart.getContent());
                return params;
            }


        };
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            bitmap = (Bitmap) data.getExtras().get("data");
//            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            userProfileImageUpdate.setImageBitmap(bitmap);
//            userProfileImageUpdate.setImageDrawable(mDrawable);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri filePath = null;
            filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
                //Setting the Bitmap to ImageView
                userProfileImageUpdate.setImageBitmap(bitmap);
//                userProfileImageUpdate.setImageDrawable(mDrawable);
                AppHelper.sop("filePath===" + filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
