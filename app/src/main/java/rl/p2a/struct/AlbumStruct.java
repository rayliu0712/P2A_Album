package rl.p2a.struct;

import java.util.ArrayList;

public class AlbumStruct {
    public String galleryName;
    public ArrayList<MediaStruct> mediaList = new ArrayList<>();

    public AlbumStruct(String galleryName) {
        this.galleryName = galleryName;
    }

    public AlbumStruct(String galleryName, ArrayList<MediaStruct> mediaList) {
        this.galleryName = galleryName;
        this.mediaList = mediaList;
    }

    public void addMedia(MediaStruct ms) {
        mediaList.add(ms);
    }

    public MediaStruct getMedia(int i) {
        return mediaList.get(i);
    }
}