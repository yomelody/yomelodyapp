package com.instamelody.instamelody.Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.instamelody.instamelody.Models.SubscriptionPackage;
import com.instamelody.instamelody.Parse.ParseContents;
import com.instamelody.instamelody.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
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
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyName;
import static com.instamelody.instamelody.utils.Const.ServiceType.AuthenticationKeyValue;
import static com.instamelody.instamelody.utils.Const.ServiceType.BRAINTREE_FILES_TRANSACTION;
import static com.instamelody.instamelody.utils.Const.ServiceType.PACKAGES;
import static com.instamelody.instamelody.utils.Const.ServiceType.SUBSCRIPTION;
import static com.instamelody.instamelody.utils.Const.ServiceType.SUBSCRIPTION_DETAIL;
import static com.instamelody.instamelody.utils.Const.ServiceType.SUB_DETAIL;

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

    String BRAIN_TREE_TRANSACTION_ID = "id";
    String cost;

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
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("AeE8pO8NpWo4hbsM8Ha5sjRXXvFVjUNO4R6VKF7Oic0UeLcbgrAXdtXsjtvLtkDaGfB9RSAKC3qfDDq6");

    PayPalPayment payment;


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

        SharedPreferences twitterEditor = getActivity().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        userIdTwitter = twitterEditor.getString("userId", null);
        firstNameTwitter = twitterEditor.getString("firstName", null);
        lastNameTwitter = twitterEditor.getString("lastName", null);
        userNameTwitter = twitterEditor.getString("userName", null);
        emailFinalTwitter = twitterEditor.getString("emailFinal", null);
        profilePicTwitter = twitterEditor.getString("profilePic", null);


        SharedPreferences fbEditor = getActivity().getSharedPreferences("MyFbPref", MODE_PRIVATE);
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
        } else {
            userId = userIdTwitter;
        }

            brainTreeFragment = BraintreeFragment.newInstance(getActivity(), Authorization);
//For PayPal Integration
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

    }



    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences switchFreeEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE);
        switchFreeEditor.getBoolean("switchFree", false);
        SharedPreferences switchStandardEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE);
        switchStandardEditor.getBoolean("switchStandard", false);
        SharedPreferences switchPremiumEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE);
        switchPremiumEditor.getBoolean("switchPremium", false);
        SharedPreferences switchProducerEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE);
        switchProducerEditor.getBoolean("switchProducer", false);
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


//        /For PayPal Integration
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            nonce = result.getPaymentMethodNonce().getNonce();
            brainTree(nonce);
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.d("payment", confirm.toJSONObject().toString(4));
//                    Toast.makeText(getActivity(), "" + confirm.toJSONObject().toString(4), Toast.LENGTH_SHORT).show();
                    JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());
                    String client = jsonObj.getString("client");
                    String response = jsonObj.getString("response");
                    String response_type = jsonObj.getString("response_type");
                    JSONObject client_details = jsonObj.getJSONObject("client");
                    String environment = client_details.getString("environment");
                    String paypal_sdk_version = client_details.getString("paypal_sdk_version");
                    String plateform = client_details.getString("platform");
                    String product_name = client_details.getString("product_name");
                    JSONObject response_details = jsonObj.getJSONObject("response");
                    String create_time = response_details.getString("create_time");
                    String payment_id = response_details.getString("id");
                    String intent = response_details.getString("intent");
                    String state = response_details.getString("state");

                    SharedPreferences.Editor PayPal_detail = getActivity().getSharedPreferences("PayPal_detail", MODE_PRIVATE).edit();
                    PayPal_detail.putString("Transaction_Id", payment_id);
                    PayPal_detail.putString("state", state);
                    PayPal_detail.putString("create_time", create_time);
                    PayPal_detail.apply();

                } catch (JSONException e) {
                    Log.e("payment", "an extremely unlikely failure occurred: ", e);
                }
                sub_detail();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("payment", "The user canceled.");
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("payment", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    public void subscriptionPackage() {
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
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
                        subscriptionPackageArrayList.clear();
                        new ParseContents(getActivity()).parsePackageSubscription(response, subscriptionPackageArrayList);


                        TextView[] tv = new TextView[4];
                        tv[0] = (TextView) rootView.findViewById(R.id.tvFreemium);
                        tv[1] = (TextView) rootView.findViewById(R.id.tvStandard);
                        tv[2] = (TextView) rootView.findViewById(R.id.tvPremium);
                        tv[3] = (TextView) rootView.findViewById(R.id.tvProducer);


                        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                            tv[i].setText(subscriptionPackageArrayList.get(i).getPackage_name());

                        }

                        TextView[] tv1 = new TextView[4];
                        tv1[0] = (TextView) rootView.findViewById(R.id.descFreeLayers);
                        tv1[1] = (TextView) rootView.findViewById(R.id.tvStandardLayers);
                        tv1[2] = (TextView) rootView.findViewById(R.id.tvPremiumLayers);
                        tv1[3] = (TextView) rootView.findViewById(R.id.tvProducerLayers);

                        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                            tv1[i].setText(subscriptionPackageArrayList.get(i).getTotal_melody());
                        }

                        TextView[] tv2 = new TextView[4];
                        tv2[0] = (TextView) rootView.findViewById(R.id.tvPriceFree);
                        tv2[1] = (TextView) rootView.findViewById(R.id.priceStandard);
                        tv2[2] = (TextView) rootView.findViewById(R.id.pricePremium);
                        tv2[3] = (TextView) rootView.findViewById(R.id.priceProducer);

                        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
                            tv2[i].setText(subscriptionPackageArrayList.get(i).getCost());
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
                        switchFree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (switchFree.isChecked()) {
                                    SharedPreferences.Editor switchFreeEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE).edit();
                                    switchFreeEditor.putBoolean("switchFree", true);
                                    switchFreeEditor.apply();
                                } else {
                                    SharedPreferences.Editor switchFreeEditor = getActivity().getSharedPreferences("SwitchStatusFree", MODE_PRIVATE).edit();
                                    switchFreeEditor.putBoolean("switchFree", false);
                                    switchFreeEditor.apply();
                                }

                                if (switchFlag == "1") {
                                    switchFree.setChecked(false);
                                    switchFlag = "0";
                                } else {
                                    switchFree.setChecked(true);
                                    switchFlag = "1";
                                    packageId = "1";
                                    if (userId != null) {
                                        tvUserUpgrade.setText("Upgrade" + " " + userNameLogin + "!");
                                        userImage.setVisibility(View.INVISIBLE);
                                        userProfileImage.setVisibility(View.VISIBLE);
                                        Picasso.with(getActivity()).load(profilePicLogin).into(userProfileImage);
                                    }
//                                    subscription();

//                                    onBuyPressed(v);


                                }
                            }
                        });

                        switchStandard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (switchStandard.isChecked()) {
                                    SharedPreferences.Editor switchStandardEditor = getActivity().getSharedPreferences("SwitchStatusStandard", MODE_PRIVATE).edit();
                                    switchStandardEditor.putBoolean("switchStandard", true);
                                    switchStandardEditor.apply();
                                } else {
                                    SharedPreferences.Editor switchStandardEditor = getActivity().getSharedPreferences("SwitchStatusStandard", MODE_PRIVATE).edit();
                                    switchStandardEditor.putBoolean("switchStandard", false);
                                    switchStandardEditor.apply();
                                }

                                if (switchFlag == "1") {
                                    switchStandard.setChecked(false);
                                    switchFlag = "0";
                                } else {
                                    switchStandard.setChecked(true);
                                    switchFlag = "1";
                                    packageId = "2";
//                                    subscription();
//                                    onBuyPressed(v);
                                    onBraintreeSubmit(v);

                                }
                            }
                        });

                        switchPremium.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (switchPremium.isChecked()) {
                                    SharedPreferences.Editor switchPremiumEditor = getActivity().getSharedPreferences("SwitchStatusPremium", MODE_PRIVATE).edit();
                                    switchPremiumEditor.putBoolean("switchPremium", true);
                                    switchPremiumEditor.apply();
                                } else {
                                    SharedPreferences.Editor switchPremiumEditor = getActivity().getSharedPreferences("SwitchStatusPremium", MODE_PRIVATE).edit();
                                    switchPremiumEditor.putBoolean("switchPremium", false);
                                    switchPremiumEditor.apply();
                                }
                                if (switchFlag == "1") {
                                    switchPremium.setChecked(false);
                                    switchFlag = "0";
                                } else {
                                    switchPremium.setChecked(true);
                                    switchFlag = "1";
                                    packageId = "3";
//                                    subscription();
//                                    onBuyPressed(v);
                                    onBraintreeSubmit(v);
                                }
                            }
                        });

                        switchProducer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (switchProducer.isChecked()) {
                                    SharedPreferences.Editor switchProducerEditor = getActivity().getSharedPreferences("SwitchStatusProducer", MODE_PRIVATE).edit();
                                    switchProducerEditor.putBoolean("switchProducer", true);
                                    switchProducerEditor.apply();
                                } else {
                                    SharedPreferences.Editor switchProducerEditor = getActivity().getSharedPreferences("SwitchStatusProducer", MODE_PRIVATE).edit();
                                    switchProducerEditor.putBoolean("switchProducer", false);
                                    switchProducerEditor.apply();
                                }
                                if (switchFlag == "1") {
                                    switchProducer.setChecked(false);
                                    switchFlag = "0";
                                } else {
                                    switchProducer.setChecked(true);
                                    switchFlag = "1";
                                    packageId = "4";
//                                    subscription();
//                                    onBuyPressed(v);
                                    onBraintreeSubmit(v);

                                }
                            }
                        });

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
                params.put(KEY_SUBSCRIPTION, "admin@123");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void subscription() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUBSCRIPTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("flag");
                            String response1 = jsonObject.getString("response");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                            String msg = jsonObject1.getString("msg");
                            Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                            subscription_detail();

                        } catch (JSONException e) {
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
                params.put(STATUS, switchFlag);
                params.put(PACKAGE_ID, packageId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    public void sub_detail() {
        final SharedPreferences PayPal_detail = getActivity().getSharedPreferences("PayPal_detail", MODE_PRIVATE);
        PayPal_detail.getString("Transaction_Id", null);
        PayPal_detail.getString("state", null);
        PayPal_detail.getString("create_time", null);
        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            subscriptionPackageArrayList.get(i).getCost();
            if (packageId.equals("2")) {
                cost = subscriptionPackageArrayList.get(1).getCost();
            } else if (packageId.equals("3")) {
                cost = subscriptionPackageArrayList.get(2).getCost();
            } else if (packageId.equals("4")) {
                cost = subscriptionPackageArrayList.get(3).getCost();
            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("flag");
                            String response1 = jsonObject.getString("response");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                            String msg = jsonObject1.getString("msg");
                            String sub_id = jsonObject1.getString("sub_id");
                            if (flag.equals("unsuccess")) {
                                sub_detail2(sub_id);
                            }
//                            subscription_detail();

                        } catch (JSONException e) {
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
                params.put(STATUS, switchFlag);
                params.put(PACKAGE_ID, packageId);
                params.put(AuthenticationKeyName, AuthenticationKeyValue);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void sub_detail2(final String sub_id) {
        final SharedPreferences PayPal_detail = getActivity().getSharedPreferences("PayPal_detail", MODE_PRIVATE);
        PayPal_detail.getString("Transaction_Id", null);
        PayPal_detail.getString("state", null);
        PayPal_detail.getString("create_time", null);
        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            subscriptionPackageArrayList.get(i).getCost();
            if (packageId.equals("2")) {
                cost = subscriptionPackageArrayList.get(1).getCost();
            } else if (packageId.equals("3")) {
                cost = subscriptionPackageArrayList.get(2).getCost();
            } else if (packageId.equals("4")) {
                cost = subscriptionPackageArrayList.get(3).getCost();
            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SUB_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("flag");
                            String response1 = jsonObject.getString("response");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                            String msg = jsonObject1.getString("msg");
                            if (userId != null) {
                                tvUserUpgrade.setText("Upgrade" + " " + userNameLogin + "!");
                                userImage.setVisibility(View.INVISIBLE);
                                userProfileImage.setVisibility(View.VISIBLE);
                                Picasso.with(getActivity()).load(profilePicLogin).into(userProfileImage);
                            }
                            Toast.makeText(getActivity(), "" + msg, Toast.LENGTH_SHORT).show();
                            subscription_detail();

                        } catch (JSONException e) {
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
                params.put(SUB_ID, sub_id);
                params.put(STATUS, switchFlag);
                params.put(PACKAGE_ID, packageId);
                params.put(PAYPAL_ID, PayPal_detail.getString("Transaction_Id", null));
                params.put(STATE, PayPal_detail.getString("state", null));
                params.put(CREATE_TIME, PayPal_detail.getString("create_time", null));
                params.put(PAYMENT, cost);
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

//        nonce = paymentMethodNonce.getNonce();
        Log.d("nonce", paymentMethodNonce.getNonce());
    public void brainTree(final String nonce) {
                            String status = jsonObject.getString("status");
                            String success = jsonObject.getString("success");
                            String transactionId = jsonObject.getString("transaction_id");
                            brainTreeTransaction(transactionId);
                params.put(PAYMENT_METHOD_NOUNCE, nonce);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }


    public void brainTreeTransaction(final String transaction_Id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BRAINTREE_FILES_TRANSACTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            String success = jsonObject.getString("success");
                            String transaction_message = jsonObject.getString("message");
                            JSONObject jsonObject1 = jsonObject.getJSONObject("transaction_detail");
                            String transaction_id = jsonObject1.getString("id");
                            String type = jsonObject1.getString("amount");
                            String status_detail = jsonObject1.getString("status");
                            String created_at = jsonObject1.getString("created_at");
                            String updated_at = jsonObject1.getString("updated_at");
                            JSONObject jsonObject2 = jsonObject.getJSONObject("payment_detail");
//                            String token = jsonObject2.getString("token");
                            String bin = jsonObject2.getString("bin");
                            String last_4 = jsonObject2.getString("last_4");
                            String cardType = jsonObject2.getString("card_type");
                            String expirationDate = jsonObject2.getString("expiration_date");
//                            String cardHolderName = jsonObject2.getString("cardholder_name");
                            String location = jsonObject2.getString("customer_location");

                        } catch (JSONException e) {
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
                params.put(BRAIN_TREE_TRANSACTION_ID, transaction_Id);
    public void onBuyPressed(View pressed) {

        for (int i = 0; i < subscriptionPackageArrayList.size(); i++) {
            subscriptionPackageArrayList.get(i).getPackage_id();
            subscriptionPackageArrayList.get(i).getCost();
            if (packageId.equals("2")) {
                payment = new PayPalPayment(new java.math.BigDecimal(subscriptionPackageArrayList.get(1).getCost()), "USD", subscriptionPackageArrayList.get(1).getPackage_name(), PayPalPayment.PAYMENT_INTENT_SALE);
            } else if (packageId.equals("3")) {
                payment = new PayPalPayment(new java.math.BigDecimal(subscriptionPackageArrayList.get(2).getCost()), "USD", subscriptionPackageArrayList.get(2).getPackage_name(), PayPalPayment.PAYMENT_INTENT_SALE);

            } else if (packageId.equals("4")) {
                payment = new PayPalPayment(new java.math.BigDecimal(subscriptionPackageArrayList.get(3).getCost()), "USD", subscriptionPackageArrayList.get(3).getPackage_name(), PayPalPayment.PAYMENT_INTENT_SALE);
            }
        }
        Intent intent = new Intent(getActivity(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }
}