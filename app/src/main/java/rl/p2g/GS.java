package rl.p2g;

import java.util.ArrayList;

// Gallery Struct
public class GS {
    public final String galleryName;
    public final ArrayList<PS> psList = new ArrayList<>();

    public GS(String galleryName) {
        this.galleryName = galleryName;
    }
}