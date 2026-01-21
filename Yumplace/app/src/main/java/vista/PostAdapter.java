package vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;

import java.util.List;

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

        // Datos básicos
        holder.imgProfile.setImageResource(post.profileImage);
        holder.tvUsername.setText(post.username);
        holder.tvTime.setText(post.time);
        holder.imgPost.setImageResource(post.postImage);
        holder.tvLikes.setText(post.likes + " me gusta");
        holder.tvComments.setText("Ver los " + post.comments + " comentarios");

        // Icono de like según estado
        if (post.isLiked) {
            holder.imgLike.setImageResource(R.drawable.likellen);
        } else {
            holder.imgLike.setImageResource(R.drawable.likevac);
        }

        // Click en el botón like
        holder.imgLike.setOnClickListener(v -> {

            if (post.isLiked) {
                // Quitar like
                post.isLiked = false;
                post.likes--;
            } else {
                // Dar like
                post.isLiked = true;
                post.likes++;
            }

            // Actualizamos SOLO este item
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // 👇 ÚNICO ViewHolder (el correcto)
    static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProfile, imgPost, imgLike;
        TextView tvUsername, tvTime, tvLikes, tvComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}
