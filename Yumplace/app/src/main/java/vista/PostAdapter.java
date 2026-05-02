package vista;

import android.content.Context;
import android.content.Intent;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    List<Post> postList;
    Context context;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
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

        // ================= DATOS =================

        holder.tvUsername.setText(post.getUsername());
        holder.tvTime.setText(post.getTime());

        holder.tvLikes.setText(post.getLikes() + " me gusta");
        holder.tvComments.setText("Ver los " + post.getComments() + " comentarios");

        // ================= IMAGEN =================
        String imageUrl = post.getPostImage();

        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcci%C3%B3n.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.pasta)
                .error(R.drawable.pasta)
                .into(holder.imgPost);

        // ================= PERFIL CLICK =================
        holder.tvUsername.setOnClickListener(v -> {
            Intent intent = new Intent(context, OtherProfileActivity.class);
            intent.putExtra("userId", post.getId()); // mejor usar id real
            context.startActivity(intent);
        });

        // ================= LIKE ICON =================
        if (post.isLiked()) {
            holder.imgLike.setImageResource(R.drawable.likellen);
        } else {
            holder.imgLike.setImageResource(R.drawable.likevac);
        }

        // ================= CLICK LIKE =================
        holder.imgLike.setOnClickListener(v -> {

            if (post.isLiked()) {
                post.setLiked(false);
                post.setLikes(post.getLikes() - 1);
            } else {
                post.setLiked(true);
                post.setLikes(post.getLikes() + 1);
            }

            notifyItemChanged(position);
        });

        // ================= DETALLE =================
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("username", post.getUsername());
            intent.putExtra("postImage", post.getPostImage());
            intent.putExtra("likes", post.getLikes());

            // ⚠️ steps viene como String
            intent.putExtra("stepsText", post.getSteps());

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