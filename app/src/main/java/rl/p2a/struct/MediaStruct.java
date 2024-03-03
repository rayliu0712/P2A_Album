package rl.p2a.struct;

import android.graphics.drawable.Drawable;
import android.net.Uri;

public class MediaStruct {
    public final boolean isVideo;
    public Drawable thumbnailDrawable = null;
    public final long id;
    public final Uri uri;
    public final String absPath;
    public final String date;

    public MediaStruct(boolean isVideo, long id, Uri uri, String absPath, String date) {
        this.isVideo = isVideo;
        this.id = id;
        this.uri = uri;
        this.absPath = absPath;
        this.date = date;
    }
}