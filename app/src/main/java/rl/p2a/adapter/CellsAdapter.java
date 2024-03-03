package rl.p2a.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.fragment.BedFragment;
import rl.p2a.fragment.CellsFragment;

public class CellsAdapter extends RecyclerView.Adapter<CellsAdapter.CellsHolder> {
    public MainActivity ma;

    public static class CellsHolder extends RecyclerView.ViewHolder {
        public final ImageView iv;

        public CellsHolder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv);
        }
    }

    public CellsAdapter(MainActivity ma) {
        this.ma = ma;
    }

    @NonNull
    @Override
    public CellsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CellsHolder(
                ma.getLayoutInflater().inflate(R.layout.cells_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CellsHolder holder, int i) {
        holder.itemView.setOnClickListener(v -> {
            ma.backState++;

            // https://stackoverflow.com/a/48612294
            CellsFragment.scrollState = CellsFragment.rv.getLayoutManager().onSaveInstanceState();

            ma.updateFragmentPagerAdapter(new int[]{i}, new Fragment[]{new BedFragment()}, 0);
        });
        Drawable drawable = Database.getAlbum().getMedia(i).thumbnailDrawable;
        if (drawable == null)
            drawable = ma.getResources().getDrawable(R.drawable.t1);
        Glide.with(ma).load(drawable).placeholder(drawable).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return Database.getAlbum().mediaList.size();
    }
}
