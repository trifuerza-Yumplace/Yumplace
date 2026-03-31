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

import android.content.Intent;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FeedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter adapter;
    List<Post> postList;
    boolean isLoading = false;
    List<String> ingredientes1;
    List<String> pasos1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerView = findViewById(R.id.recyclerPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ingredientes1 = new ArrayList<>();
        pasos1 = new ArrayList<>();

        ingredientes1.add("400g de espaguetis");
        ingredientes1.add("200g de panceta o guanciale");
        ingredientes1.add("4 huevos");
        ingredientes1.add("100g de queso pecorino");
        ingredientes1.add("Pimienta negra");
        ingredientes1.add("Sal");

        pasos1.add("Hierve agua con sal y cocina los espaguetis.");
        pasos1.add("Corta la panceta y fríela hasta que quede crujiente.");
        pasos1.add("Bate los huevos con el queso y la pimienta.");
        pasos1.add("Escurre la pasta reservando un poco de agua.");
        pasos1.add("Mezcla la pasta con la panceta y añade la mezcla de huevo.");
        pasos1.add("Ajusta con agua de cocción si hace falta.");
        pasos1.add("Sirve con más queso y pimienta.");

        // 5 post dinamicos --> back llamar por scroll
        postList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            postList.add(new Post(
                    R.drawable.user,
                    "usuario_" + i,
                    "hace " + i + " horas",
                    R.drawable.pasta,
                    100 + i,
                    10 + i,
                    ingredientes1,
                    pasos1
            ));
        }

        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        // naveggar entre pantallas
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

// marcar inicio como seleccionado
        bottomNav.setSelectedItemId(R.id.nav_home);

// navegación del menú inferior
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(FeedActivity.this, SearchActivity.class));
                return true;
            } else if (id == R.id.nav_add) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(FeedActivity.this, ProfileActivity.class));
                return true;
            }

            return false;
        });
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
                    100 + i,
                    10 + i,
                    ingredientes1,
                    pasos1
            ));
        }

        adapter.notifyDataSetChanged();
        isLoading = false;
    }
}
