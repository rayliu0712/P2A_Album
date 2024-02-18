package rl.p2g;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CellFragment extends Fragment {

    private GridView gv;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gv = view.findViewById(R.id.gv);
        gv.setOnItemClickListener((parent, view1, i, id) -> {
            cc().switchDisplayedFragment(cc().BED_FRAGMENT, i, true);
        });
        setAdapter();
    }

    public void setAdapter() {
        gv.setAdapter(new ArrayAdapter<PS>(cc(), R.layout.cell_item, cc().getCurList()) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.cell_item, parent, false);

                ImageView convertIV = convertView.findViewById(R.id.iv);
                convertIV.setImageBitmap(cc().getCurPS(i).bitmap);
                return convertView;
            }
        });
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cell, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}