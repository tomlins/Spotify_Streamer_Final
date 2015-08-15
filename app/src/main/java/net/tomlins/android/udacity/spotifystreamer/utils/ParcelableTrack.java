package net.tomlins.android.udacity.spotifystreamer.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jason on 15/08/15.
 */
public class ParcelableTrack implements Parcelable {

    private String artistName;
    private String trackName;
    private String albumName;
    private String albumArtUrl;
    private String trackUrl;
    private long duration;

    public ParcelableTrack(String artistName, String trackName, String albumName,
                           String albumArtUrl, String trackUrl, long duration) {
        this.artistName = artistName;
        this.trackName = trackName;
        this.albumName = albumName;
        this.albumArtUrl = albumArtUrl;
        this.trackUrl = trackUrl;
        this.duration = duration;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public long getDuration() {
        return duration;
    }

    protected ParcelableTrack(Parcel in) {
        artistName = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        albumArtUrl = in.readString();
        trackUrl = in.readString();
        duration = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artistName);
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(albumArtUrl);
        dest.writeString(trackUrl);
        dest.writeLong(duration);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ParcelableTrack> CREATOR = new Parcelable.Creator<ParcelableTrack>() {
        @Override
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        @Override
        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };
}