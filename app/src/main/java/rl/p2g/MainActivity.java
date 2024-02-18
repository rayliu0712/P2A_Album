package rl.p2g;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Video;
import static rl.p2g.Lazybones.*;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final int INIT = -1;
    public final int CELL_FRAGMENT = 0;
    public final int GALLERY_FRAGMENT = 1;
    public final int BED_FRAGMENT = 2;
    private final int BASIC_PERMISSIONS = 0;

    private int iList;
    private ArrayList<PS> curList;  // ref
    private final ArrayList<PS> photosList = new ArrayList<>();  // all photos
    private final ArrayList<GS> galleriesList = new ArrayList<>();  // sort by gallery

    private final CellFragment cellFragment = new CellFragment();
    private final BedFragment bedFragment = new BedFragment();
    private final GalleryFragment galleryFragment = new GalleryFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkOrGrantBasicPermissions())
            onRequestPermissionsResult(BASIC_PERMISSIONS, null, new int[]{PERMISSION_GRANTED});
    }

    public void hideBar() {
        findViewById(R.id.bar).setVisibility(View.GONE);
    }

    public void showBar() {
        findViewById(R.id.bar).setVisibility(View.VISIBLE);
    }

    public void setPhotoAndGalleryStructList() {
        galleriesList.add(new GS("Camera"));
        galleriesList.add(new GS("Screenshots"));

        for (int i = 0; i < 2; i++) {
            Uri externalContentUri = (i == 0 ? Images.Media.EXTERNAL_CONTENT_URI : Video.Media.EXTERNAL_CONTENT_URI);

            Cursor cursor = getContentResolver().query(
                    externalContentUri,
                    new String[]{"date_added", "_data", "_id"},
                    null,
                    null,
                    "date_added ASC");  // ascending order

            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                    long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    Uri uri = Uri.parse(externalContentUri + "/" + id);
                    String absPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    Bitmap bitmap;

                    if (i == 0)
                        bitmap = Images.Thumbnails.getThumbnail(getContentResolver(), id, Images.Thumbnails.FULL_SCREEN_KIND, null);
                    else
                        bitmap = Video.Thumbnails.getThumbnail(getContentResolver(), id, Video.Thumbnails.FULL_SCREEN_KIND, null);

                    photosList.add(new PS(date, uri, bitmap, absPath));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        photosList.sort((o1, o2) -> o2.date.compareTo(o1.date));


        ArrayList<String> galleryNameList = new ArrayList<>();
        galleryNameList.add("Camera");
        galleryNameList.add("Screenshots");


        for (PS ps : photosList) {
            String[] array = ps.absPath.split("/");
            String galleryName = array[array.length - 2];
            int index = galleryNameList.indexOf(galleryName);

            // create new gallery
            if (index == -1) {
                galleriesList.add(new GS(galleryName));
                galleryNameList.add(galleryName);
                index = galleryNameList.size() - 1;
            }
            getGS(index).psList.add(ps);
        }
    }

    // call it before switch fragment
    public void setCurList(int i) {
        // -1 : All
        if (i == -1)
            curList = photosList;
        else
            curList = getGS(i).psList;
    }

    public GS getGS(int i) {
        return galleriesList.get(i);
    }

    public void switchDisplayedFragment(int newFragment, int argSetCurList, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (newFragment) {
            case INIT:
                ft.add(R.id.container, cellFragment)
                        .add(R.id.container, bedFragment)
                        .add(R.id.container, galleryFragment);

                ft.show(cellFragment)
                        .hide(galleryFragment).hide(bedFragment);

                ft.commit();
                return;

            case CELL_FRAGMENT:
                setCurList(argSetCurList);
                cellFragment.setAdapter();
                ft.show(cellFragment);
                break;

            case GALLERY_FRAGMENT:
                ft.show(galleryFragment);
                break;

            case BED_FRAGMENT:
                hideBar();
                ft.show(bedFragment);
                break;
        }

        if (addToBackStack)
            ft.addToBackStack(null);

        if (cellFragment.isVisible())
            ft.hide(cellFragment);
        else if (bedFragment.isVisible())
            ft.hide(bedFragment);
        else if (galleryFragment.isVisible())
            ft.hide(galleryFragment);

        ft.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showBar();
    }

    /* ==================================================================================================== */
    public ArrayList<PS> getCurList() {
        return curList;
    }

    public PS getCurPS(int i) {
        return curList.get(i);
    }

    public PS getCurPS() {
        return curList.get(iList);
    }

    public ArrayList<GS> getGalleriesList() {
        return galleriesList;
    }

    public int getIList() {
        return iList;
    }

    public void changeIListByOne(boolean isAdd) {
        if (isAdd)
            iList++;
        else
            iList--;
    }


    /* ==================================================================================================== */
    private boolean checkOrGrantBasicPermissions() {
        String[] ss;

        // A13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // TIRAMISU = A13
            if (checkSelfPermission(READ_MEDIA_IMAGES) == PERMISSION_GRANTED && checkSelfPermission(READ_MEDIA_VIDEO) == PERMISSION_GRANTED)
                return true;  // granted

            ss = new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
        }
        // A9-A12
        else {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
                return true;  // granted

            ss = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        }

        requestPermissions(ss, BASIC_PERMISSIONS);
        return false;
        // then goto onRequestPermissionsResult
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Lazybones.tt(this, shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE));

        if (requestCode == BASIC_PERMISSIONS) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                setPhotoAndGalleryStructList();
                setCurList(-1);
                switchDisplayedFragment(INIT, -1, false);
            } else {
                dd(this, "Insufficient Access Rights", "P2G Gallery requires the permissions you denied.", (dialog, which) -> {

                });
            }
        }
    }
}