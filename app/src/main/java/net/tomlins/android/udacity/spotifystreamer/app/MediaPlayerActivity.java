package net.tomlins.android.udacity.spotifystreamer.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.tomlins.android.udacity.spotifystreamer.R;

public class MediaPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        // Retrieve and set the subtitle for the action bar to the selected artist
//        Intent intent = getIntent();
//        String artistId = intent.getStringExtra(SearchResultsFragment.ARTIST_ID);
//        String artistName = intent.getStringExtra(SearchResultsFragment.ARTIST_NAME);
//        if (artistName != null && getSupportActionBar() != null) {
//            getSupportActionBar().setSubtitle(artistName);
//        }

        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle args = new Bundle();
//            args.putString(SearchResultsFragment.ARTIST_ID, artistId);
//            args.putString(SearchResultsFragment.ARTIST_NAME, artistName);

            MediaPlayerDialogFragment fragment = new MediaPlayerDialogFragment();
            fragment.setArguments(args);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.media_player_container, fragment)
                    .commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media_player, menu);
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
