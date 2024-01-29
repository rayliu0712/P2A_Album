package rl.p2g;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final int BASIC_PERMISSIONS = 0;
    private final Lazy ii = new Lazy(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantBasicPermissions();

        fragment(new HomeFragment(this));
    }

    private void grantBasicPermissions() {
        String[] ss;

        // A13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {  // TIRAMISU = A13
            if (checkSelfPermission(READ_MEDIA_IMAGES) == PERMISSION_GRANTED && checkSelfPermission(READ_MEDIA_VIDEO) == PERMISSION_GRANTED)
                return;

            ss = new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
        }
        // A9-A12
        else {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
                return;

            ss = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
        }

        requestPermissions(ss, BASIC_PERMISSIONS);
        // then goto onRequestPermissionsResult
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BASIC_PERMISSIONS) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                // granted
                fragment(new HomeFragment(this));
            } else {
                // denied
                ii.dd("Insufficient Access Rights", "P2G Gallery requires the permissions you denied.", (dialog, which) -> finish());
            }
        }
    }

    public void fragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment);

        List<Fragment> list = getSupportFragmentManager().getFragments();
        if (!list.isEmpty())
            ft.hide(list.get(list.size() - 1));

        ft.commit();
    }
}