package vista;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;

import java.util.List;

public class RecipeGridAdapter extends RecyclerView.Adapter<RecipeGridAdapter.GridViewHolder> {

    private Context context;
    private List<Integer> imageList;

    public RecipeGridAdapter(Context context, List<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        // Hacemos que cada imagen sea un cuadrado perfecto
        int size = parent.getWidth() / 3;
        imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(2, 2, 2, 2);
        return new GridViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        int imageRes = imageList.get(position);
        holder.imageView.setImageResource(imageRes);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, controlador.PostDetailActivity.class);

            intent.putExtra("username", "Chef Gourmet");
            intent.putExtra("profileImage", R.drawable.user);
            intent.putExtra("postImage", imageRes);
            intent.putExtra("likes", 120 + position);

            intent.putExtra("ingredientsText", "• Ingrediente 1\n• Ingrediente 2\n• Ingrediente 3");
            intent.putExtra("stepsText", "1. Primer paso de mi receta.\n\n2. Segundo paso de mi receta.");

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        GridViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }
}