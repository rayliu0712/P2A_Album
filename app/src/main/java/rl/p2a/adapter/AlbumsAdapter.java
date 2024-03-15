package rl.p2a.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.fragment.AlbumsFragment;
import rl.p2a.fragment.CellsFragment;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumsHolder> {
    private final MainActivity ma;

    static class AlbumsHolder extends RecyclerView.ViewHolder {
        private final TextView tv;
        private final ImageView iv;

        public AlbumsHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            iv = itemView.findViewById(R.id.iv);
        }
    }

    public AlbumsAdapter(MainActivity ma) {
        this.ma = ma;
    }

    @NonNull
    @Override
    public AlbumsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AlbumsHolder(
                ma.getLayoutInflater().inflate(R.layout.albums_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsHolder holder, int i) {
        holder.itemView.setOnClickListener(v -> {
            ma.backState = 2;

            // https://stackoverflow.com/a/48612294
            AlbumsFragment.scrollState = AlbumsFragment.rv.getLayoutManager().onSaveInstanceState();

            Database.iAlbum = holder.getAdapterPosition();
            ma.updateFragmentPagerAdapter(new Fragment[]{new CellsFragment()}, 0);
        });

        holder.tv.setText(Database.getAlbum(i).galleryName);

        Drawable drawable = Database.getAlbum(i).getMedia(0).thumbnailDrawable;
        Glide.with(ma).load(drawable).placeholder(drawable).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return Database.albumList.size();
    }
}
