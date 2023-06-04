package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemColorDrawableAdapter extends RecyclerView.Adapter<ItemColorDrawableVH> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private Context context;
    private List<Integer> data;

    private OnItemClickListener listener;

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ItemColorDrawableAdapter(Context context, List<Integer> data) {
        this.context = context;
        this.data = data;
    }
    @NotNull
    @Override
    public ItemColorDrawableVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color_drawable_adapter_item, parent, false);
        return new ItemColorDrawableVH(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ItemColorDrawableVH holder, int position) {
        holder.itemColorDrawableView.setBackgroundResource(data.get(position));

        holder.itemColorDrawableView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
