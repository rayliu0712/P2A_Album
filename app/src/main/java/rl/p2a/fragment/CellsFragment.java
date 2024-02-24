package rl.p2a.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.struct.MediaStruct;

public class CellsFragment extends Fragment {
    private static Parcelable scrollState = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cells, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;

        GridView gv = view.findViewById(R.id.gv);
        gv.setOnItemClickListener((parent, view1, i, id) -> {
            MainActivity.onBackState++;

            // https://stackoverflow.com/questions/29581782/how-to-get-the-scrollposition-in-the-recyclerview-layoutmanager
            scrollState = gv.onSaveInstanceState();

            ma.updateFragmentPagerAdapter(new int[]{i}, new Fragment[]{new BedFragment()}, 0);
        });


        gv.setAdapter(new ArrayAdapter<MediaStruct>(ma, R.layout.cells_item, Database.getAlbum().mediaList) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.cells_item, parent, false);

                Drawable drawable = Database.getAlbum().getMedia(i).thumbnailDrawable;
                Glide.with(ma)
                        .load(drawable).placeholder(drawable)
                        .into((ImageView) convertView.findViewById(R.id.iv));

                return convertView;
            }
        });

        if (scrollState != null)
            gv.onRestoreInstanceState(scrollState);
    }
}