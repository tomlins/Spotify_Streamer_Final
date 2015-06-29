package net.tomlins.android.udacity.spotifystreamer.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.tomlins.android.udacity.spotifystreamer.R;
import net.tomlins.android.udacity.spotifystreamer.utils.ImageHelper;

import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by jasontomlins on 20/06/2015.
 */
public class SearchResultsListArrayAdapter extends ArrayAdapter<Artist> {

    public static final String TAG = SearchResultsListArrayAdapter.class.getSimpleName();
    private final Activity context;

    static class ViewHolder {
        ImageView artistThumbnail;
        TextView artistName;
        int index;
    }

    public SearchResultsListArrayAdapter(Activity context, List<Artist> artists) {
        super(context, R.layout.fragment_search_results_list_view, artists);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(TAG, "getView called");

        ViewHolder viewHolder;
        Artist artist = getItem(position);

        if (view == null) {
            Log.d(TAG, "Creating new row view instance");

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.search_result_list_item, null, true);

            viewHolder = new ViewHolder();
            viewHolder.index = position;
            viewHolder.artistThumbnail = (ImageView) view.findViewById(R.id.artist_thumbnail);
            viewHolder.artistName = (TextView) view.findViewById(R.id.artist_name);
            view.setTag(viewHolder);

        } else {
            Log.d(TAG, "Reusing existing row view");
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.index = position;
        }

        // Retrieve the smallest image available to use for the image view thumbnail
        String url = ImageHelper.findSmallestImage(artist.images);
        if (url == null) {
            Log.d(TAG, "No image available for " + artist.name + " using default");
            viewHolder.artistThumbnail.setImageResource(R.drawable.image_not_available);
        } else {
            Picasso.with(context).load(url).into(viewHolder.artistThumbnail);
        }

        viewHolder.artistName.setText(artist.name);

        return view;
    }

}
