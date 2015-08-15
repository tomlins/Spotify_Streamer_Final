package net.tomlins.android.udacity.spotifystreamer.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.tomlins.android.udacity.spotifystreamer.R;

public class ArtistTopTracksActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_top_tracks);

        // Retrieve and set the subtitle for the action bar to the selected artist
        Intent intent = getIntent();
        String artistId = intent.getStringExtra(SearchResultsFragment.ARTIST_ID);
        String artistName = intent.getStringExtra(SearchResultsFragment.ARTIST_NAME);
        if (artistName != null && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(artistName);
        }

        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle args = new Bundle();
            args.putString(SearchResultsFragment.ARTIST_ID, artistId);
            args.putString(SearchResultsFragment.ARTIST_NAME, artistName);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(args);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.artist_top_tracks_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_todo_coming_soon, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
