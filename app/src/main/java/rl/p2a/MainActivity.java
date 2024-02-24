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

import androidx.activity.OnBackPressedCallback;
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
    public static Handler handler = new Handler();
    public static int onBackState = 0;
    // 0 = finish()
    // 1 = bed -> cells
    // 2 = album's cells -> albums
    // 3 = album's bed -> album's cells

    private final char BASIC_PERMISSIONS = 0;
    private Fragment[] currentFragments = new Fragment[]{};
    private ViewPager fragmentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                switch (onBackState) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        updateFragmentPagerAdapter(null, new Fragment[]{new CellsFragment(), new AlbumsFragment()}, 0);
                        onBackState = 0;
                        break;
                    case 2:
                        updateFragmentPagerAdapter(new int[]{-1}, new Fragment[]{new CellsFragment(), new AlbumsFragment()}, 1);
                        onBackState = 0;
                        break;
                    case 3:
                        updateFragmentPagerAdapter(null, new Fragment[]{new CellsFragment()}, 0);
                        onBackState--;
                        break;
                }
                EzTools.log(onBackState);
            }
        });

        fragmentPager = findViewById(R.id.vp);
        fragmentPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return currentFragments[position];
            }

            // https://stackoverflow.com/questions/30080045/fragmentpageradapter-notifydatasetchanged-not-working
            // required when updating this adapter
            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return currentFragments.length;
            }
        });

        // 設定高更新率
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Display.Mode[] modes = getWindowManager().getDefaultDisplay().getSupportedModes();
        Arrays.sort(modes, (o1, o2) -> (int) (o2.getRefreshRate() - o1.getRefreshRate()));
        layoutParams.preferredDisplayModeId = modes[0].getModeId();
        getWindow().setAttributes(layoutParams);

        if (checkOrGrantBasicPermissions())
            onRequestPermissionsResult(BASIC_PERMISSIONS, new String[]{}, new int[]{PERMISSION_GRANTED});
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

    public void updateFragmentPagerAdapter(int[] nextMediaOrAlbumIndex, Fragment[] nextFragments, int itemPosition) {  // add state arg
        if (nextMediaOrAlbumIndex != null) {
            for (Fragment fragment : nextFragments) {
                if (fragment instanceof CellsFragment) {
                    Database.iAlbum = nextMediaOrAlbumIndex[0];
                } else if (fragment instanceof BedFragment) {
                    Database.iMedia = nextMediaOrAlbumIndex[0];
                }
            }
        }

        for (Fragment fragment : currentFragments) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
        }
        currentFragments = nextFragments;  // currentFragment is reference of nextFragment

        assert fragmentPager.getAdapter() != null;
        fragmentPager.getAdapter().notifyDataSetChanged();

        fragmentPager.setCurrentItem(itemPosition, false);
    }

    @Override
    protected void onDestroy() {
        Database.allMediaList = null;
        Database.albumList = null;
        handler = null;
        currentFragments = null;
        fragmentPager = null;

        super.onDestroy();
    }
}