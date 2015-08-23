package net.tomlins.android.udacity.spotifystreamer.service;


import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by jasontomlins on 16/08/2015.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String LOG_TAG = MediaPlayerService.class.getSimpleName();

    public static final String BROADCAST_MESSAGE = "message";
    public static final int PLAY_REQUESTED = 0;
    public static final int PLAY_STARTED = 1;
    public static final int PLAY_COMPLETED = 2;

    private final IBinder mBinder = new LocalBinder();

    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;
    private int currentlyPlayingIdx = -1;
    private String trackUrl;
    private int seekTo;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MediaPlayerService.this;
        }
    }

    private void sendBroadcast(int message) {
        Intent intent = new Intent("net.tomlins.android.udacity.spotifystreamer.service");
        intent.putExtra(BROADCAST_MESSAGE, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate called");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            // Notify when playback completed
            public void onCompletion(MediaPlayer mp) {
                Log.d(LOG_TAG, "onCompletion called. Playback Complete");
                resetPlayer();
                sendBroadcast(PLAY_COMPLETED);
            }
        });
    }

    public void resetPlayer() {
        Log.d(LOG_TAG, "resetPlayer");
        mediaPlayer.reset();
        isPaused = false;
        currentlyPlayingIdx = -1;
        seekTo = 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind called");
        trackUrl = intent.getStringExtra("trackUrl");
        currentlyPlayingIdx = intent.getIntExtra("currentlyPlayingIdx", -1);

        // When we first bind to service, play track
        play(trackUrl, currentlyPlayingIdx);
        return mBinder;
    }

    public void play(String trackUrl, int trackIdx) {

        Log.d(LOG_TAG, "play called for track " + trackUrl);
        sendBroadcast(PLAY_REQUESTED);

        this.trackUrl = trackUrl;
        this.currentlyPlayingIdx = trackIdx;

        if (mediaPlayer.isPlaying() && trackIdx == currentlyPlayingIdx) {
            Log.d(LOG_TAG, "Already playing this track, ignore.");
            sendBroadcast(PLAY_STARTED);
            return;
        }
        try {
            if (mediaPlayer.isPlaying()) {
                Log.d(LOG_TAG, "Already playing, stop current track and reset");
                mediaPlayer.stop();
                resetPlayer();
            }

            mediaPlayer.setDataSource(trackUrl);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
            Log.d(LOG_TAG, "preparing track " + trackIdx);
            this.currentlyPlayingIdx = trackIdx;
        } catch (IOException x) {
            Log.e(LOG_TAG, "Error playing back track", x);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPrepared - playing, seek position = " + seekTo);
        isPaused = false;
        if (seekTo!=0)
            mp.seekTo(seekTo);
        sendBroadcast(PLAY_STARTED);
        mp.start();
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy called");
        mediaPlayer.release();
        mediaPlayer = null;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentSeekPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getSeekTo() {
        return seekTo;
    }

    public int getCurrentlyPlayingIdx() {
        return currentlyPlayingIdx;
    }

    public void setCurrentSeekPosition(int position) {
        if (mediaPlayer.isPlaying() || isPaused())
            mediaPlayer.seekTo(position);
        else
            seekTo = position;
    }

    public boolean playPause(int trackIdx) {
        if (isPaused) {
            mediaPlayer.start();
            isPaused = false;
            Log.d(LOG_TAG, "playPause play resumed");
        } else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
            Log.d(LOG_TAG, "playPause play paused");
        } else {
            //play(trackUrl, currentlyPlayingIdx);
            play(trackUrl, trackIdx);
        }

        // we return true to indicate state changed
        return true;
    }

}
