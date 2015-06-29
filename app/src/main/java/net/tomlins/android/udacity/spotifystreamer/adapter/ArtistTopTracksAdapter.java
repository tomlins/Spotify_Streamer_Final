package net.tomlins.android.udacity.spotifystreamer.adapter;

import android.app.Activity;
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

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by jasontomlins on 24/06/2015.
 */
public class ArtistTopTracksAdapter extends ArrayAdapter<Track> {

    public static final String TAG = ArtistTopTracksAdapter.class.getSimpleName();
    private final Activity context;

    static class ViewHolder {
        ImageView albumImageView;
        TextView trackName;
        TextView albumTitle;
        int index;
    }

    public ArtistTopTracksAdapter(Activity context, List<Track> tracks) {
        super(context, R.layout.fragment_top_tracks_list_view, tracks);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(TAG, "getView called");

        ViewHolder viewHolder;
        Track track = getItem(position);

        if (view == null) {
            Log.d(TAG, "Creating new row view instance");

            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.top_tracks_list_item, null, true);

            viewHolder = new ViewHolder();
            viewHolder.index = position;
            viewHolder.albumImageView = (ImageView) view.findViewById(R.id.albumImageView);
            viewHolder.trackName = (TextView) view.findViewById(R.id.trackName_textView);
            viewHolder.albumTitle = (TextView) view.findViewById(R.id.albumTitle_textView);
            view.setTag(viewHolder);

        } else {
            Log.d(TAG, "Reusing existing row view");
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.index = position;
        }

        // Retrieve the smallest image available to use for the image view thumbnail
        String url = ImageHelper.findSmallestImage(track.album.images);
        if (url == null) {
            Log.d(TAG, "No image available for " + track.album.name + ", using default");
            viewHolder.albumImageView.setImageResource(R.drawable.image_not_available);
        } else {
            Picasso.with(context).load(url).into(viewHolder.albumImageView);
        }

        viewHolder.trackName.setText(track.name);
        viewHolder.albumTitle.setText(track.album.name);

        return view;
    }

}
