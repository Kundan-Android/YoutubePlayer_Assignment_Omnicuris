package com.widas.youtubeplayer_assignment_omnicuris;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

//AIzaSyBSADAzB8opboTlSHjYQWaWxyDTjs-EfH4
public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener, RecyclerAdapter.LoadVodeofromList {
    public static final String API_KEY = "AIzaSyBSADAzB8opboTlSHjYQWaWxyDTjs-EfH4";
    public static final String VIDEO_ID = "7CSYn_VEYZI";
    private View mPlayButtonLayout;
    private TextView mPlayTimeTextView;
    private YouTubePlayer mPlayer;

    private Handler mHandler = null;
    private SeekBar mSeekBar;

    private ImageButton play_video;
    private ImageButton pause_video;
    private ImageButton fullscreen_video;
    private int playPercent = 0;
    @SuppressLint("InlinedApi")
    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

    @SuppressLint("InlinedApi")
    private static final int LANDSCAPE_ORIENTATION = Build.VERSION.SDK_INT < 9
            ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            : ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    private boolean mAutoRotation = false;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAutoRotation = Settings.System.getInt(getContentResolver(),
                Settings.System.ACCELEROMETER_ROTATION, 0) == 1;

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(API_KEY, this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        //to use RecycleView, you need a layout manager. default is LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(getApplicationContext(),this);
        recyclerView.setAdapter(adapter);




        //Add play button to explicitly play video in YouTubePlayerView
        mPlayButtonLayout = findViewById(R.id.video_control);
        play_video = findViewById(R.id.play_video);
        pause_video = findViewById(R.id.pause_video);
        fullscreen_video = findViewById(R.id.fullscreen);
       // pause_video.setVisibility(View.GONE);
        findViewById(R.id.play_video).setOnClickListener(this);
        findViewById(R.id.pause_video).setOnClickListener(this);

        mPlayTimeTextView = (TextView) findViewById(R.id.play_time);
        mSeekBar = (SeekBar) findViewById(R.id.video_seekbar);
      //  mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        // set Progress bar values
        mSeekBar.setProgress(0);
        mSeekBar.setMax(100);
        mHandler = new Handler();



        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mPlayer.getDurationMillis();
                int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mPlayer.seekToMillis(currentPosition);

                // update timer progress again
                updateProgressBar();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

            }
        });
        fullscreen_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE)
                   setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mPlayer != null) {
                long totalDuration = mPlayer.getDurationMillis();
                long currentDuration = mPlayer.getCurrentTimeMillis();

           /* // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));
*/
                // Updating progress bar
                int progress = (int) (getProgressPercentage(currentDuration, totalDuration));
                //Log.d("Progress", ""+progress);
                mSeekBar.setProgress(progress);

                // Running this thread after 100 milliseconds
                mHandler.postDelayed(this, 100);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPlayer = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer = null;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        Log.d("MainAcitivity.java", "Success Initialization");
        if (null == youTubePlayer)
            return;
        mPlayer = youTubePlayer;
       // mPlayer.setOnFullscreenListener(this);

        displayCurrentTime();

        //buffering started
        if (!b) {
            youTubePlayer.loadVideo(VIDEO_ID);/*.loadVideo(VIDEO_ID);*/
        }
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        mPlayButtonLayout.setVisibility(View.VISIBLE);

        //Add listners to youtube Player instance
        youTubePlayer.setPlayerStateChangeListener(mPlayerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(mPlaybackEventListener);
        updateProgressBar();
        if (mAutoRotation) {
            mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE
                    | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        } else {
            mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                    | YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI
                    | YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        }
    }

        YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {
                displayCurrentTime();
            }

            @Override
            public void onVideoEnded() {

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        };

        YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                mHandler.postDelayed(runnable, 100);
                displayCurrentTime();
            }

            @Override
            public void onPaused() {
                mHandler.removeCallbacks(runnable);
            }

            @Override
            public void onStopped() {
                mHandler.removeCallbacks(runnable);
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {
                mHandler.postDelayed(runnable, 100);
            }
        };

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            long lengthPlayed = (mPlayer.getDurationMillis() * progress) / 100;
            mPlayer.seekToMillis((int) lengthPlayed);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    @Override //reconfigure display properties on screen rotation
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // handle change here
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            if (mPlayer != null){
                mPlayer.setFullscreen(true);

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

               // questionVideo.setDimensions(displayHeight, displayWidth);
               // questionVideo.getHolder().setFixedSize(displayHeight, displayWidth);
            }
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Log.d("MainAcitivity.java", "Failed Initialization");
    }

    private void displayCurrentTime(){
        if (null == mPlayer) return;

        String formaatedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formaatedTime);
        playPercent = (int) (((float) mPlayer.getCurrentTimeMillis()/(float) mPlayer.getDurationMillis()) * 100);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mSeekBar.setProgress(playPercent,true);
        }
    }
    private String formatTime(int millis){
     int seconds = millis /1000;
     int minutes = seconds / 60;
     int hours = minutes / 60;
     return (hours == 0 ? "--:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            displayCurrentTime();
            mHandler.postDelayed(this, 100);
        }
    };



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_video){
            if (null != mPlayer && !mPlayer.isPlaying()) {
                mPlayer.play();
               play_video.setVisibility(View.GONE);
               pause_video.setVisibility(View.VISIBLE);

            }
        } else if (v.getId() == R.id.pause_video){
            mPlayer.pause();
            play_video.setVisibility(View.VISIBLE);
            pause_video.setVisibility(View.GONE);
        }

    }

    @Override
    public void passvideoURL(String url) {
        Log.d("passvideoURL", url);
        if (mPlayer != null){
            mPlayer.pause();
            mPlayer.loadVideo(url);
        }
    }
}
