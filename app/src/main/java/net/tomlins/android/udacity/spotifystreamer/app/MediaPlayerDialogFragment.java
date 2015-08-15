package net.tomlins.android.udacity.spotifystreamer.app;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.SeekBar;

import net.tomlins.android.udacity.spotifystreamer.R;

/**
 * Created by jasontomlins on 13/08/2015.
 */
public class MediaPlayerDialogFragment extends DialogFragment {

    public static final String FRAG_TAG = "MP_FRAG";

    private String artistName;
    private String albumName;
    private ImageView albumArtwork;
    private String trackName;
    private int trackDuration;

    private SeekBar seekBar;


    public MediaPlayerDialogFragment() {
        // empty constructor
    }

    static MediaPlayerDialogFragment newInstance(
//            String artistName,
//            String trackName,
//            String albumName,
//            String albumArtwork
    ) {

        MediaPlayerDialogFragment frag = new MediaPlayerDialogFragment();
        Bundle args = new Bundle();
        //args.putString("title", title);
        frag.setArguments(args);


        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_media_player, container, false);
//        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
//        String title = getArguments().getString("title", "Enter Name");
//        getDialog().setTitle(title);
//        // Show soft keyboard automatically
//        mEditText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        return view;
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

}
