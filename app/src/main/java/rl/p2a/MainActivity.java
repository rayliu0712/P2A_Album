package rl.p2a;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;

import rl.p2a.fragment.AlbumsFragment;
import rl.p2a.fragment.CellsFragment;
import rl.p2a.struct.AlbumStruct;
import rl.p2a.struct.MediaStruct;

public class MainActivity extends AppCompatActivity {
    /*
     * 0 = finishAffinity()
     * 1 = bed -> cells
     * 2 = album's cells -> albums
     * 3 = album's bed -> album's cells
     */
    public int backState = 0;
    public Handler handler = new Handler();

    private final char BASIC_PERMISSIONS = 0;
    private final ArrayList<Fragment> currentFragments = new ArrayList<>();
    private ViewPager fragmentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Tools.setContext(this);
        fragmentPager = findViewById(R.id.vp);
        findViewById(R.id.bg).setBackgroundColor(getWindow().getStatusBarColor());


        // high refresh rate
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Display.Mode[] modes = getWindowManager().getDefaultDisplay().getSupportedModes();
        Arrays.sort(modes, (o1, o2) -> (int) (o2.getRefreshRate() - o1.getRefreshRate()));
        layoutParams.preferredDisplayModeId = modes[0].getModeId();
        getWindow().setAttributes(layoutParams);


        switch (Build.VERSION.SDK_INT) {
            case Build.VERSION_CODES.UPSIDE_DOWN_CAKE:
                // do sth...
                break;

            case Build.VERSION_CODES.TIRAMISU:
                if ((checkSelfPermission(READ_MEDIA_IMAGES) | checkSelfPermission(READ_MEDIA_VIDEO)) == 0)
                    init();
                else
                    requestPermissions(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO}, BASIC_PERMISSIONS);
                break;

            case Build.VERSION_CODES.S_V2:
            case Build.VERSION_CODES.S:
            case Build.VERSION_CODES.R:
                if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    init();
                else
                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, BASIC_PERMISSIONS);
                break;
        }
    }

    private void init() {
        currentFragments.addAll(Arrays.asList(new CellsFragment(), new AlbumsFragment()));

        fragmentPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return currentFragments.get(position);
            }

            @Override
            public int getCount() {
                return currentFragments.size();
            }

            // https://stackoverflow.com/a/36348078, required when updating this adapter
            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }
        });


        ArrayList<String> albumNameList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Uri externalContentUri =
                    i == 0 ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            Cursor cursor = getContentResolver().query(
                    externalContentUri,
                    new String[]{"date_added", "_data", "_id"},
                    null, null, "date_added DESC");

            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));
                    String absPath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    Uri uri = Uri.parse(externalContentUri + "/" + id);
                    Database.allMediaList.add(new MediaStruct(i == 0, id, uri, absPath, date));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        Database.allMediaList.sort((o1, o2) -> o2.date.compareTo(o1.date));

        Tools.toast(Database.allMediaList.size());

        new Thread(() -> {
            for (int i = 0; i < Database.allMediaList.size(); i++) {
                MediaStruct ms = Database.allMediaList.get(i);

                ms.thumbnailDrawable = new BitmapDrawable(getResources(), ms.isVideo ?
                        MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), ms.id, MediaStore.Video.Thumbnails.MINI_KIND, null) :
                        MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), ms.id, MediaStore.Images.Thumbnails.MINI_KIND, null)
                );

                final int fI = i;
                handler.post(() -> {
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
                        Database.albumList.add(0, new AlbumStruct(albumName));
                    else if (albumName.equals("Screenshots"))
                        Database.albumList.add(albumNameList.size() > 0 ? 1 : 0, new AlbumStruct(albumName));
                    else
                        Database.albumList.add(new AlbumStruct(albumName));

                    albumNameList.add(albumName);
                    index = albumNameList.size() - 1;

                    final int fIndex = index;
                    handler.post(() -> {
                        try {
                            // 在全部載入完之前點進BedFragment會crash
                            AlbumsFragment.rv.getAdapter().notifyItemChanged(fIndex);
                        } catch (Exception ignored) {
                        }
                    });
                }
                Database.getAlbum(index).addMedia(ms);
            }

            handler.post(() -> {
                fragmentPager.getAdapter().notifyDataSetChanged();
                Tools.toast("finished");
            });
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BASIC_PERMISSIONS) {
            if (grantResults[0] == 0)
                init();
            else
                Tools.toast("are u even serious?");
        }
    }

    public void updateFragmentPagerAdapter(Fragment[] nextFragments, int itemPosition) {
        for (Fragment f : currentFragments)
            getSupportFragmentManager().beginTransaction().remove(f).commitNow();
        currentFragments.clear();
        currentFragments.addAll(Arrays.asList(nextFragments));

        fragmentPager.getAdapter().notifyDataSetChanged();
        fragmentPager.setCurrentItem(itemPosition, false);
    }

    @Override
    public void onBackPressed() {
        switch (backState) {
            case 0:
                finishAffinity();
                break;
            case 1:
                updateFragmentPagerAdapter(new Fragment[]{new CellsFragment(), new AlbumsFragment()}, 0);
                backState = 0;
                break;
            case 2:
                Database.iAlbum = -1;
                updateFragmentPagerAdapter(new Fragment[]{new CellsFragment(), new AlbumsFragment()}, 1);
                backState = 0;
                break;
            case 3:
                updateFragmentPagerAdapter(new Fragment[]{new CellsFragment()}, 0);
                backState--;
                break;
            default:
                super.onBackPressed();
                break;
        }
    }
}