package rl.p2a;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import rl.p2a.fragment.AlbumFragment;
import rl.p2a.fragment.BedFragment;
import rl.p2a.fragment.CellFragment;

public class MainActivity extends AppCompatActivity {

    public static final int INIT = -1;
    public static final int CELL_FRAGMENT = 0;
    public static final int AlBUM_FRAGMENT = 1;
    public static final int BED_FRAGMENT = 2;
    private final int BASIC_PERMISSIONS = 0;

    private final CellFragment cellFragment = new CellFragment();
    private final BedFragment bedFragment = new BedFragment();
    private final AlbumFragment albumFragment = new AlbumFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.album).setOnClickListener(v -> {
            switchDisplayedFragment(AlBUM_FRAGMENT, 0, false);
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
                Handler handler = new Handler();
                Database.getThumbnailsAndSetLists(this, handler);
            } else {
                EzTools.dialog(this, "Insufficient Access Rights", "P2G Gallery requires the permissions you denied.", null);
            }
        }
    }


    public void switchDisplayedFragment(int nextFragment, int nextMediaOrAlbumIndex, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (nextFragment) {
            case INIT:
                ft.add(R.id.container, cellFragment)
                        .add(R.id.container, bedFragment)
                        .add(R.id.container, albumFragment);

                ft.show(cellFragment)
                        .hide(albumFragment).hide(bedFragment);

                ft.commit();
                return;

            case CELL_FRAGMENT:
                Database.iAlbum = nextMediaOrAlbumIndex;
                cellFragment.setAdapter();
                ft.show(cellFragment);
                break;

            case AlBUM_FRAGMENT:
                ft.show(albumFragment);
                break;

            case BED_FRAGMENT:
                Database.iMedia = nextMediaOrAlbumIndex;
                bedFragment.glide();
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
        else if (albumFragment.isVisible())
            ft.hide(albumFragment);

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
        findViewById(R.id.bar).setVisibility(View.GONE);
    }

    public void showBar() {
        findViewById(R.id.bar).setVisibility(View.VISIBLE);
    }
}