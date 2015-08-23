package net.tomlins.android.udacity.spotifystreamer.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.utils.ConnectivityHelper;

import kaaes.spotify.webapi.android.models.Artist;


public class MainActivity extends ActionBarActivity implements SearchResultsFragment.Callback {

    public final String LOG_TAG = MainActivity.class.getSimpleName();
    private SearchView searchView;
    private boolean mMasterDetailView = false;
    public static final String TOPTRACKSFRAGMENT_TAG = "TTFRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate called");

        // pull the appropriate view for phone/tablet dir
        setContentView(R.layout.activity_main);

        // Are we on phone or tablet?
        // TODO: we could replace this check with the value in the bools.xml file
        if (findViewById(R.id.artist_top_tracks_container) != null) {
            // If we find this container in our view, we must be using the one for the tablet
            // therefore, load the TopTracksFragment into the container placeholder
            mMasterDetailView = true;
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.artist_top_tracks_container, new TopTracksFragment(), TOPTRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mMasterDetailView = false;
            getSupportActionBar().setElevation(0f);
        }

    }


    @Override
    public void onNewIntent(Intent intent) {
        Log.d(LOG_TAG, "onNewIntent called");

        // Search intent received
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchView.clearFocus();

            // Check connectivity
            if (!ConnectivityHelper.isConnectedToInternet(this)) {
                Log.d(LOG_TAG, "No internet connection");
                Toast.makeText(getApplicationContext(),
                        R.string.toast_check_connection, Toast.LENGTH_LONG).show();
                return;
            }

            // Obtain the search query and perform the artist search
            String query = intent.getStringExtra(SearchManager.QUERY);

            // Get a handle on this activities fragment that will perform the search
            // and display results
            SearchResultsFragment searchResultsFragment =
                    (SearchResultsFragment) getFragmentManager().findFragmentById(R.id.fragment_main);

            searchResultsFragment.doSearch(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu called");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Setup the search view
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView)searchMenuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(),
                    R.string.toast_todo_coming_soon, Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(Artist artist) {

        if (mMasterDetailView) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(SearchResultsFragment.ARTIST_ID, artist.id);
            args.putString(SearchResultsFragment.ARTIST_NAME, artist.name);

            TopTracksFragment fragment = new TopTracksFragment();
            fragment.setArguments(args);

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.artist_top_tracks_container, fragment, TOPTRACKSFRAGMENT_TAG)
                    .commit();
        } else {
            // Invoke intent passing selected artist id to retrieve top
            // tracks and artist name to use in subtitle
            Intent intent = new Intent(this, ArtistTopTracksActivity.class);
            intent.putExtra(SearchResultsFragment.ARTIST_ID, artist.id);
            intent.putExtra(SearchResultsFragment.ARTIST_NAME, artist.name);
            startActivity(intent);
        }


    }

}
