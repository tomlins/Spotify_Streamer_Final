package net.tomlins.android.udacity.spotifystreamer.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.tomlins.android.udacity.spotifystreamer.R;

import java.io.IOException;

/**
 * Created by jasontomlins on 13/08/2015.
 */
public class MediaPlayerDialogFragment extends DialogFragment {

    public final String LOG_TAG = MediaPlayerDialogFragment.class.getSimpleName();
    public static final String FRAG_TAG = "MP_FRAG";

    private String trackUrl;
    private long trackDuration;

    private SeekBar seekBar;


    public MediaPlayerDialogFragment() {
        // empty constructor
    }

    static MediaPlayerDialogFragment newInstance(
            String artistName,
            String trackName,
            String albumName,
            String albumArtUrl,
            String trackUrl,
            long duration) {

        MediaPlayerDialogFragment frag = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        args.putString("artistName", artistName);
        args.putString("trackName", trackName);
        args.putString("albumName", albumName);
        args.putString("albumArtUrl", albumArtUrl);
        args.putString("trackUrl", trackUrl);
        args.putLong("duration", duration);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        String title = getArguments().getString("title", "Enter Name");

        TextView artistTextView = (TextView) view.findViewById(R.id.artist);
        artistTextView.setText(getArguments().getString("artistName"));

        TextView albumNameTextView = (TextView) view.findViewById(R.id.album);
        albumNameTextView.setText(getArguments().getString("albumName"));

        ImageView albumArt = (ImageView) view.findViewById(R.id.album_art);
        String albumArtUrl = getArguments().getString("albumArtUrl");
        Picasso.with(getActivity()).load(albumArtUrl).into(albumArt);

        TextView trackNameTextView = (TextView) view.findViewById(R.id.album_track);
        trackNameTextView.setText(getArguments().getString("trackName"));

        trackUrl = getArguments().getString("trackUrl");


        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        String url = trackUrl;
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException x) {
            Log.d(LOG_TAG, "Error playing back track", x);
        }
    }

}
