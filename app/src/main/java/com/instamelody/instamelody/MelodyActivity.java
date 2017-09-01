package com.instamelody.instamelody;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.instamelody.instamelody.Fragments.MelodyPacksFragment;
import com.instamelody.instamelody.Fragments.RecordingsFragment;
import com.instamelody.instamelody.Fragments.SubscriptionsFragment;
import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;

import java.util.ArrayList;

import static android.view.View.VISIBLE;


/**
 * Created by Shubhansh Jaiswal on 11/29/2016.
 */

public class MelodyActivity extends AppCompatActivity {

    public Button btnMelodyPacks, btnRecordings, btnSubscriptions, melodySearchButton, btnCancel;
    RelativeLayout rlMelodySearchButton, rlMelodySearch;
    AppBarLayout appBarMelody;
    ArrayList<RecordingsModel> recordingList = new ArrayList<>();
    ArrayList<RecordingsPool> recordingsPools = new ArrayList<>();
    private MenuItem searchMenuItem;
    String strArtist, userId, Instruments, BPM, artistName;
    EditText subEtFilterName, subEtFilterInstruments, subEtFilterBPM;
    int clicked_button = 0;

    ImageView discover, message, ivBackButton, ivHomeButton, ivProfile, audio_feed, ivMelodyFilter;
    SearchView searchView, search1;
    String searchGet;
    String strName;
    TextView appBarMainText;
    ProgressDialog progressDialog;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    //  LinearLayout tab1,llFragSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        clearSharePrefMelody();

        SharedPreferences filterPref = this.getSharedPreferences("FilterPref", MODE_PRIVATE);
        strName = filterPref.getString("stringFilter", null);

        Bundle bundle = getIntent().getExtras();
        SharedPreferences loginSharedPref = getApplicationContext().getSharedPreferences("prefInstaMelodyLogin", MODE_PRIVATE);
        SharedPreferences twitterPref = getApplicationContext().getSharedPreferences("TwitterPref", MODE_PRIVATE);
        SharedPreferences fbPref = getApplicationContext().getSharedPreferences("MyFbPref", MODE_PRIVATE);

        if (loginSharedPref.getString("userId", null) != null) {
            userId = loginSharedPref.getString("userId", null);
        } else if (fbPref.getString("userId", null) != null) {
            userId = fbPref.getString("userId", null);
        } else if (twitterPref.getString("userId", null) != null) {
            userId = twitterPref.getString("userId", null);
        }

        search1 = (SearchView) findViewById(R.id.searchMelody);
        btnMelodyPacks = (Button) findViewById(R.id.btnMelodyPacks);
        melodySearchButton = (Button) findViewById(R.id.melodySearchButton);
        rlMelodySearchButton = (RelativeLayout) findViewById(R.id.rlMelodySearchButton);
        btnSubscriptions = (Button) findViewById(R.id.btnSubscriptions);
        rlMelodySearch = (RelativeLayout) findViewById(R.id.rlMelodySearch);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnRecordings = (Button) findViewById(R.id.btnRecordings);
        discover = (ImageView) findViewById(R.id.discover);
        message = (ImageView) findViewById(R.id.message);
        ivProfile = (ImageView) findViewById(R.id.userProfileImage);
        audio_feed = (ImageView) findViewById(R.id.audio_feed);
        appBarMainText = (TextView) findViewById(R.id.appBarMainText);
        appBarMelody = (AppBarLayout) findViewById(R.id.appBarMelody);
        ivMelodyFilter = (ImageView) findViewById(R.id.ivMelodyFilter);
        //  tab1 = (LinearLayout) findViewById(R.id.tab1);
        // llFragSubs = (LinearLayout) findViewById(R.id.llFragSubs);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMelody);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        MelodyPacksFragment mpf = new MelodyPacksFragment();
        getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();

        btnMelodyPacks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMelodyPacks.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnRecordings.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#E4E4E4"));
                clicked_button = 0;
                clearSharePrefMelody();
                MelodyPacksFragment mpf = new MelodyPacksFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
            }
        });

        btnRecordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMelodyPacks.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnRecordings.setBackgroundColor(Color.parseColor("#FFFFFF"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#E4E4E4"));
                clicked_button = 1;
                clearSharePrefMelody();
                RecordingsFragment rf = new RecordingsFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
            }
        });

        btnSubscriptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMelodyPacks.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnRecordings.setBackgroundColor(Color.parseColor("#E4E4E4"));
                btnSubscriptions.setBackgroundColor(Color.parseColor("#FFFFFF"));
                clicked_button = 2;
                SubscriptionsFragment subf = new SubscriptionsFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_melody, subf).commit();
            }
        });

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiscoverActivity.class);
                startActivity(intent);
            }
        });

        melodySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivHomeButton.setVisibility(View.GONE);
                appBarMainText.setVisibility(View.GONE);
                ivMelodyFilter.setVisibility(View.GONE);
                search1.setVisibility(VISIBLE);
                ((EditText) search1.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setHintTextColor(getResources().getColor(R.color.colorSearch));
                btnCancel.setVisibility(VISIBLE);
            }
        });

        /*rlMelodySearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlMelodySearch.setVisibility(VISIBLE);
                ivHomeButton.setVisibility(VISIBLE);
                appBarMainText.setVisibility(VISIBLE);
                ivMelodyFilter.setVisibility(VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            }
        });


        search1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                rlMelodySearch.setVisibility(VISIBLE);
                ivHomeButton.setVisibility(VISIBLE);
                appBarMainText.setVisibility(VISIBLE);
                ivMelodyFilter.setVisibility(VISIBLE);
                search1.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                search1.isSubmitButtonEnabled();
                String searchContent = search1.getQuery().toString();
                SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                editorSearchString.putString("stringSearch", searchContent);
                editorSearchString.apply();
                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.clear();
                editorFilterString.apply();

                if (clicked_button == 0) {
                    MelodyPacksFragment mpf = new MelodyPacksFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();

                } else if (clicked_button == 1) {
                    RecordingsFragment rf = new RecordingsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((EditText) search1.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                        .setTextColor(getResources().getColor(R.color.colorSearch));
                return false;
            }
        });

        ivMelodyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MelodyActivity.this, "" + clicked_button, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MelodyActivity.this);
//                builderSingle.setIcon(R.drawable.ic_launcher);
                builderSingle.setTitle("Filter Audio");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MelodyActivity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Latest");
                arrayAdapter.add("Trending");
                arrayAdapter.add("Favorites");
                arrayAdapter.add("Artist");
                arrayAdapter.add("# of Instruments");
                arrayAdapter.add("BPM");


                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strName = arrayAdapter.getItem(which);
                        if (strName.equals("Artist")) {
                            openDialog();
                        } else if (strName.equals("# of Instruments")) {
                            openDialogInstruments();
                        } else if (strName.equals("BPM")) {
                            openDialogBPM();
                        } else {
                            AlertDialog.Builder builderInner = new AlertDialog.Builder(MelodyActivity.this);
                            builderInner.setMessage(strName);
                            SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                            editorFilterString.putString("stringFilter", strName);
                            editorFilterString.apply();
                            SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
                            editorSearchString.clear();
                            editorSearchString.apply();
                            SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                            editorFilterArtist.putString("stringFilterArtist", artistName);
                            editorFilterArtist.apply();
                            SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                            editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                            editorFilterInstruments.apply();
                            SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                            editorFilterBPM.clear();
                            editorFilterBPM.apply();
                            builderInner.setTitle("Your Selected Item is");
                            if (clicked_button == 0) {
                                MelodyPacksFragment mpf = new MelodyPacksFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();

                            } else if (clicked_button == 1) {
                                RecordingsFragment rf = new RecordingsFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();

                            }

                        }
                    }
                });
                builderSingle.show();
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MessengerActivity.class);
                startActivity(intent);
            }
        });

        ivBackButton = (ImageView) findViewById(R.id.ivBackButton);
        ivHomeButton = (ImageView) findViewById(R.id.ivHomeButton);

        ivBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        audio_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MelodyActivity.this, StationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        try{
            super.onBackPressed();
            clearSharePrefMelody();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearSharePrefMelody();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener((SearchView.OnQueryTextListener) this);

        searchGet = (String) searchView.getQuery();

        return true;
    }

    private void openDialog() {
        LayoutInflater inflater = LayoutInflater.from(MelodyActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterName = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder2 = new android.support.v7.app.AlertDialog.Builder(this);
        builder2.setTitle("ArtistName");
        builder2.setMessage("Choose Artist Name to Search Artist");
        builder2.setView(subView);

        TextView title = new TextView(this);
        title.setText("ArtistName");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder2.setCustomTitle(title);

        builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                strArtist = subEtFilterName.getText().toString().trim();
                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", strArtist);
                editorFilterArtist.apply();

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.clear();
                editorFilterBPM.apply();

                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.clear();
                editorFilterInstruments.apply();

                if (clicked_button == 0) {
                    MelodyPacksFragment mpf = new MelodyPacksFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
                } else if (clicked_button == 1) {
                    RecordingsFragment rf = new RecordingsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
                }


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);

            }
        });

        builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterName.getWindowToken(), 0);
            }
        });

        builder2.show();
    }

    private void openDialogInstruments() {
        LayoutInflater inflater = LayoutInflater.from(MelodyActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterInstruments = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("Number of Instruments");
        builder3.setMessage("Give Instruments Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("Instruments");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                Instruments = subEtFilterInstruments.getText().toString().trim();
                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                editorFilterInstruments.apply();

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.clear();
                editorFilterBPM.apply();

                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", artistName);
                editorFilterArtist.apply();


                if (clicked_button == 0) {
                    MelodyPacksFragment mpf = new MelodyPacksFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
                } else if (clicked_button == 1) {
                    RecordingsFragment rf = new RecordingsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
                }


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterInstruments.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    private void openDialogBPM() {
        LayoutInflater inflater = LayoutInflater.from(MelodyActivity.this);
        View subView = inflater.inflate(R.layout.dialog_layout, null);

        subEtFilterBPM = (EditText) subView.findViewById(R.id.dialogEtTopicName);

        android.support.v7.app.AlertDialog.Builder builder3 = new android.support.v7.app.AlertDialog.Builder(this);
        builder3.setTitle("BPM");
        builder3.setMessage("Give BPM Value to Filter");
        builder3.setView(subView);

        TextView title = new TextView(this);
        title.setText("BPM");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder3.setCustomTitle(title);

        builder3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //tvInfo.setText(subEtTopicName.getText().toString());
                BPM = subEtFilterBPM.getText().toString().trim();
                SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
                editorFilterBPM.putString("stringFilterBPM", BPM);
                editorFilterBPM.apply();

                SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
                editorFilterString.putString("stringFilter", strName);
                editorFilterString.apply();

                SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
                editorFilterArtist.putString("stringFilterArtist", artistName);
                editorFilterArtist.apply();

                SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
                editorFilterInstruments.putString("stringFilterInstruments", Instruments);
                editorFilterInstruments.apply();


                if (clicked_button == 0) {
                    MelodyPacksFragment mpf = new MelodyPacksFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
                } else if (clicked_button == 1) {
                    RecordingsFragment rf = new RecordingsFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, rf).commit();
                }


                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);

            }
        });

        builder3.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(subEtFilterBPM.getWindowToken(), 0);
            }
        });

        builder3.show();
    }

    public void clearSharePrefMelody() {
        SharedPreferences.Editor editorFilterBPM = getApplicationContext().getSharedPreferences("FilterPrefBPM", MODE_PRIVATE).edit();
        editorFilterBPM.clear();
        editorFilterBPM.apply();

        SharedPreferences.Editor editorFilterInstruments = getApplicationContext().getSharedPreferences("FilterPrefInstruments", MODE_PRIVATE).edit();
        editorFilterInstruments.clear();
        editorFilterInstruments.apply();

        SharedPreferences.Editor editorFilterString = getApplicationContext().getSharedPreferences("FilterPref", MODE_PRIVATE).edit();
        editorFilterString.clear();
        editorFilterString.apply();

        SharedPreferences.Editor editorFilterArtist = getApplicationContext().getSharedPreferences("FilterPrefArtist", MODE_PRIVATE).edit();
        editorFilterArtist.clear();
        editorFilterArtist.apply();

        SharedPreferences.Editor editorSearchString = getApplicationContext().getSharedPreferences("SearchPref", MODE_PRIVATE).edit();
        editorSearchString.clear();
        editorSearchString.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if (MelodyCardListAdapter.mediaPlayer != null) {
            try {
                MelodyCardListAdapter.mediaPlayer.stop();
                MelodyCardListAdapter.mediaPlayer.reset();
                MelodyCardListAdapter.mediaPlayer.release();
                try {
                    MelodyPacksFragment mpf = new MelodyPacksFragment();
                    getFragmentManager().beginTransaction().replace(R.id.activity_melody, mpf).commit();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }

        }*/
    }


}
