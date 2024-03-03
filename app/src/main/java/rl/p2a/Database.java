package rl.p2a;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;

import java.util.ArrayList;

import rl.p2a.adapter.CellsAdapter;
import rl.p2a.fragment.AlbumsFragment;
import rl.p2a.fragment.CellsFragment;
import rl.p2a.struct.AlbumStruct;
import rl.p2a.struct.MediaStruct;

public class Database {
    public static int iMedia = 0;
    public static int iAlbum = -1;
    public static final ArrayList<MediaStruct> allMediaList = new ArrayList<>();
    public static final ArrayList<AlbumStruct> albumList = new ArrayList<>();

    public static void getThumbnailsAndSetLists(MainActivity ma) {
        ArrayList<String> albumNameList = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Uri externalContentUri =
                    i == 0 ? Video.Media.EXTERNAL_CONTENT_URI : Images.Media.EXTERNAL_CONTENT_URI;

            Cursor cursor = ma.getContentResolver().query(
                    externalContentUri,
                    new String[]{"date_added", "_data", "_id"},
                    null, null, "date_added DESC");

            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                    String absPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    Uri uri = Uri.parse(externalContentUri + "/" + id);
                    allMediaList.add(new MediaStruct(i == 0, id, uri, absPath, date));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        allMediaList.sort((o1, o2) -> o2.date.compareTo(o1.date));
        CellsFragment.rv.setAdapter(new CellsAdapter(ma));

        new Thread(() -> {
            for (int i = 0; i < allMediaList.size(); i++) {
                MediaStruct ms = allMediaList.get(i);

                ms.thumbnailDrawable = new BitmapDrawable(ma.getResources(), ms.isVideo ?
                        Video.Thumbnails.getThumbnail(ma.getContentResolver(), ms.id, Video.Thumbnails.MINI_KIND, null) :
                        Images.Thumbnails.getThumbnail(ma.getContentResolver(), ms.id, Images.Thumbnails.MINI_KIND, null)
                );

                final int fI = i;
                ma.handler.post(() -> {
                    try {
                        // 在全部載入完之前點進BedFragment會crash
                        CellsFragment.rv.getAdapter().notifyItemChanged(fI);
                    } catch (Exception ignored) {
                    }
                });


                // 潛在問題:同名不同路徑
                String[] split = ms.absPath.split("/");
                String albumName = split[split.length - 2];
                int index = albumNameList.indexOf(albumName);
                if (index == -1) {
                    if (albumName.equals("Camera"))
                        albumList.add(0, new AlbumStruct(albumName));
                    else if (albumName.equals("Screenshots"))
                        albumList.add(albumNameList.size() > 0 ? 1 : 0, new AlbumStruct(albumName));
                    else
                        albumList.add(new AlbumStruct(albumName));

                    albumNameList.add(albumName);
                    index = albumNameList.size() - 1;


                    final int fIndex = index;
                    ma.handler.post(() -> {
                        try {
                            // 在全部載入完之前點進BedFragment會crash
                            AlbumsFragment.rv.getAdapter().notifyItemChanged(fIndex);
                        } catch (Exception ignored) {
                        }
                    });
                }
                getAlbum(index).addMedia(ms);
            }

            ma.handler.post(() -> EzTools.toast(ma, "finished"));
        }).start();
    }

    public static AlbumStruct getAlbum(int i) {
        if (i == -1)
            return new AlbumStruct("Photos", allMediaList);
        else
            return albumList.get(i);
    }

    public static AlbumStruct getAlbum() {
        return getAlbum(iAlbum);
    }
}
