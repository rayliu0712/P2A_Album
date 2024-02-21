package rl.p2a.struct;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

public class MediaStruct {
    public final Drawable thumbnailDrawable;
    public final Uri uri;
    public final String absPath;
    public final String date;

    public MediaStruct(Drawable thumbnailDrawable, Uri uri, String absPath, String date) {
        this.thumbnailDrawable = thumbnailDrawable;
        this.uri = uri;
        this.absPath = absPath;
        this.date = date;
    }
}