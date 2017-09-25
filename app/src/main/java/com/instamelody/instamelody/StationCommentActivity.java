package com.instamelody.instamelody;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.instamelody.instamelody.Models.RecordingsModel;
import com.instamelody.instamelody.Models.RecordingsPool;

public class StationCommentActivity extends AppCompatActivity {

    TextView tvUserName, tvRecordingName, tvContributeLength, tvRecordingDate, tvRecordingGenres, tvContributeDate, tvIncludedCount;
    TextView tvViewCount, tvLikeCount, tvCommentCount, tvShareCount, txtJoinCount, TemptxtJoinCount;
    ImageView userProfileImage, ivRecordingCover, ivLikeButton, ivCommentButton, ivShareButton, ivDislikeButton, ivStationPre, ivStationNext;
    ImageView ivJoin, ivStationPlay, ivStationPause;
    SeekBar seekBarRecordings;
    RelativeLayout rlProfilePic, rlLike;
    ProgressDialog progressDialog;
    RecordingsModel recordingsModel;
    RecordingsPool recordingsPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_comment);

        userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        ivRecordingCover = (ImageView) findViewById(R.id.ivRecordingCover);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvRecordingName = (TextView) findViewById(R.id.tvRecordingName);
        tvRecordingDate = (TextView) findViewById(R.id.tvRecordingDate);
        tvRecordingGenres = (TextView) findViewById(R.id.tvRecordingGenres);
        tvContributeDate = (TextView) findViewById(R.id.tvContributeDate);
        tvContributeLength = (TextView) findViewById(R.id.tvContributeLength);
        tvIncludedCount = (TextView) findViewById(R.id.tvIncludedCount);
        tvViewCount = (TextView) findViewById(R.id.tvViewCount);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
        tvShareCount = (TextView) findViewById(R.id.tvShareCount);
        ivJoin = (ImageView) findViewById(R.id.ivJoin);
        ivStationPlay = (ImageView) findViewById(R.id.ivStationPlay);
        ivStationPause = (ImageView) findViewById(R.id.ivStationPause);
        seekBarRecordings = (SeekBar) findViewById(R.id.seekBarRecordings);
        rlProfilePic = (RelativeLayout) findViewById(R.id.rlProfilePic);
        ivLikeButton = (ImageView) findViewById(R.id.ivLikeButton);
        ivDislikeButton = (ImageView) findViewById(R.id.ivDislikeButton);
        ivCommentButton = (ImageView) findViewById(R.id.ivCommentButton);
        ivShareButton = (ImageView) findViewById(R.id.ivShareButton);
        rlLike = (RelativeLayout) findViewById(R.id.rlLike);
        ivStationPre = (ImageView) findViewById(R.id.ivStationPre);
        ivStationNext = (ImageView) findViewById(R.id.ivStationNext);
        txtJoinCount = (TextView) findViewById(R.id.txtJoinCount);
        TemptxtJoinCount = (TextView) findViewById(R.id.TemptxtJoinCount);

        setData();

    }

    void setData(){
        if (getIntent() != null) {
            recordingsModel = (RecordingsModel) getIntent().getSerializableExtra("recording_modle");
            recordingsPool = (RecordingsPool) getIntent().getSerializableExtra("recording_pool");

            tvUserName.setText(recordingsModel.getUserName());
            tvRecordingName.setText(recordingsModel.getRecordingName());
            tvRecordingGenres.setText("Genre:" + " " + recordingsModel.getGenreName());
            tvContributeLength.setText(recordingsPool.getDuration());
            tvContributeDate.setText(recordingsPool.getDateAdded());

        }
    }
}
