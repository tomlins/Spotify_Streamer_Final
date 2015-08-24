package net.tomlins.android.udacity.spotifystreamer.app;

import android.content.Intent;
import android.support.v4.app.NavUtils;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                //this method requires android:launchMode="singleTop" in the manifest
                //where the parent activity is declared
                //NavUtils.navigateUpFromSameTask(this);

                // this method does not require android:launchMode="singleTop"
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, upIntent);
                return true;

            // Settings action -> place holder only
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
