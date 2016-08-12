package com.aviatorex.playnsor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener, SensorEventListener {
    public ImageButton btnPlay;
    public ImageButton btnForward;
    public ImageButton btnBackward;
    public ImageButton btnNext;
    public ImageButton btnPrevious;
    public ImageButton btnPlaylist;
    public ImageButton btnRepeat;
    public ImageButton btnShuffle;
    public SeekBar songProgressBar;
    public TextView songTitleLabel;
    public TextView songCurrentDurationLabel;
    public TextView songTotalDurationLabel;
    public MediaPlayer mp;
    public Utilities utils;
    public SongManager songManager;
    public int seekForwardTime = 5000; // 5000 milliseconds
    public int seekBackwardTime = 5000; // 5000 milliseconds
    public int currentSongIndex = 0;
    public boolean isShuffle = false;
    public boolean isRepeat = false;
    public Handler mHandler = new Handler();
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    public ImageView songImage;
    private SensorManager sensormanager;
    private Sensor accelometerSensor;
    private Sensor proximitySensor;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD_MIN = 600;
    private static final int SHAKE_THRESHOLD_MAX = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = (ImageButton) findViewById(R.id.playButton);
        btnForward = (ImageButton) findViewById(R.id.forwarddButton);
        btnBackward = (ImageButton) findViewById(R.id.backwardButton);
        btnNext = (ImageButton) findViewById(R.id.nextButton);
        btnPrevious = (ImageButton) findViewById(R.id.previousButton);
        btnPlaylist = (ImageButton) findViewById(R.id.playlistButton);
        btnRepeat = (ImageButton) findViewById(R.id.repeatButton);
        btnShuffle = (ImageButton) findViewById(R.id.shuffleButton);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.SongTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        songImage = (ImageView) findViewById(R.id.SongImage);
        sensormanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelometerSensor = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensormanager.registerListener(this, accelometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        proximitySensor = sensormanager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensormanager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        mp = new MediaPlayer();
        songManager = new SongManager();
        utils = new Utilities();
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
        songsList = songManager.getPlayList();
        playSong(0);
        addImageToSong(currentSongIndex);
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.btn_play);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.btn_pause);
                    }
                }

            }
        });
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if (isShuffle) {
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                    addImageToSong(currentSongIndex);
                } else {
                    if (currentSongIndex < (songsList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                        addImageToSong(currentSongIndex);
                    } else {
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                        addImageToSong(currentSongIndex);
                    }
                }

            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    Random rand = new Random();
                    currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                    playSong(currentSongIndex);
                    addImageToSong(currentSongIndex);
                } else {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                        addImageToSong(currentSongIndex);
                    } else {
                        // play last song
                        playSong(songsList.size() - 1);
                        currentSongIndex = songsList.size() - 1;
                        addImageToSong(currentSongIndex);
                    }
                }

            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });
        btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.btn_shuffle);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Playlist.class);
                i.putExtra("Index", currentSongIndex);
                startActivityForResult(i, 100);
            }
        });
    }

    private void addImageToSong(int currentSongIndex) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        byte[] rawArt;
        Bitmap art = null;
        BitmapFactory.Options bfo = new BitmapFactory.Options();

        mmr.setDataSource(songsList.get(currentSongIndex).get("Path"));
        rawArt = mmr.getEmbeddedPicture();
        if (null != rawArt) {
            art = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
            songImage.setImageBitmap(art);
        } else {
            songImage.setImageResource(R.drawable.adele);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            currentSongIndex = data.getExtras().getInt("Index");
            // play selected song
            playSong(currentSongIndex);
            addImageToSong(currentSongIndex);
        }

    }

    public void playSong(int songIndex) {
        // Play song
        try {
            mp.reset();


            mp.setDataSource(songsList.get(songIndex).get("Path"));
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get("Title");
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.btn_pause);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            // Displaying Total Duration time
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = utils.getProgressPercentage(currentDuration, totalDuration);
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat) {
            // repeat is on play same song again
            playSong(currentSongIndex);
            addImageToSong(currentSongIndex);
        } else if (isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
            addImageToSong(currentSongIndex);
        } else {
            // no repeat or shuffle ON - play next song
            if (currentSongIndex < (songsList.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
                addImageToSong(currentSongIndex);
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
                addImageToSong(currentSongIndex);
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensormanager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensormanager.registerListener(this, accelometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                last_x = x;
                last_y = y;
                last_z = z;

                if (speed > SHAKE_THRESHOLD_MIN && speed < SHAKE_THRESHOLD_MAX) {
                    if (isShuffle) {
                        Random rand = new Random();
                        currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
                        playSong(currentSongIndex);
                        addImageToSong(currentSongIndex);
                    } else {
                        if (currentSongIndex < (songsList.size() - 1)) {
                            playSong(currentSongIndex + 1);
                            currentSongIndex = currentSongIndex + 1;
                            addImageToSong(currentSongIndex);
                        } else {
                            // play first song
                            playSong(0);
                            currentSongIndex = 0;
                            addImageToSong(currentSongIndex);
                        }
                    }
                    Toast.makeText(this, "Shake The Song", Toast.LENGTH_SHORT).show();

                }
            }
        }
        float distance = event.values[0];
        if (distance == 5.0) {
            if (mp.isPlaying()) {
                if (mp != null) {
                    mp.pause();
                    // Changing button image to play button
                    btnPlay.setImageResource(R.drawable.btn_play);
                }
            } else {
                // Resume song
                if (mp != null) {
                    mp.start();
                    // Changing button image to pause button
                    btnPlay.setImageResource(R.drawable.btn_pause);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
