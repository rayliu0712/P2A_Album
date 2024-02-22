package rl.p2a;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;

import rl.p2a.fragment.AlbumsFragment;
import rl.p2a.fragment.BedFragment;
import rl.p2a.fragment.CellsFragment;
import rl.p2a.R;

public class MainActivity extends AppCompatActivity {
    public static final int INIT = -1;
    public static final int CELLS_FRAGMENT = 0;
    public static final int AlBUMS_FRAGMENT = 1;
    public static final int BED_FRAGMENT = 2;
    private final int BASIC_PERMISSIONS = 0;

    private final CellsFragment cellsFragment = new CellsFragment();
    private final BedFragment bedFragment = new BedFragment();
    private final AlbumsFragment albumsFragment = new AlbumsFragment();

    public View bar;
    public ProgressBar pb;
    public final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 設定高更新率
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        Display.Mode[] modes = getWindowManager().getDefaultDisplay().getSupportedModes();
        Arrays.sort(modes, (o1, o2) -> (int) (o2.getRefreshRate() - o1.getRefreshRate()));
        layoutParams.preferredDisplayModeId = modes[0].getModeId();
        getWindow().setAttributes(layoutParams);


        ImageView photosIV = findViewById(R.id.photos);
        ImageView albumsIV = findViewById(R.id.albums);
        ImageView moreIV = findViewById(R.id.more);
        TextView moreTV = findViewById(R.id.more_t);
        bar = findViewById(R.id.bar);
        pb = findViewById(R.id.pb);

        findViewById(R.id.bar1).setOnClickListener(v -> {
            photosIV.setImageDrawable(getDrawableFromResource(R.drawable.photo_outlined));
            albumsIV.setImageDrawable(getDrawableFromResource(R.drawable.album_filled));
            moreIV.setImageDrawable(getDrawableFromResource(R.drawable.more_white));
            moreTV.setTextColor(getColor(R.color.white));
            switchDisplayedFragment(CELLS_FRAGMENT, -1, false);
        });

        findViewById(R.id.bar2).setOnClickListener(v -> {
            photosIV.setImageDrawable(getDrawableFromResource(R.drawable.photo_filled));
            albumsIV.setImageDrawable(getDrawableFromResource(R.drawable.album_outlined));
            moreIV.setImageDrawable(getDrawableFromResource(R.drawable.more_white));
            moreTV.setTextColor(getColor(R.color.white));
            switchDisplayedFragment(AlBUMS_FRAGMENT, -1, false);
        });

        findViewById(R.id.bar3).setOnClickListener(v -> {
            photosIV.setImageDrawable(getDrawableFromResource(R.drawable.photo_filled));
            albumsIV.setImageDrawable(getDrawableFromResource(R.drawable.album_filled));
            moreIV.setImageDrawable(getDrawableFromResource(R.drawable.more_blue));
            moreTV.setTextColor(getColor(R.color.blue));
        });

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
                Database.getThumbnailsAndSetLists(this, handler);
            } else {
                EzTools.dialog(this, "Insufficient Access Rights", "P2A Album requires the permissions you denied.", null);
            }
        }
    }


    public void switchDisplayedFragment(int nextFragment, int nextMediaOrAlbumIndex, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (nextFragment) {
            case INIT:
                ft.add(R.id.container, cellsFragment)
                        .add(R.id.container, bedFragment)
                        .add(R.id.container, albumsFragment);

                ft.show(cellsFragment)
                        .hide(albumsFragment).hide(bedFragment);

                ft.commit();
                return;

            case CELLS_FRAGMENT:
                Database.iAlbum = nextMediaOrAlbumIndex;
                cellsFragment.update();
                ft.show(cellsFragment);
                break;

            case AlBUMS_FRAGMENT:
                ft.show(albumsFragment);
                break;

            case BED_FRAGMENT:
                Database.iMedia = nextMediaOrAlbumIndex;
                bedFragment.update();
                ft.show(bedFragment);
                break;
        }

        if (addToBackStack)
            ft.addToBackStack(null);

        if (cellsFragment.isVisible() && nextFragment != CELLS_FRAGMENT)
            ft.hide(cellsFragment);
        else if (bedFragment.isVisible() && nextFragment != BED_FRAGMENT)
            ft.hide(bedFragment);
        else if (albumsFragment.isVisible() && nextFragment != AlBUMS_FRAGMENT)
            ft.hide(albumsFragment);

        ft.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        showBar();
    }


    @Override
    protected void onDestroy() {
        Database.allMediaList = null;
        Database.albumList = null;

        super.onDestroy();
    }


    public void hideBar() {
        bar.setVisibility(View.GONE);
    }

    public void showBar() {
        bar.setVisibility(View.VISIBLE);
    }

    public Drawable getDrawableFromResource(int id) {
        return getResources().getDrawable(id);
    }
}