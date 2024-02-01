package rl.p2g;

import android.graphics.Bitmap;
import android.net.Uri;

// Photo Struct
public class PS {
    public final String date;
    public final Uri uri;
    public final Bitmap bitmap;
    public final String absPath;

    public PS(String date, Uri uri, Bitmap bitmap, String absPath) {
        this.date = date;
        this.uri = uri;
        this.bitmap = bitmap;
        this.absPath = absPath;
    }
}
