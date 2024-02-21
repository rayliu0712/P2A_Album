package rl.p2a;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;

import java.util.ArrayList;

import rl.p2a.struct.AlbumStruct;
import rl.p2a.struct.MediaStruct;

public class Database {
    public static int iMedia = 0;
    public static int iAlbum = -1;
    public static ArrayList<MediaStruct> allMediaList = new ArrayList<>();
    public static ArrayList<AlbumStruct> albumList = new ArrayList<>();

    public static void getThumbnailsAndSetLists(MainActivity cc, Handler handler) {

        new Thread(() -> {

            for (int i = 0; i < 2; i++) {
                Uri externalContentUri;
                if (i == 0)
                    externalContentUri = Images.Media.EXTERNAL_CONTENT_URI;
                else
                    externalContentUri = Video.Media.EXTERNAL_CONTENT_URI;

                Cursor cursor = cc.getContentResolver().query(
                        externalContentUri,
                        new String[]{"date_added", "_data", "_id"},
                        null,
                        null,
                        "date_added ASC");  // ascending order

                int k = 0;
                cc.pb.setMax(cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));

                        String absPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));

                        long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                        Uri uri = Uri.parse(externalContentUri + "/" + id);

                        Drawable thumbnailDrawable;
                        if (i == 0)
                            thumbnailDrawable = new BitmapDrawable(Images.Thumbnails.getThumbnail(cc.getContentResolver(), id, Images.Thumbnails.MINI_KIND, null));
                        else
                            thumbnailDrawable = new BitmapDrawable(Video.Thumbnails.getThumbnail(cc.getContentResolver(), id, Video.Thumbnails.MINI_KIND, null));

                        allMediaList.add(new MediaStruct(thumbnailDrawable, uri, absPath, date));

                        k++;

                        int finalK = k;
                        handler.post(() -> {
                            cc.pb.setProgress(finalK);
                        });

                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            allMediaList.sort((o1, o2) -> o2.date.compareTo(o1.date));

            ArrayList<String> albumNameList = new ArrayList<>();
            for (MediaStruct ms : allMediaList) {
                String[] array = ms.absPath.split("/");
                String albumName = array[array.length - 2];
                int index = albumNameList.indexOf(albumName);

                // create new album
                if (index == -1) {
                    albumList.add(new AlbumStruct(albumName));
                    albumNameList.add(albumName);
                    index = albumNameList.size() - 1;
                }
                getAlbum(index).addMedia(ms);
            }

            handler.post(() -> {
                cc.switchDisplayedFragment(MainActivity.INIT, -1, false);
            });
        }).start();
    }


    public static AlbumStruct getAlbum(int i) {
        if (i == -1)
            return new AlbumStruct(allMediaList);
        else
            return albumList.get(i);
    }

    public static AlbumStruct getAlbum() {
        return getAlbum(iAlbum);
    }
}
