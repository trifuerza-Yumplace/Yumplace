package vista;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;

import java.util.List;

import controlador.PostDetailActivity;
import modelo.Post;
import modelo.RecipeIngredientResponse;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ViewHolder> {

    Context context;
    List<Post> list;

    private final String DEFAULT_IMAGE =
            "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcci%C3%B3n.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";

    public ProfileGridAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_grid_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = list.get(position);

        String imageUrl = post.getPostImage();

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = DEFAULT_IMAGE;
        }

        final String finalImageUrl = imageUrl;

        Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.pasta)
                .error(R.drawable.pasta)
                .into(holder.image);

        // ================= CLICK A DETALLE =================
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("username",
                    post.getUser() != null ? post.getUser().getUsername() : "Usuario");

            intent.putExtra("postImage", finalImageUrl);
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("title", post.getTitle());

            intent.putExtra(
                    "time",
                    post.getPrepTime() != null ? String.valueOf(post.getPrepTime()) : ""
            );

            intent.putExtra("stepsText",
                    post.getSteps() != null ? post.getSteps() : "");

            // ================= FIX INGREDIENTES =================
            if (post.getIngredients() != null) {

                StringBuilder ingredientsText = new StringBuilder();

                for (RecipeIngredientResponse ri : post.getIngredients()) {
                    if (ri != null && ri.getIngredient() != null) {
                        ingredientsText
                                .append("• ")
                                .append(ri.getIngredient().getName())
                                .append("\n");
                    }
                }

                intent.putExtra("ingredientsText", ingredientsText.toString());

            } else {
                intent.putExtra("ingredientsText", "");
            }

            // ================= USER ID =================
            int userId = -1;

            if (post.getUser() != null) {
                userId = post.getUser().getId();
            }

            if (userId == -1 && post.getUserId() != null) {
                userId = post.getUserId();
            }

            intent.putExtra("userId", userId);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.gridIcon);
        }
    }
}