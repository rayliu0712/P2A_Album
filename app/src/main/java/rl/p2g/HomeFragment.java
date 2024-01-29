package rl.p2g;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private final MainActivity context;
    private final Lazy ii;

    private final ArrayList<Uri> uris = new ArrayList<>();
    private final ArrayList<Bitmap> thumbnails = new ArrayList<>();

    private GridView gv;

    public HomeFragment(MainActivity context) {
        this.context = context;
        ii = new Lazy(context);
    }

    @Nullable
    @Override  // not important
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gv = view.findViewById(R.id.gv);

        getThumbnails();

        ArrayAdapter<Uri> adapter = new ArrayAdapter<Uri>(context, R.layout.home_item, uris) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.home_item, parent, false);

                ImageView convertIV = convertView.findViewById(R.id.iv);
                convertIV.setImageBitmap(thumbnails.get(position));
                return convertView;
            }
        };
        gv.setAdapter(adapter);
        gv.setOnItemClickListener((parent, view1, position, id) -> {
            BedFragment.uris = uris;
            BedFragment.iUris = position;
            context.fragment(new BedFragment(context));
        });
    }

    private void getThumbnails() {
        ArrayList<Pair<String, String>> list = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Cursor cursor = context.getContentResolver().query(
                    i == 0 ? Images.Media.EXTERNAL_CONTENT_URI : Video.Media.EXTERNAL_CONTENT_URI,
                    new String[]{"_id", "date_added"},
                    null,
                    null,
                    "date_added ASC");  // ascending order

            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));

                    long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("_id")));
                    String uri = (i == 0 ? Images.Media.EXTERNAL_CONTENT_URI : Video.Media.EXTERNAL_CONTENT_URI) + "/" + id;

                    list.add(new Pair<>(date, uri));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        list.sort((o1, o2) -> o2.first.compareTo(o1.first));
        for (int i = 0; i < list.size(); i++) {
            uris.add(Uri.parse(list.get(i).second));
            thumbnails.add(Images.Thumbnails.getThumbnail(context.getContentResolver(), getID(uris.get(i)), Images.Thumbnails.FULL_SCREEN_KIND, null));
        }
    }

    private long getID(Uri uri) {
        return Long.parseLong(uri.toString().replace(Images.Media.EXTERNAL_CONTENT_URI + "/", "").replace(Video.Media.EXTERNAL_CONTENT_URI + "/", ""));
    }
}