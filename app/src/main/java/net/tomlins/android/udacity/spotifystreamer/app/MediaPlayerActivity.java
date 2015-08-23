package net.tomlins.android.udacity.spotifystreamer.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.utils.ParcelableTrack;

import java.util.ArrayList;

public class MediaPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        // Retrieve and set the subtitle for the action bar to the selected artist
        Intent intent = getIntent();
        ArrayList<ParcelableTrack> trackListing = intent.getParcelableArrayListExtra("trackListing");
        int trackIdx = intent.getIntExtra("trackIdx", 0);

        // do i really need this state check?
        if (savedInstanceState == null) {
            MediaPlayerDialogFragment dialogFragment =
                    MediaPlayerDialogFragment.newInstance(trackListing, trackIdx);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.media_player_container, dialogFragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_media_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
