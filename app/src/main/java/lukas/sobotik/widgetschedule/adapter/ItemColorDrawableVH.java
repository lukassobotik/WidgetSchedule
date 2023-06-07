package lukas.sobotik.widgetschedule.adapter;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import lukas.sobotik.widgetschedule.R;
import org.jetbrains.annotations.NotNull;

public class ItemColorDrawableVH extends RecyclerView.ViewHolder {
    View itemColorDrawableView;
    public ItemColorDrawableVH(@NonNull @NotNull View itemView) {
        super(itemView);
        itemColorDrawableView = itemView.findViewById(R.id.item_color_drawable_view);
    }
}
