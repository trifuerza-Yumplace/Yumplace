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
    // Convierte una lista completa en texto con viñetas o números
    private String listaAIngredientes(List<String> lista) {
        StringBuilder sb = new StringBuilder();
        for (String item : lista) {
            sb.append("• ").append(item).append("\n");
        }
        return sb.toString();
    }

    private String listaAPasos(List<String> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(i + 1).append(". ").append(lista.get(i)).append("\n\n");
        }
        return sb.toString();
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
        holder.tvUsername.setOnClickListener(v -> {
            Intent intent = new Intent(context, OtherProfileActivity.class);

            // de momento usamos un id fijo para probar
            intent.putExtra("userId", 10);

            context.startActivity(intent);
        });
        holder.tvTime.setText(post.time);
        holder.imgPost.setImageResource(post.postImage);
        holder.imgProfile.setOnClickListener(v -> {
            Intent intent = new Intent(context, OtherProfileActivity.class);
            intent.putExtra("userId", 10);
            context.startActivity(intent);
        });
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
        // pulsamos imagen y se abre pantalla de detalles
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PostDetailActivity.class);

            intent.putExtra("username", post.username);
            intent.putExtra("profileImage", post.profileImage);
            intent.putExtra("postImage", post.postImage);
            intent.putExtra("likes", post.likes);

            intent.putExtra("ingredientsText", listaAIngredientes(post.ingredients));
            intent.putExtra("stepsText", listaAPasos(post.steps));

            context.startActivity(intent);
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
