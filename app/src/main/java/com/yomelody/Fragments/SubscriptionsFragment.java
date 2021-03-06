package com.yomelody.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.yomelody.Models.SubscriptionPackage;
import com.yomelody.Parse.ParseContents;
import com.yomelody.R;
import com.yomelody.SignInActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.yomelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.yomelody.utils.Const.ServiceType.BRAINTREE_FILES_CHECKOUT;
import static com.yomelody.utils.Const.ServiceType.BRAINTREE_FILES_CLIENT_TOKEN;
import static com.yomelody.utils.Const.ServiceType.PACKAGES;
import static com.yomelody.utils.Const.ServiceType.SUBSCRIPTION_DETAIL;

/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class SubscriptionsFragment extends Fragment {
    String KEY_SUBSCRIPTION = "key";
    String USER_ID = "user_id";
    String STATUS = "status";
    String PACKAGE_ID = "package_id";

    String STATE = "state";
    String CREATE_TIME = "create_time";
    String PAYMENT = "payment";
    String PAYPAL_ID = "id";
    String SUB_ID = "sub_id";

    String AMOUNT = "amount";
    String PAYMENT_METHOD_NOUNCE = "payment_method_nonce";
    String BRAIN_TREE_TRANSACTION_ID = "id";

    String cost;
    String nonce;
    Dialog alertDialog;
    ProgressDialog progressDialog;
    ArrayList<SubscriptionPackage> subscriptionPackageArrayList = new ArrayList<>();
    View rootView;
    public static TextView tvFreemium, descFreeLayers, tvPriceFree, tvStandard, tvStandardLayers, priceStandard, tvPremium,
            tvPremiumLayers, pricePremium, tvProducer, tvProducerLayers, priceProducer,
            descFreeRecordingTime, tvStandardDescRecordingTime, tvPremiumDescRecordingTime, tvProducerDescRecordingTime, tvUserUpgrade;

    ImageView userImage;
    CircleImageView userProfileImage;

    Switch switchFree, switchStandard, switchPremium, switchProducer;
    String userId, firstName, lastName, userNameLogin, profilePicLogin, dob, mobile, email, date, userIdNormal, emailNormal;
    String userIdTwitter, firstNameTwitter, lastNameTwitter, emailFinalTwitter, profilePicTwitter, userNameTwitter;
    String userIdFb, firstNameFb, lastNameFb, emailFinalFb, profilePicFb, userNameFb;
    String switchFlag = "0";
    String packageId = "";

    String Authorization;
    private static int REQUEST_CODE = 12458;
    String clientTokens = null;
    String Nonec = null;

    public SubscriptionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());

        SharedPreferences loginSharedPref = getActivity().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        userIdNormal = loginSharedPref.getString("userId", null);
        firstName = loginSharedPref.getString("firstName", null);
        lastName = loginSharedPref.getString("lastName", null);
        emailNormal = loginSharedPref.getString("emailFinal", null);
        userNameLogin = loginSharedPref.getString("userName", null);
        profilePicLogin = loginSharedPref.getString("profilePic", null);
        dob = loginSharedPref.getString("dob", null);
        mobile = loginSharedPref.getString("mobile", null);

        if (userIdNormal == null) {
            SharedPreferences twitterEditor = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
            userIdTwitter = twitterEditor.getString("userId", null);
            firstNameTwitter = twitterEditor.getString("firstName", null);
            lastNameTwitter = twitterEditor.getString("lastName", null);
            emailFinalTwitter = twitterEditor.getString("emailFinal", null);
            userNameLogin = twitterEditor.getString("userName", null);
            profilePicLogin = twitterEditor.getString("profilePic", null);
        }

        if (userIdTwitter == null && userIdNormal == null) {
            SharedPreferences fbEditor = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);
            userIdFb = fbEditor.getString("userId", null);
            firstNameFb = fbEditor.getString("firstName", null);
            lastNameFb = fbEditor.getString("lastName", null);
            emailFinalFb = fbEditor.getString("emailFinal", null);
            profilePicLogin = fbEditor.getString("profilePic", null);
            userNameLogin = fbEditor.getString("userName", null);
        }


        if (userIdNormal != null) {
            userId = userIdNormal;
        } else if (userIdFb != null) {
            userId = userIdFb;
        } else {
            userId = userIdTwitter;
        }


    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subscriptions, container, false);
        rootView = view;
        subscriptionPackage();
        tvFreemium = (TextView) view.findViewById(R.id.tvFreemium);
        descFreeLayers = (TextView) view.findViewById(R.id.descFreeLayers);
        tvPriceFree = (TextView) view.findViewById(R.id.tvPriceFree);
        tvStandard = (TextView) view.findViewById(R.id.tvStandard);
        tvStandardLayers = (TextView) view.findViewById(R.id.tvStandardLayers);
        priceStandard = (TextView) view.findViewById(R.id.priceStandard);
        tvPremium = (TextView) view.findViewById(R.id.tvPremium);
        tvPremiumLayers = (TextView) view.findViewById(R.id.tvPremiumLayers);
        pricePremium = (TextView) view.findViewById(R.id.pricePremium);
        tvProducer = (TextView) view.findViewById(R.id.tvProducer);
        tvProducerLayers = (TextView) view.findViewById(R.id.tvProducerLayers);
        priceProducer = (TextView) view.findViewById(R.id.priceProducer);
        descFreeRecordingTime = (TextView) view.findViewById(R.id.descFreeRecordingTime);
        tvStandardDescRecordingTime = (TextView) view.findViewById(R.id.tvStandardDescRecordingTime);
        tvPremiumDescRecordingTime = (TextView) view.findViewById(R.id.tvPremiumDescRecordingTime);
        tvProducerDescRecordingTime = (TextView) view.findViewById(R.id.tvProducerDescRecordingTime);
        tvUserUpgrade = (TextView) view.findViewById(R.id.tvUserUpgrade);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        userProfileImage = (CircleImageView) view.findViewById(R.id.userProfileImage);
        switchFree = (Switch) view.findViewById(R.id.switchFree);
        switchStandard = (Switch) view.findViewById(R.id.switchStandard);
        switchPremium = (Switch) view.findViewById(R.id.switchPremium);
        switchProducer = (Switch) view.findViewById(R.id.switchProducer);

        try {

            if (userId != null) {
                userImage.setVisibility(View.INVISIBLE);
                userProfileImage.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(profilePicLogin).into(userProfileImage);
                tvUserUpgrade.setText("Upgrade" + " " + userNameLogin + "!");

            /*switchFree.setChecked(true);
            switchFree.setClickable(false);*/
            } else {
                switchStandard.setChecked(false);
                switchFree.setChecked(true);
            /*switchFree.setClickable(false);*/
                switchPremium.setChecked(false);
                switchProducer.setChecked(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SharedPreferences profileEditor = getActivity().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
        SharedPreferences profileImageEditor = getActivity().getSharedPreferences("ProfileImage", MODE_PRIVATE);
        if (profileImageEditor.getString("ProfileImage", null) != null) {
            userImage.setVisibility(View.INVISIBLE);
            userProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(profileImageEditor.getString("ProfileImage", null)).into(userProfileImage);
        }
        if (profileEditor.getString("updateId", null) != null) {
            //switchFree.setChecked(true);
            tvUserUpgrade.setText("Upgrade" + " " + profileEditor.getString("updateUserName", null) + "!");
        }


        GetClientTokenKey();


        return view;

    }


    public void subscriptionPackage() {
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PACKAGES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("AdvertisementData", response);
                        if (progressDialog != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("flag");
                            if (flag.equals("success")) {
                                try {
                                    packageId = jsonObject.getString("subscribedPack");
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                            switchPremium.setClickable(true);
                            switchStandard.setClickable(true);
                            switchProducer.setClickable(true);
                            if (packageId.equals("1")) {
                                switchFree.setChecked(true);
                                switchStandard.setChecked(false);
                                switchPremium.setChecked(false);
                                switchProducer.setChecked(false);
                            } else if (packageId.equals("2")) {
                                switchStandard.setChecked(true);
                                switchStandard.setClickable(false);
                                switchFree.setChecked(false);
                                switchPremium.setChecked(false);
                                switchProducer.setChecked(false);
                            } else if (packageId.equals("3")) {
                                switchPremium.setChecked(true);
                                switchPremium.setClickable(false);
                                switchStandard.setChecked(false);
                                switchFree.setChecked(false);
                                switchProducer.setChecked(false);

                            } else if (packageId.equals("4")) {
                                switchProducer.setChecked(true);
                                switchProducer.setClickable(false);
                                switchPremium.setChecked(false);
                                switchStandard.setChecked(false);
                                switchFree.setChecked(false);
                            }

                            subscriptionPackageArrayList.clear();
                            new ParseContents(getActivity()).parsePackageSubscription(response, subscriptionPackageArrayList);
                            TextView[] tv2 = new TextView[4];
                            tv2[0] = (TextView) rootView.findViewById(R.id.tvPriceFree);
                            tv2[1] = (TextView) rootView.findViewById(R.id.priceStandard);
                            tv2[2] = (TextView) rootView.findViewById(R.id.pricePremium);
                            tv2[3] = (TextView) rootView.findViewById(R.id.priceProducer);

                            for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                                tv2[0].setText(subscriptionPackageArrayList.get(0).getCost());
                                tv2[1].setText("$" + (subscriptionPackageArrayList.get(1).getCost()));
                                tv2[2].setText("$" + (subscriptionPackageArrayList.get(2).getCost()));
                                tv2[3].setText("$" + (subscriptionPackageArrayList.get(3).getCost()));
                            }

                            TextView[] tv = new TextView[4];
                            tv[0] = (TextView) rootView.findViewById(R.id.tvFreemium);
                            tv[1] = (TextView) rootView.findViewById(R.id.tvStandard);
                            tv[2] = (TextView) rootView.findViewById(R.id.tvPremium);
                            tv[3] = (TextView) rootView.findViewById(R.id.tvProducer);


                            for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                                tv[i].setText(subscriptionPackageArrayList.get(i).getPackage_name());
                                if (subscriptionPackageArrayList.get(i).getPackage_name().equals("Freemium")) {
                                    tv2[0].setText("Free");
                                }

                            }

                            TextView[] tv1 = new TextView[4];
                            tv1[0] = (TextView) rootView.findViewById(R.id.descFreeLayers);
                            tv1[1] = (TextView) rootView.findViewById(R.id.tvStandardLayers);
                            tv1[2] = (TextView) rootView.findViewById(R.id.tvPremiumLayers);
                            tv1[3] = (TextView) rootView.findViewById(R.id.tvProducerLayers);

                            for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                                tv1[i].setText(subscriptionPackageArrayList.get(i).getTotal_melody());
                            }


                            TextView[] tv3 = new TextView[4];
                            tv3[0] = (TextView) rootView.findViewById(R.id.descFreeRecordingTime);
                            tv3[1] = (TextView) rootView.findViewById(R.id.tvStandardDescRecordingTime);
                            tv3[2] = (TextView) rootView.findViewById(R.id.tvPremiumDescRecordingTime);
                            tv3[3] = (TextView) rootView.findViewById(R.id.tvProducerDescRecordingTime);

                            for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                                tv3[i].setText(subscriptionPackageArrayList.get(i).getRecording_time());
                            }

                            switchFree = (Switch) rootView.findViewById(R.id.switchFree);
                            switchStandard = (Switch) rootView.findViewById(R.id.switchStandard);
                            switchPremium = (Switch) rootView.findViewById(R.id.switchPremium);
                            switchProducer = (Switch) rootView.findViewById(R.id.switchProducer);
                            SharedPreferences profileEditor = getActivity().getSharedPreferences("ProfileUpdate", MODE_PRIVATE);
                            SharedPreferences profileImageEditor = getActivity().getSharedPreferences("ProfileImage", MODE_PRIVATE);
                            if (userId == null && (profileEditor.getString("updateId", null) == null)) {
                                switchFree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), SignInActivity.class);
                                        startActivity(i);
                                    }
                                });
                                switchStandard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), SignInActivity.class);
                                        startActivity(i);
                                    }
                                });

                                switchProducer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), SignInActivity.class);
                                        startActivity(i);
                                    }
                                });

                                switchPremium.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent i = new Intent(getActivity(), SignInActivity.class);
                                        startActivity(i);
                                    }
                                });
                            } else {
                                switchFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            packageId = "1";
                                            switchFree.setChecked(false);
                                        }
                                    }
                                });
                                switchFree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        packageId = "1";

                                    }
                                });
                                switchStandard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            packageId = "2";
                                            onBraintreeSubmit();
                                        }
                                    }
                                });
                                /*switchStandard.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBraintreeSubmit(v);
                                    }
                                });*/
                                switchPremium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            packageId = "3";
                                            onBraintreeSubmit();
                                        }
                                    }
                                });
                                /*switchPremium.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        onBraintreeSubmit(v);
                                    }
                                });*/
                                switchProducer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked) {
                                            packageId = "4";
                                            onBraintreeSubmit();
                                        }
                                    }
                                });
                                /*switchProducer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onBraintreeSubmit(v);
                                    }
                                });*/
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
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
                if (userId != null) {
                    params.put(USER_ID, userId);
                }
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void subscription_detail() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBSCRIPTION_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("flag");
                            String response1 = jsonObject.getString("res");
                            JSONArray jsonArray = jsonObject.getJSONArray("res");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String time = jsonObject1.getString("time");
                                String dt = time;  // Start date
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(dt));
                                c.add(Calendar.DATE, 30);
                                dt = sdf.format(c.getTime());

                                if (dt.equals(true)) {
                                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                        String errorMsg = error.toString();
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(USER_ID, userId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                Nonec = result.getPaymentMethodNonce().getNonce();

                LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = mInflater.inflate(R.layout.view_payment_conf, null, false);
                alertDialog = new Dialog(getActivity());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(customView);
                ImageView closePayDial = (ImageView) alertDialog.findViewById(R.id.closePayDial);
                ImageView CardImg = (ImageView) alertDialog.findViewById(R.id.CardImg);
                TextView txtAmount = (TextView) alertDialog.findViewById(R.id.txtAmount);
                TextView txtPackName = (TextView) alertDialog.findViewById(R.id.txtPackId);
                Button btnPaymentConf = (Button) alertDialog.findViewById(R.id.btnPayment);
                if (result.getPaymentMethodNonce() != null) {
                    //displayResult(result.getPaymentMethodNonce(), result.getDeviceData());
                    CardImg.setImageResource(result.getPaymentMethodType().getDrawable());
                    for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                        subscriptionPackageArrayList.get(i).getPackage_id();
                        subscriptionPackageArrayList.get(i).getCost();
                        if (packageId.equals("2")) {
                            txtAmount.setText("Paid Amount: $" + subscriptionPackageArrayList.get(1).getCost());
                            txtPackName.setText("Package Name :" + subscriptionPackageArrayList.get(1).getPackage_name());
                            cost = subscriptionPackageArrayList.get(1).getCost();
                        } else if (packageId.equals("3")) {
                            txtAmount.setText("Paid Amount: $" + subscriptionPackageArrayList.get(2).getCost());
                            txtPackName.setText("Package Name :" + subscriptionPackageArrayList.get(2).getPackage_name());
                            cost = subscriptionPackageArrayList.get(2).getCost();

                        } else if (packageId.equals("4")) {
                            txtAmount.setText("Paid Amount: $" + subscriptionPackageArrayList.get(3).getCost());
                            txtPackName.setText("Package Name :" + subscriptionPackageArrayList.get(3).getPackage_name());
                            cost = subscriptionPackageArrayList.get(3).getCost();
                        }
                    }

                    //txtCard_Number.setText(result.getPaymentMethodNonce());
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams wmlp = alertDialog.getWindow().getAttributes();
                    wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    wmlp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    alertDialog.show();

                }

                //
                btnPaymentConf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            alertDialog.dismiss();
                            PaymentContext(cost, Nonec);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                closePayDial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            subscriptionPackage();
                            alertDialog.dismiss();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                });
                // use the result to update your UI and send the payment method nonce to your server
            } else if (resultCode == Activity.RESULT_CANCELED) {
                subscriptionPackage();
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                subscriptionPackage();
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);

            }
        }
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest().clientToken(clientTokens);

        startActivityForResult(dropInRequest.getIntent(getActivity()), REQUEST_CODE);
    }


    public void GetClientTokenKey() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, BRAINTREE_FILES_CLIENT_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("status");
                            if (flag.equals("201")) {
                                clientTokens = jsonObject.getString("client_token");

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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void PaymentContext(final String Amount, final String Nonces) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BRAINTREE_FILES_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(HomeActivity.this, "" + response.toString();, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String flag = jsonObject.getString("status");
                            String status = jsonObject.getString("success");
                            if (flag.equals("201")) {
                                if (status.equals("true")) {
                                    subscriptionPackage();
                                    alertDialog.dismiss();
                                } else {
                                    subscriptionPackage();
                                    alertDialog.dismiss();
                                    Toast.makeText(getActivity(), "" + "Not a valid payment.", Toast.LENGTH_SHORT).show();
                                }

                            } else {

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
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            errorMsg = "There is either no connection or it timed out.";
                        } else if (error instanceof AuthFailureError) {
                            errorMsg = "AuthFailureError";
                        } else if (error instanceof ServerError) {
                            errorMsg = "ServerError";
                        } else if (error instanceof NetworkError) {
                            errorMsg = "Network Error";
                        } else if (error instanceof ParseError) {
                            errorMsg = "ParseError";
                        }
                        Log.d("Error", errorMsg);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("amount", Amount);
                params.put("payment_method_nonce", Nonces);
                params.put(USER_ID, userId);
                params.put(PACKAGE_ID, packageId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


}