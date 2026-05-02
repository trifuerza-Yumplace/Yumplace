package vista;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.bumptech.glide.Glide;

import java.util.List;

import modelo.Post;

public class ProfileGridAdapter extends RecyclerView.Adapter<ProfileGridAdapter.ViewHolder> {

    Context context;
    List<Post> list;

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
            imageUrl = "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcci%C3%B3n.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.pasta)
                .error(R.drawable.pasta)
                .into(holder.image);
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