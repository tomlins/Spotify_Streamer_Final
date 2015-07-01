package net.tomlins.android.udacity.spotifystreamer.app;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.adapter.SearchResultsListArrayAdapter;
import net.tomlins.android.udacity.spotifystreamer.utils.ConnectivityHelper;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchResultsFragment extends ListFragment {

    public static final String LOG_TAG = SearchResultsFragment.class.getSimpleName();
    public static final String ARTIST_ID = "ARTIST_ID";
    public static final String ARTIST_NAME = "ARTIST_NAME";

    private SearchResultsListArrayAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView rootView;
    private View emptyView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView called");

        // Preserves state, e.g. on rotation
        setRetainInstance(true);

        // Inflate the view for use when no artist search results
        // to display, ie app launch
        emptyView = inflater.inflate(
                R.layout.empty_view,
                container,
                false);

        // Inflate the list view
        rootView = (ListView)inflater.inflate(
                R.layout.fragment_search_results_list_view,
                container,
                false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated called");

        // Add the empty view to the view group
        ((ViewGroup)getListView().getParent()).addView(emptyView);

        // when no search results to display, ie on app launch
        // set the view to the empty view
        getListView().setEmptyView(emptyView);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG, "onListItemClick called, list position " + position);

        // Check internet connectivity before making launching new intent
        if (!ConnectivityHelper.isConnectedToInternet(getActivity())) {
            Log.d(LOG_TAG, "No internet connection");
            Toast.makeText(getActivity(),
                    R.string.toast_check_connection, Toast.LENGTH_LONG).show();
            return;
        }

        // Invoke intent passing selected artist id to retrieve top
        // tracks and artist name to use in subtitle
        Artist artist = (Artist)getListView().getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), ArtistTopTracksActivity.class);
        intent.putExtra(ARTIST_ID, artist.id);
        intent.putExtra(ARTIST_NAME, artist.name);
        startActivity(intent);
    }

    public void doSearch(final String query) {
        Log.d(LOG_TAG, "doSearch called with query, " + query);

        new SearchArtistAsyncTask().execute(query);
    }

    /**
     * AsyncTask to search for artist entered by user in the search action bar and
     * update the adapter and list view
     */
    private class SearchArtistAsyncTask extends AsyncTask<String, Void, ArtistsPager> {

        public final String LOG_TAG = SearchArtistAsyncTask.class.getSimpleName();
        private final SpotifyService spotifyService = new SpotifyApi().getService();

        @Override
        protected ArtistsPager doInBackground(String... query) {
            Log.d(LOG_TAG, "doInBackground called");

            ArtistsPager results = null;
            try {
                results = spotifyService.searchArtists(query[0]);
            } catch (Exception x) {
                Log.e(LOG_TAG, "Error calling Spotify API", x);
            }
            return results;
        }

        @Override
        protected void onPreExecute() {
            // Display a please wait dialog that is displayed whilst
            // background task in progress
            progressDialog = ProgressDialog.show(getActivity(),
                    getString(R.string.progress_dialog_search_artist_title),
                    getString(R.string.progress_dialog_please_wait),
                    true);
        }

        @Override
        protected void onPostExecute(ArtistsPager artistsPager) {
            Log.d(LOG_TAG, "onPostExecute called.");

            try {
                progressDialog.dismiss();
            } catch (Exception x) {
                Log.d(LOG_TAG, "Progress dialog detached from view");
            }

            if (artistsPager == null || artistsPager.artists.items.size() == 0) {
                Toast.makeText(getActivity(), R.string.toast_artist_not_found, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(LOG_TAG, "Retrieved " + artistsPager.artists.items.size() + " artists");

            // reset search action bar
            getFragmentManager().invalidateOptionsMenu();

            adapter = new SearchResultsListArrayAdapter(getActivity(), artistsPager.artists.items);
            setListAdapter(adapter);

        }
    }

}
