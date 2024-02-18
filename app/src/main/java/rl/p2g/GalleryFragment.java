package rl.p2g;

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

public class GalleryFragment extends Fragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gv = view.findViewById(R.id.gv);
        gv.setAdapter(new ArrayAdapter<GS>(cc(), R.layout.gallery_item, cc().getGalleriesList()) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.gallery_item, parent, false);

                ((TextView) convertView.findViewById(R.id.tv))
                        .setText(cc().getGS(i).galleryName);

                ((ImageView) convertView.findViewById(R.id.iv))
                        .setImageBitmap(cc().getGS(i).psList.get(0).bitmap);

                return convertView;
            }
        });
        gv.setOnItemClickListener((parent, view1, i, id) -> {
            cc().switchDisplayedFragment(cc().CELL_FRAGMENT, i, true);
        });
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}