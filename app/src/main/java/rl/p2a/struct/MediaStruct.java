package rl.p2a.struct;

import android.graphics.Bitmap;
import android.net.Uri;

public class MediaStruct {

    public final Bitmap thumbnailBitmap;
    public final Uri uri;
    public final String absPath;
    public final String date;

    public MediaStruct(Bitmap thumbnailBitmap, Uri uri, String absPath, String date) {
        this.thumbnailBitmap = thumbnailBitmap;
        this.uri = uri;
        this.absPath = absPath;
        this.date = date;
    }
}