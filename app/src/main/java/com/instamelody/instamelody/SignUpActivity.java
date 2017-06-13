package com.instamelody.instamelody;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.instamelody.instamelody.utils.AppHelper;
import com.instamelody.instamelody.utils.VolleyMultipartRequest;
import com.instamelody.instamelody.utils.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Saurabh Singh on 12/26/2016.
 */

public class SignUpActivity extends AppCompatActivity {

    String REGISTER_URL = "http://35.165.96.167/api/registration.php";
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
    String KEY_DEVICE_TYPE = "device_type";
    private int PICK_IMAGE_REQUEST = 1;
    private String UPLOAD_URL = "http://35.165.96.167/api/uploadfile.php";
    private String FILE_TYPE = "file_type";
    private String FILE1 = "file1";
    private String USER_ID = "user_id";
    private String KEY_IMAGE = "encodedImage";
    private Bitmap bitmap;
    private final int requestCode = 20;

    public String profilepic2;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

        btnsignup.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View view) {
                                             if (etfirstname.getText().toString().trim().equals("")) {
                                                 errorFname.setVisibility(View.VISIBLE);
                                                 errorFname.setText("required");
                                             }

                                             if (etemail.getText().toString().trim().equals("")) {
                                                 errorEmail.setVisibility(View.VISIBLE);
                                                 errorEmail.setText("required");
                                             }
                                             if (etusername.getText().toString().trim().equals("")) {
                                                 errorUserName.setVisibility(View.VISIBLE);
                                                 errorUserName.setText("required");
                                             }
                                             if (etpassword.getText().toString().trim().equals("")) {
                                                 errorPassword.setVisibility(View.VISIBLE);
                                                 errorPassword.setText("required");
                                             }
                                             if (etConfirmPassWord.getText().toString().trim().equals("")) {
                                                 errorConfirmPass.setVisibility(View.VISIBLE);
                                                 errorConfirmPass.setText("required");
                                             }
                                             if (tvDob.getText().toString().trim().equals("")) {
                                                 errorDOB.setVisibility(View.VISIBLE);
                                                 errorDOB.setText("required");

                                             }
                                             if (etphone.getText().toString().trim().equals("")) {
                                                 errorPhone.setVisibility(View.VISIBLE);
                                                 errorPhone.setText("required");
                                             } else if (etpassword.getText().toString().trim().length() < 8) {
                                                 errorPassword.setVisibility(View.VISIBLE);
                                                 errorPassword.setText("At least 8 characters");
                                             } else if (!isValidMobile(etphone.getText().toString().trim())) {
                                                 Toast.makeText(SignUpActivity.this, "Invalid phone number.", Toast.LENGTH_SHORT).show();
                                                 errorPhone.setVisibility(View.VISIBLE);
                                                 errorPhone.setText("Atleast 10 digits");

                                             } else if (!isValidMail(etemail.getText().toString().trim())) {
                                                 errorEmail.setVisibility(View.VISIBLE);
                                                 errorEmail.setText(" Valid email id. required.");
                                             } else if (!etConfirmPassWord.getText().toString().equals(etpassword.getText().toString())) {
                                                 // Toast.makeText(SignUpActivity.this, "please check your confirm password .", Toast.LENGTH_SHORT).show();
                                                 errorConfirmPass.setVisibility(View.VISIBLE);
                                                 errorConfirmPass.setText("Password didn't match!");
                                             } else {
                                                 signUp();
                                             }
                                         }
                                     }

        );

        tvDone.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View view) {

                                          Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
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



        /*userProfileImage.setOnClickListener(new View.OnClickListener()

                                    {
                                        @Override
                                        public void onClick(View v) {





                                            recyclerView = (RecyclerView)findViewById(R.id.recyclerViewDialogBox);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                                            RelativeLayout rlBtnPhotoLibrary = (RelativeLayout) customView.findViewById(R.id.rlBtnPhotoLibrary);
                                            RelativeLayout rlBtnTakePhotoOrVideo = (RelativeLayout) customView.findViewById(R.id.rlBtnTakeFromLibrary);
                                            RelativeLayout rlBtnCancel = (RelativeLayout) customView.findViewById(R.id.rlBtnCancel);

                                            final Dialog alertDialog = new Dialog(SignUpActivity.this);
                                            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            alertDialog.setContentView(customView);
                                            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
                                            wmlp.width = android.view.WindowManager.LayoutParams.MATCH_PARENT;
                                            wmlp.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
                                            alertDialog.show();

                                            rlBtnPhotoLibrary.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent();
                                                    intent.setType("image");
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
                                                }
                                            });

                                            rlBtnCancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog.cancel();
                                                }
                                            });
                                        }
                                    }
        );*/

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
                                                                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                                startActivityForResult(photoCaptureIntent, requestCode);
                                                            }
                                                        });
                                                        alertDialog.show();
                                                    }
                                                }
                                            }

        );
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

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(this, "" + requestCode, Toast.LENGTH_SHORT).show();
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
           *//* Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            userProfileImage.setImageBitmap(bitmap);*//*


        }
    }*/

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

    /*public String getStringImage(Bitmap bmp){
        bitmap = ((BitmapDrawable) userProfileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/

    private void uploadImage(final String n) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPLOAD_URL, new Response.Listener<NetworkResponse>() {
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

                    // JSONObject uploadImgRspns = new JSONObject(resultResponse);
                    //  String flag = uploadImgRspns.getString("flag");
                    //   JSONObject imgrespons = uploadImgRspns.getJSONObject("response");
                    // profilepic2 = "http://" + imgrespons.getString("profilepic");
                    //profilepic2 = imgrespons.getString("profilepic");
                    //   profilepic2 = imgrespons.getJSONObject("").getString("profilepic");
//                    profilepic2 = alpha.getJSONObject("profilepic").get("profilepic");

                    /*profilepic2 = profilePic.getString("profilepic");*/
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
        final String dob = tvDob.getText().toString().trim();
        String a = dob.replaceAll(" ", "");
        String b = a.substring(a.indexOf(":"), a.length());
        final String date = b.replace(":", "").replace("|", "/");
        final String phone = etphone.getText().toString().trim();
        final String usertype = "USER";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String successmsg = response;
//                        Toast.makeText(SignUpActivity.this, "Registeration Successful", Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(successmsg);
                            flag = jsonObject.getString("flag");
//                            if (flag.equals("unsuccess")) {
//                            }
//                            JSONObject rspns = jsonObject.getJSONObject("response");
//                            id = rspns.getString("id");
//                            username1 = rspns.getString("username");
//                            fname = rspns.getString("f_name");
//                            lname = rspns.getString("l_name");
//                            jemail = rspns.getString("email");
//                            /*profilepic2 = rspns.getString("profilepic");*/
//                            coverpic = rspns.getString("coverpic");
//                            followers = rspns.getString("followers");
//                            fans = rspns.getString("fans");
//                            records = rspns.getString("records");
//                            dob1 = rspns.getString("dob");
//                            deviceType = rspns.getString("device_type");
                            if (flag.equals("success")) {
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
                                uploadImage(id);
                                Toast.makeText(SignUpActivity.this, "Registeration successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(i);
                            } else {
//                                Toast.makeText(SignUpActivity.this, response, Toast.LENGTH_SHORT).show();
//                                Toast.makeText(SignUpActivity.this, "Registeration unsuccessful", Toast.LENGTH_SHORT).show();
                                String msg = jsonObject.getString("msg");
                                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            String error = e.toString();
//                            Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
                            Log.d("error", error);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(SignUpActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                        String errormsg = error.toString();
//                        Log.d("Error", errormsg);

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
                // params.put(KEY,"admin@123");
                params.put(KEY_FNAME, firstname);
                params.put(KEY_LNAME, lastname);
                params.put(KEY_EMAIL, email);
                params.put(KEY_USERNAME, username);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_DOB, dob);
                params.put(KEY_PHONE, phone);
                params.put(KEY_DOB, date);

                params.put(KEY_DEVICE_TYPE, "android");
                params.put(KEY_USER_TYPE, "1");
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
