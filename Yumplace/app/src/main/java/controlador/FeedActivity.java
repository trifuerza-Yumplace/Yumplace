package controlador;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;

import java.util.ArrayList;
import java.util.List;

import modelo.Post;
import vista.PostAdapter;

public class FeedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter adapter;
    List<Post> postList;
    boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerView = findViewById(R.id.recyclerPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 5 post dinamicos --> back llamar por scroll
        postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            postList.add(new Post(
                    R.drawable.user,
                    "usuario_" + i,
                    "hace " + i + " horas",
                    R.drawable.pasta,
                    100 + i,
                    10 + i
            ));
        }

        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        // al hacer scroll carga las publi
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int totalItems = layoutManager.getItemCount();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();

                    // Si estamos cerca del final → cargamos más
                    if (!isLoading && lastVisible >= totalItems - 2) {
                        isLoading = true;
                        cargarMasPosts();
                    }
                }
            }
        });
    }

    // cargar mas publicaciones
    private void cargarMasPosts() {
        int start = postList.size();

        for (int i = start; i < start + 5; i++) {
            postList.add(new Post(
                    R.drawable.user,
                    "usuario_" + i,
                    "hace " + i + " horas",
                    R.drawable.pasta,
                    200 + i,
                    20 + i
            ));
        }

        adapter.notifyDataSetChanged();
        isLoading = false;
    }
}
