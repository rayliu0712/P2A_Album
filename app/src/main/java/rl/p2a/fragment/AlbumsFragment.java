package rl.p2a.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import rl.p2a.Database;
import rl.p2a.R;
import rl.p2a.struct.AlbumStruct;
import rl.p2a.MainActivity;

public class AlbumsFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gv = view.findViewById(R.id.medium_av);
        gv.setAdapter(new ArrayAdapter<AlbumStruct>(cc(), R.layout.albums_item, Database.albumList) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.albums_item, parent, false);

                ((TextView) convertView.findViewById(R.id.tv))
                        .setText(Database.getAlbum(i).galleryName);

                ((ImageView) convertView.findViewById(R.id.small_iv))
                        .setImageBitmap(Database.getAlbum(i).getMedia(0).thumbnailBitmap);

                return convertView;
            }
        });
        gv.setOnItemClickListener((parent, view1, i, id) -> {
            cc().switchDisplayedFragment(MainActivity.CELL_FRAGMENT, i, true);
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}