package net.tomlins.android.udacity.spotifystreamer.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.service.MediaPlayerService;
import net.tomlins.android.udacity.spotifystreamer.utils.ConnectivityHelper;
import net.tomlins.android.udacity.spotifystreamer.utils.ParcelableTrack;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasontomlins on 13/08/2015.
 */
public class MediaPlayerDialogFragment extends DialogFragment
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    public static final String LOG_TAG = MediaPlayerDialogFragment.class.getSimpleName();
    public static final String FRAG_TAG = "MP_FRAG";

    private ArrayList<ParcelableTrack> trackListing;
    private int trackIdx;
    private String trackUrl;

    private SeekBar seekBar;
    private int currentSeekPosition = 0;

    private TextView artistTextView;
    private TextView albumNameTextView;
    private ImageView albumArt;
    private TextView trackNameTextView;
    private ImageButton playPauseButton;
    private TextView timeElapsedTextView;
    private TextView timeRemainingTextView;

    private static MediaPlayerService mService;
    private boolean mBound = false;

    private ProgressDialog progressDialog;

    private Handler updateSeekBarHandler;
    private SeekBarRunnable updateSeekBarRunnable;
    private static boolean killRunnable = false;
    private boolean buffering = false;
    private boolean playFinished = false;


    public MediaPlayerDialogFragment() {

        // empty constructor
    }

    static MediaPlayerDialogFragment newInstance(ArrayList<ParcelableTrack> trackListing, int trackIdx) {
        Log.d(LOG_TAG, "Instantiating new MediaPlayerDialogFragment instance");

        MediaPlayerDialogFragment frag = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("trackListing", trackListing);
        args.putInt("trackIdx", trackIdx);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /**
         * The system calls this only when creating the layout in a dialog.
         * The only reason you might override this method when using onCreateView() is
         * to modify any dialog characteristics. For example, the dialog includes a
         * title by default, but your custom layout might not need it. So here you can
         * remove the dialog title, but you must call the superclass to get the Dialog.
         */
        Log.d(LOG_TAG, "onCreateDialog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        Bundle args = getArguments();
        if (args == null) {
            // MediaPlayerDialogFragment must be called via newInstance and have args
            return null;
        }

        trackListing = args.getParcelableArrayList("trackListing");

        if (savedInstanceState != null) {
            buffering = savedInstanceState.getBoolean("buffering", false);
            trackIdx = savedInstanceState.getInt("trackIdx", 0);
            playFinished = savedInstanceState.getBoolean("playFinished", true);
        } else {
            trackIdx = args.getInt("trackIdx");
        }
        ParcelableTrack currentTrack = trackListing.get(trackIdx);
        trackUrl = currentTrack.getTrackUrl();

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);
        artistTextView = (TextView) view.findViewById(R.id.artist);
        albumNameTextView = (TextView) view.findViewById(R.id.album);
        albumArt = (ImageView) view.findViewById(R.id.album_art);
        trackNameTextView = (TextView) view.findViewById(R.id.album_track);
        timeElapsedTextView = (TextView) view.findViewById(R.id.track_time_elapsed);
        timeRemainingTextView = (TextView) view.findViewById(R.id.track_time_remaining);

        ImageButton previousButton = (ImageButton) view.findViewById(R.id.previous_button);
        previousButton.setOnClickListener(this);

        playPauseButton = (ImageButton) view.findViewById(R.id.play_pause_button);
        playPauseButton.setOnClickListener(this);
        if (playFinished)
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);

        ImageButton nextButton = (ImageButton) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(this);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        updateView(currentTrack);

        return view;
    }

    private void updateView(ParcelableTrack currentTrack) {
        Log.d(LOG_TAG, "updateView - updating view");
        artistTextView.setText(currentTrack.getArtistName());
        albumNameTextView.setText(currentTrack.getAlbumName());
        String albumArtUrl = currentTrack.getAlbumArtUrl();
        Picasso.with(getActivity()).load(albumArtUrl).into(albumArt);
        trackNameTextView.setText(currentTrack.getTrackName());
        timeRemainingTextView.setText(R.string.seek_bar_clip_duration);
//        if (mService!=null) {
//            seekBar.setProgress(mService.getSeekTo());
//            //seekBar.setMax(mService.getDuration());
//        }
    }

    @Override
    public void onClick(View v) {
        Log.d(LOG_TAG, "onClick called");
        switch (v.getId()) {

            case R.id.play_pause_button:
                Log.d(LOG_TAG, "play/pause pressed");
                mService.playPause(trackIdx);
                if (mService.isPaused()) {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
                }
                break;

            case R.id.next_button:
                Log.d(LOG_TAG, "next pressed");
                nextTrack();
                break;

            case R.id.previous_button:
                Log.d(LOG_TAG, "next pressed");
                previousTrack();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart called with track index " + trackIdx);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause called. un-registered broadcastreciever, clicklistener and handler");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mediaPlayerBroadcastReceiver);
        //playPauseButton.setOnClickListener(null);
        killRunnable = true;
        if (updateSeekBarHandler!=null)
            updateSeekBarHandler.removeCallbacks(null);
        if (progressDialog!=null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume called. Track position at " + currentSeekPosition);

        if (!ConnectivityHelper.isConnectedToInternet(getActivity())) {
            Log.d(LOG_TAG, "No internet connection");
            Toast.makeText(getActivity(), R.string.toast_check_connection, Toast.LENGTH_LONG).show();
            return;
        }

        // register our receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mediaPlayerBroadcastReceiver,
                new IntentFilter("net.tomlins.android.udacity.spotifystreamer.service"));
        Log.d(LOG_TAG, "Broadcast receiver registered");

        if (playFinished)
            return;

        if (buffering) {
            playRequested();
            return;
        }

        if (mService == null) {
            Log.d(LOG_TAG, "Launching new service...");
            Intent i = new Intent(getActivity(), MediaPlayerService.class);
            i.putExtra("trackUrl", trackUrl);
            i.putExtra("currentlyPlayingIdx", trackIdx);
            getActivity().getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
        } else {
            Log.d(LOG_TAG, "Using existing service");

            if (mService.isPlaying() || mService.isPaused()) {
                if (trackIdx == mService.getCurrentlyPlayingIdx()) {
                    // same track so just resume seekbar progress
                    Log.d(LOG_TAG, "User reselected existing playing track");
                    playStarted();
                    return;
                } else {
                    // player currently playing but user now selected different track so reset player
                    // and play new track (after if statement below)
                    Log.d(LOG_TAG, "Playing but now user has selected different track so reset player");
                    mService.resetPlayer();
                }
            }

            mService.play(trackUrl, trackIdx);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("buffering", buffering);
        outState.putInt("trackIdx", trackIdx);
        outState.putBoolean("playFinished", playFinished);
    }

    private void playRequested() {
        buffering = true;
        playFinished = false;
        progressDialog = ProgressDialog.show(getActivity(),
                getString(R.string.dialog_buffering),
                getString(R.string.dialog_please_wait),
                true);
    }

    private void playStarted() {
        Log.d(LOG_TAG, "Play Started");
        buffering = false;
        seekBar.setMax(mService.getDuration());

        if (mService.isPaused())
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);

        updateSeekBarHandler = new Handler();
        updateSeekBarRunnable = new SeekBarRunnable();
        killRunnable = false;
        updateSeekBarHandler.post(updateSeekBarRunnable);

        if (progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();

    }

    private void nextTrack() {
        Log.d(LOG_TAG, "Next track");
        if (trackIdx == trackListing.size()-1) {
            Log.d(LOG_TAG, "Already on last track");
            return;
        }
        trackIdx = trackIdx + 1;
        changeTrack();
    }

    private void previousTrack() {
        Log.d(LOG_TAG, "Previous track");
        if (trackIdx == 0) {
            Log.d(LOG_TAG, "Already on first track");
            return;
        }
        trackIdx = trackIdx - 1;
        changeTrack();
    }

    private void changeTrack() {
        ParcelableTrack currentTrack = trackListing.get(trackIdx);
        trackUrl = currentTrack.getTrackUrl();
        updateView(currentTrack);
        mService.resetPlayer();
        mService.play(trackUrl, trackIdx);

    }

    private void playCompleted() {
        Log.d(LOG_TAG, "Play Completed");
        killRunnable = true;
        playFinished = true;
        updateSeekBarHandler.removeCallbacks(null);
        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        seekBar.setProgress(0);
        timeElapsedTextView.setText(R.string.seek_bar_zero_time);
        timeRemainingTextView.setText(convertMillisToMinsSecs(seekBar.getMax()));
    }


    private BroadcastReceiver mediaPlayerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra(MediaPlayerService.BROADCAST_MESSAGE, -1);
            switch (message) {

                case MediaPlayerService.PLAY_REQUESTED:
                    playRequested();
                    break;

                case MediaPlayerService.PLAY_STARTED:
                    playStarted();
                    break;

                case MediaPlayerService.PLAY_COMPLETED:
                    playCompleted();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            Log.d(LOG_TAG, "onServiceConnected - service bound");
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            Log.d(LOG_TAG, "onServiceDisconnected - service unbound");
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            mService.setCurrentSeekPosition(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    /**
     * Utility method to conver millis into MM:SS
     * @param millis
     * @return String MM:SS
     */
    private String convertMillisToMinsSecs(long millis) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    private class SeekBarRunnable implements Runnable {
        public final String LOG_TAG = SeekBarRunnable.class.getSimpleName();
        private long trackDuration = mService.getDuration();
        private long timeRemaining = 0;
        @Override
        public void run() {
            Log.d(LOG_TAG, "handler run called, kill bool is " + killRunnable);
            if (mService != null && !killRunnable) {
                Log.d(LOG_TAG, "setting seek bar");
                long currentSeekPosition = mService.getCurrentSeekPosition();
                timeRemaining = trackDuration - currentSeekPosition;

                timeElapsedTextView.setText(convertMillisToMinsSecs(currentSeekPosition));
                timeRemainingTextView.setText(convertMillisToMinsSecs(timeRemaining));
                seekBar.setProgress((int)currentSeekPosition);
            }
            if (!killRunnable)
                updateSeekBarHandler.postDelayed(updateSeekBarRunnable, 1000);
        }
    }

}
