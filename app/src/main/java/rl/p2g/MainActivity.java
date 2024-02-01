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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final int BASIC_PERMISSIONS = 0;

    public int iList;
    public ArrayList<PS> currentList;
    public final ArrayList<PS> apList = new ArrayList<>();  // all photos
    public final ArrayList<GS> gsList = new ArrayList<>();  // sort by gallery

    public final CellFragment cellFragment = new CellFragment();
    public final BedFragment bedFragment = new BedFragment();
    public final GalleryFragment galleryFragment = new GalleryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView cellIV = findViewById(R.id.cell);
        cellIV.setOnClickListener(v -> {
            cellFragment.refresh(-1);
            switchDisplayedFragment(cellFragment, false);
        });

        ImageView galleryIV = findViewById(R.id.gallery);
        galleryIV.setOnClickListener(v -> {
            switchDisplayedFragment(galleryFragment, true);
        });

        if (checkOrGrantBasicPermissions())
            setListsAndSwitchFragment();
    }

    public void setListsAndSwitchFragment() {
        setPhotoAndGalleryStructList();
        setCurrentList(-1);
        switchDisplayedFragment(null, false);
    }

    public void setPhotoAndGalleryStructList() {
        gsList.add(new GS("Camera"));
        gsList.add(new GS("Screenshots"));

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

                    apList.add(new PS(date, uri, bitmap, absPath));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        apList.sort((o1, o2) -> o2.date.compareTo(o1.date));


        ArrayList<String> galleryNameList = new ArrayList<>();
        galleryNameList.add("Camera");
        galleryNameList.add("Screenshots");


        for (PS ps : apList) {
            String[] array = ps.absPath.split("/");
            String galleryName = array[array.length - 2];
            int index = galleryNameList.indexOf(galleryName);

            // create new gallery
            if (index == -1) {
                gsList.add(new GS(galleryName));
                galleryNameList.add(galleryName);
                index = galleryNameList.size() - 1;
            }
            getGS(index).psList.add(ps);
        }
    }

    public GS getGS(int i) {
        return gsList.get(i);
    }

    // call it before switch fragment
    public void setCurrentList(int i) {
        // -1 : All
        if (i == -1)
            currentList = apList;
        else
            currentList = getGS(i).psList;
    }

    public void switchDisplayedFragment(Fragment newFragment, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (newFragment == null) {
            ft.add(R.id.container, cellFragment)
                    .add(R.id.container, bedFragment)
                    .add(R.id.container, galleryFragment);

            ft.show(cellFragment)
                    .hide(galleryFragment)
                    .hide(bedFragment);
        } else {
            if (addToBackStack)
                ft.addToBackStack(null);

            if (cellFragment.isVisible())
                ft.hide(cellFragment);
            else if (bedFragment.isVisible())
                ft.hide(bedFragment);
            else if (galleryFragment.isVisible())
                ft.hide(galleryFragment);

            ft.show(newFragment);
        }
        ft.commit();
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

        if (requestCode == BASIC_PERMISSIONS) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                // granted
                setListsAndSwitchFragment();
            } else {
                // denied
                dd(this, "Insufficient Access Rights", "P2G Gallery requires the permissions you denied.", (dialog, which) -> finish());
            }
        }
    }
}