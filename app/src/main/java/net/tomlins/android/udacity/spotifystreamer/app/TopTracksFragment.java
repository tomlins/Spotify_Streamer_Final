package net.tomlins.android.udacity.spotifystreamer.app;


import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.adapter.ArtistTopTracksAdapter;

import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopTracksFragment extends ListFragment {

    public static final String LOG_TAG = TopTracksFragment.class.getSimpleName();
    private ArtistTopTracksAdapter adapter;
    private ProgressDialog progressDialog;
    private ListView rootView;
    private String currentArtistId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView called");

        // Preserves state, e.g. on rotation
        setRetainInstance(true);

        // Inflate the layout for this list fragment
        rootView = (ListView)inflater.inflate(
                R.layout.fragment_top_tracks_list_view,
                container,
                false);

        return rootView;
    }

    /**
     * When this fragment starts, launch the AsyncTask to retrieve the top
     * tracks for the selected artist if a new artist selection has been made
     */
    @Override
    public void onStart() {
        super.onStart();
        Intent intent = getActivity().getIntent();
        String artistId = intent.getStringExtra(SearchResultsFragment.ARTIST_ID);
        if (!artistId.equals(currentArtistId)) {
            // Load top tracks only if different artist selected, i.e. not on rotation
            currentArtistId = artistId;
            new FetchArtistTopTracksAsyncTask().execute(artistId);

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG, "onListItemClick called, list position " + position);
        Toast.makeText(getActivity(), R.string.toast_todo_coming_soon, Toast.LENGTH_SHORT).show();
    }

    /**
     * Retrieves top tracks for the specified artist and sets the adapter and list view
     */
    private class FetchArtistTopTracksAsyncTask extends AsyncTask<String, Void, Tracks> {

        public final String LOG_TAG = FetchArtistTopTracksAsyncTask.class.getSimpleName();
        private final SpotifyService spotifyService = new SpotifyApi().getService();

        @Override
        protected Tracks doInBackground(String... artistId) {
            Log.d(LOG_TAG, "doInBackground called. Retrieving top tracks for ID " + artistId[0]);

            Tracks tracks = null;
            try {
                HashMap<String, Object> params = new HashMap<>();
                params.put("country", getString(R.string.country_code));
                tracks = spotifyService.getArtistTopTrack(artistId[0], params);
            } catch (Exception x) {
                Log.e(LOG_TAG, "Error calling Spotify API", x);
            }
            return tracks;
        }

        @Override
        protected void onPreExecute() {
            // Display a please wait dialog that is displayed whilst
            // background task in progress
            progressDialog = ProgressDialog.show(getActivity(),
                    getString(R.string.progress_dialog_top_tracks_title),
                    getString(R.string.progress_dialog_please_wait),
                    true);
        }

        @Override
        protected void onPostExecute(Tracks tracks) {
            Log.d(LOG_TAG, "onPostExecute called.");

            try {
                progressDialog.dismiss();
            } catch (Exception x) {
                Log.d(LOG_TAG, "Progress dialog detached from view");
            }

            if (tracks == null || tracks.tracks.size() == 0) {
                Toast.makeText(getActivity(), R.string.toast_no_tracks_found, Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(LOG_TAG, "Retrieved " + tracks.tracks.size() + " tracks");

            adapter = new ArtistTopTracksAdapter(getActivity(), tracks.tracks);
            setListAdapter(adapter);

        }
    }


}
