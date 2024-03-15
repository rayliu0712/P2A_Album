package rl.p2a;

import java.util.ArrayList;

import rl.p2a.struct.AlbumStruct;
import rl.p2a.struct.MediaStruct;

public class Database {
    public static int iMedia = 0;
    public static int iAlbum = -1;
    public static final ArrayList<MediaStruct> allMediaList = new ArrayList<>();
    public static final ArrayList<AlbumStruct> albumList = new ArrayList<>();

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