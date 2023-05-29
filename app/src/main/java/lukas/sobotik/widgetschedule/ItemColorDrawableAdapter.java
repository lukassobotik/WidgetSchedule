package lukas.sobotik.widgetschedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemColorDrawableAdapter extends RecyclerView.Adapter<ItemColorDrawableVH> {
    private Context context;
    private List<Integer> data;

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
