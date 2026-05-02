package vista;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.bumptech.glide.Glide;

import java.util.List;

import controlador.OtherProfileActivity;
import controlador.PostDetailActivity;
import modelo.Post;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    List<Post> postList;
    Context context;
    ApiService apiService;

    private final String DEFAULT_IMAGE =
            "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcci%C3%B3n.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.apiService = RetrofitClient.getApiService(context);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        Post post = postList.get(position);

        holder.tvUsername.setText(post.getUsername());
        holder.tvTime.setText(post.getTime());
        holder.tvLikes.setText(post.getLikes() + " me gusta");
        holder.tvComments.setText("Ver los " + post.getComments() + " comentarios");

        String imageUrl = post.getPostImage();

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = DEFAULT_IMAGE;
        }

        final String finalImageUrl = imageUrl;

        Glide.with(context)
                .load(finalImageUrl)
                .placeholder(R.drawable.pasta)
                .error(R.drawable.pasta)
                .into(holder.imgPost);

        // ================= PERFIL CLICK =================
        holder.tvUsername.setOnClickListener(v -> {
            Intent intent = new Intent(context, OtherProfileActivity.class);

            int userId = (post.getUser() != null) ? post.getUser().getId() : -1;
            intent.putExtra("userId", userId);

            context.startActivity(intent);
        });

        // ================= LIKE UI =================
        if (post.isLiked()) {
            holder.imgLike.setImageResource(R.drawable.likellen);
        } else {
            holder.imgLike.setImageResource(R.drawable.likevac);
        }

        // ================= LIKE REAL (RETROFIT) =================
        holder.imgLike.setOnClickListener(v -> {

            apiService.likePost(post.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {

                    if (response.isSuccessful()) {

                        // 🔥 actualizar UI SOLO si backend responde OK
                        holder.imgLike.setOnClickListener(v -> {

                            ApiService api = RetrofitClient.getApiService(context);

                            int postId = post.getId();

                            api.likePost(postId).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {

                                    if (response.isSuccessful()) {

                                        // sincronizar UI SOLO si backend OK
                                        if (post.isLiked()) {
                                            post.setLiked(false);
                                            post.setLikes(post.getLikes() - 1);
                                        } else {
                                            post.setLiked(true);
                                            post.setLikes(post.getLikes() + 1);
                                        }

                                        notifyItemChanged(position);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // opcional log
                                }
                            });
                        });

                    } else {
                        Toast.makeText(context, "Error al dar like", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // ================= DETALLE =================
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("username", post.getUsername());
            intent.putExtra("postImage", finalImageUrl);
            intent.putExtra("likes", post.getLikes());
            intent.putExtra("title", post.getTitle());
            intent.putExtra("postId", post.getId());

            intent.putExtra(
                    "time",
                    post.getPrepTime() != null ? String.valueOf(post.getPrepTime()) : "0"
            );

            intent.putExtra("stepsText", post.getSteps());

            if (post.getIngredients() != null) {
                intent.putExtra("ingredientsText", String.join("\n", post.getIngredients()));
            } else {
                intent.putExtra("ingredientsText", "");
            }

            // 🔥 IMPORTANTE PARA PERFIL
            if (post.getUser() != null) {
                intent.putExtra("userId", post.getUser().getId());
            }

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPost, imgLike;
        TextView tvUsername, tvTime, tvLikes, tvComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}