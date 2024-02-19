package rl.p2a.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.struct.MediaStruct;

public class CellsFragment extends Fragment {

    private ListView bigLV;
    private GridView mediumAV;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        bigLV = view.findViewById(R.id.big_lv);
//        mediumAV = view.findViewById(R.id.medium_av);
//        mediumAV.setOnItemClickListener((parent, view1, i, id) -> {
//            cc().switchDisplayedFragment(MainActivity.BED_FRAGMENT, i, true);
//        });
//        setAdapter();
    }

    public void setAdapter() {
        bigLV.setAdapter(new ArrayAdapter<MediaStruct>(cc(), R.layout.medium_item, new MediaStruct[]{}) {

        });

        mediumAV.setAdapter(new ArrayAdapter<MediaStruct>(cc(), R.layout.small_item, Database.getAlbum().mediaList) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.small_item, parent, false);

                ImageView convertIV = convertView.findViewById(R.id.small_iv);
                Glide.with(cc()).load(Database.getAlbum().getMedia(i).thumbnailBitmap).placeholder(null).into(convertIV);
                return convertView;
            }
        });
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cells, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}