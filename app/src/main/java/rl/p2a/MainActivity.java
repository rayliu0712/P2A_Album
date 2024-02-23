package rl.p2a;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.Arrays;

import rl.p2a.fragment.AlbumsFragment;
import rl.p2a.fragment.BedFragment;
import rl.p2a.fragment.CellsFragment;

public class MainActivity extends AppCompatActivity {
    public static final char CELLS_FRAGMENT = 0;
    public static final char BED_FRAGMENT = 1;
    public static final char ALBUMS_FRAGMENT = 2;
    public static final char[] basicFragments = new char[]{CELLS_FRAGMENT, ALBUMS_FRAGMENT};
    public Handler handler = new Handler();


    // maybe can remove it, use list<Fragment> instead?
    private CellsFragment cellsFragment = new CellsFragment();
    private BedFragment bedFragment = new BedFragment();
    private AlbumsFragment albumsFragment = new AlbumsFragment();
    private final char BASIC_PERMISSIONS = 0;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.vp);

        // 設定高更新率
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Display.Mode[] modes = getWindowManager().getDefaultDisplay().getSupportedModes();
        Arrays.sort(modes, (o1, o2) -> (int) (o2.getRefreshRate() - o1.getRefreshRate()));
        layoutParams.preferredDisplayModeId = modes[0].getModeId();
        getWindow().setAttributes(layoutParams);

        if (checkOrGrantBasicPermissions())
            onRequestPermissionsResult(BASIC_PERMISSIONS, null, new int[]{PERMISSION_GRANTED});
    }

    private boolean checkOrGrantBasicPermissions() {
        String[] ss;

        // A13
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(READ_MEDIA_IMAGES) == PERMISSION_GRANTED &&
                    checkSelfPermission(READ_MEDIA_VIDEO) == PERMISSION_GRANTED)
                return true;

            ss = new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
        }

        // A9-A12
        else {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED &&
                    checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
                return true;

            ss = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        }

        requestPermissions(ss, BASIC_PERMISSIONS);
        return false;
        // goto onRequestPermissionsResult
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BASIC_PERMISSIONS) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                Database.getThumbnailsAndSetLists(this);
            } else {
                EzTools.dialog(this, "Insufficient Access Rights", "P2A Album requires the permissions you denied.", null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bedFragment.isVisible()) {
            setFragmentPagerAdapter(basicFragments, null);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Database.allMediaList = null;
        Database.albumList = null;
        handler = null;

        super.onDestroy();
    }

    public void setFragmentPagerAdapter(char[] fragmentIDs, int[] nextMediaOrAlbumIndex) {
        getSupportFragmentManager().beginTransaction()
                .remove(cellsFragment)
                .remove(albumsFragment)
                .remove(bedFragment)
                .commitNow();


        // re-instance fragments
        cellsFragment = new CellsFragment();
        bedFragment = new BedFragment();
        albumsFragment = new AlbumsFragment();

        Fragment[] fragments = new Fragment[fragmentIDs.length];
        for (int i = 0; i < fragmentIDs.length; i++) {
            switch (fragmentIDs[i]) {
                case CELLS_FRAGMENT:
                    if (fragmentIDs.length == 1)
                        Database.iAlbum = nextMediaOrAlbumIndex[0];
                    fragments[i] = cellsFragment;
                    break;

                case BED_FRAGMENT:
                    if (fragmentIDs.length == 1)
                        Database.iMedia = nextMediaOrAlbumIndex[0];
                    fragments[i] = bedFragment;
                    break;

                case ALBUMS_FRAGMENT:
                    fragments[i] = albumsFragment;
                    break;
            }
        }

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        viewPager.setCurrentItem(0, false);
    }
}