package rl.p2a;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private String[] mDataset;

    // 提供一個適當的構造函數（依賴於數據集的類型）
    public MyAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // 創建新視圖（由佈局管理器調用）
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 創建一個新視圖
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // 將元素綁定到視圖（由佈局管理器調用）
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - 獲取元素數據集中的位置
        // - 用該元素替換視圖的內容
        holder.textView.setText(mDataset[position]);
    }

    // 返回數據集的大小（由佈局管理器調用）
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    // 提供對視圖的引用
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // 每個數據項是僅一個字符串，所以我們只需要一個TextView
        public TextView textView;

        public MyViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view);
        }
    }
}

