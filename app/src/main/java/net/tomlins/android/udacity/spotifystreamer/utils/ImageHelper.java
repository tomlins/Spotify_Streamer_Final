package net.tomlins.android.udacity.spotifystreamer.utils;

import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by jasontomlins on 25/06/2015.
 */
public class ImageHelper {

    public static String findSmallestImage(List<Image> images) {
        Iterator it = images.iterator();
        String url = null;

        // insanely large starting value for smallestWidth to kick off
        int smallestWidth=10000;

        while (it.hasNext()) {
            Image image = (Image) it.next();
            if (image.width < smallestWidth) {
                url = image.url;
                smallestWidth = image.width;
            }
        }
        return url;
    }

    public static String findImageClosestToWidth(List<Image> images, int width) {
        Iterator it = images.iterator();
        int currentWidth = 0;
        String url = null;

        while (it.hasNext()) {
            Image image = (Image) it.next();
            if (image.width <= width && image.width > currentWidth) {
                url = image.url;
                currentWidth = image.width;
            }
        }
        return url;
    }
}
