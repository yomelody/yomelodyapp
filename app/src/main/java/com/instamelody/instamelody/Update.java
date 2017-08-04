package com.instamelody.instamelody;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
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

import static com.instamelody.instamelody.utils.Const.ServiceType.REGISTER;
import static com.instamelody.instamelody.utils.Const.ServiceType.UPDATEPROFILE;
import static com.instamelody.instamelody.utils.Const.ServiceType.UPLOAD_FILE;

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
    String userId, firstName, lastName, userNameLogin, profilePicLogin, dob, mobile, email, date;
    CircleImageView userProfileImageUpdate;
    int statusNormal;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private final int requestCode = 20;
    String password1;
    DatePickerDialog dpd;
    String formatedDate;
    int day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        SharedPreferences loginSharedPref = this.getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userId = loginSharedPref.getString("userId", null);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        email = loginSharedPref.getString("emailFinal", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        dob = loginSharedPref.getString("dob", null);
        mobile = loginSharedPref.getString("mobile", null);
        statusNormal = loginSharedPref.getInt("status", 0);

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


        etuFirstName.setText(firstName);
        etuLastName.setText(lastName);
        etuEmailUpdate.setText(email);
        etuUsername.setText(userNameLogin);
        etuPhone.setText(mobile);
        tvDobUpdate.setText(dob);
        Picasso.with(Update.this).load(profilePicLogin).into(userProfileImageUpdate);


        View v = findViewById(R.id.activity_update);
        Drawable d = v.getBackground();
        d.setAlpha(200);


        tvDoneUpdate = (TextView) findViewById(R.id.tvDoneUpdate);
        tvDoneUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Update.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

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
                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, requestCode);
                        }
                    });
                    alertDialog.show();
                }
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
                etuUsername.setEnabled(true);
                etuPassWord.setEnabled(true);
                etuConfirmPassWord.setEnabled(true);
                etuPhone.setEnabled(true);
                tvDobUpdate.setEnabled(true);
                etuPassWord.setHint("New Password");
                etuConfirmPassWord.setHint("Confirm Password");
                userProfileImageUpdate.setEnabled(true);

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
                if (!etuConfirmPassWord.getText().toString().equals(etuPassWord.getText().toString())) {
                    // Toast.makeText(SignUpActivity.this, "please check your confirm password .", Toast.LENGTH_SHORT).show();
                    errorConfirmPassUpdate.setVisibility(View.VISIBLE);
                    errorConfirmPassUpdate.setText("Password didn't match!");
                } else {
                    updateData();
                    if (userProfileImageUpdate != null) {
                        updateImage();
                    }
                }
            }
        });
    }

    private void updateData() {
        final String firstname = etuFirstName.getText().toString().trim();
        final String lastname = etuLastName.getText().toString().trim();
        final String username = etuUsername.getText().toString().trim();
        final String password = etuPassWord.getText().toString().trim();
        if (password.equals("")) {
            password1 = password;
        } else {
            password1 = etuPassWord.getText().toString().trim();
        }
//        final String dob = tvDob.getText().toString().trim();
        final String dob = tvDobUpdate.getText().toString().trim();
        String a = dob.replaceAll(" ", "");
        try {
            String b = a.substring(a.indexOf(":"), a.length());
            date = b.replace(":", "").replace("|", "/");
        } catch (StringIndexOutOfBoundsException siobe) {
            System.out.println("invalid input");
        }

//        final String date = b.replace(":", "").replace("|", "/");
        final String phone = etuPhone.getText().toString().trim();
        final String usertype = "USER";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATEPROFILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response.toString();
                        Toast.makeText(Update.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                String msg = jsonObject.getString("msg");
//                                Toast.makeText(Update.this, "" + msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errormsg = error.toString();
                        Log.d("Error", errormsg);
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
                params.put(KEY_PASSWORD, password1);
                params.put(KEY_DOB, date);
                params.put(KEY_PHONE, phone);
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
                Toast.makeText(getApplicationContext(), "Profile Picture Updated", Toast.LENGTH_SHORT).show();
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
                params.put(USER_ID, userId);
                params.put(FILE_TYPE, String.valueOf(1));

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put(FILE1, new DataPart("img.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), userProfileImageUpdate.getDrawable()), "image/jpeg"));
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
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            userProfileImageUpdate.setImageBitmap(bitmap);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && null != data) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userProfileImageUpdate.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
