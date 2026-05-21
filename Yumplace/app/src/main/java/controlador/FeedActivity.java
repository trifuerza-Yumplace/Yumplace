package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.Post;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.PostAdapter;

public class FeedActivity extends AppCompatActivity {

    // ===== DATA =====
    List<Post> allPosts = new ArrayList<>();
    List<Post> shownPosts = new ArrayList<>();
    List<Post> postList = new ArrayList<>();

    int currentIndex = 0;
    final int PAGE_SIZE = 5;

    // ===== UI =====
    RecyclerView recyclerView;
    PostAdapter adapter;

    boolean isLoading = false;

    private final androidx.activity.result.ActivityResultLauncher<Intent>
            postDetailLauncher =
            registerForActivityResult(
                    new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (result.getResultCode() == RESULT_OK
                                && result.getData() != null) {

                            int updatedPostId =
                                    result.getData()
                                            .getIntExtra("updatedPostId", -1);

                            int newCommentsCount =
                                    result.getData()
                                            .getIntExtra("newCommentsCount", 0);

                            for (Post post : postList) {

                                if (post.getId() == updatedPostId) {

                                    // comentarios
                                    post.setComments(newCommentsCount);

                                    // likes
                                    int newLikesCount =
                                            result.getData()
                                                    .getIntExtra("newLikesCount", post.getLikes());

                                    boolean likedByUser =
                                            result.getData()
                                                    .getBooleanExtra(
                                                            "likedByUser",
                                                            post.isLiked()
                                                    );

                                    post.setLikes(newLikesCount);
                                    post.setLiked(likedByUser);

                                    int position =
                                            postList.indexOf(post);

                                    adapter.notifyItemChanged(position);

                                    break;
                                }
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // ================= TOKEN CHECK =================
        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // ================= RECYCLER =================
        recyclerView = findViewById(R.id.recyclerPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        // ================= LOAD FIRST DATA =================
        cargarPosts();

        // ================= NAVIGATION =================
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;

            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;

            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, PublicPostActivity.class));
                return true;

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }

            return false;
        });

        // ================= SCROLL INFINITO =================
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager == null) return;

                int totalItems = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && lastVisible >= totalItems - 2) {

                    if (currentIndex < allPosts.size()) {
                        isLoading = true;

                        cargarSiguientePagina();

                        isLoading = false;
                    }
                }
            }
        });
    }
    public void openPostDetail(Intent intent) {
        postDetailLauncher.launch(intent);
    }


    // ================= CARGA INICIAL =================
    private void cargarPosts() {

        ApiService api = RetrofitClient.getApiService(this);

        api.getAllPosts().enqueue(new Callback<List<Post>>() {

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    allPosts.clear();

                    // ✅ AQUÍ ESTÁ EL FIX REAL
                    for (Post post : response.body()) {

                        Log.d("USER_JSON_DEBUG",
                                "POST ID: " + post.getId()
                                        + "\nUSER: " + (post.getUser() != null
                                        ? post.getUser().getUsername()
                                        : "NULL")
                                        + "\nPHOTO: " + (post.getUser() != null
                                        ? post.getUser().getPhoto()
                                        : "NULL"));

                        Log.d("COMMENTS_DEBUG",
                                "POST ID: " + post.getId()
                                        + " | comments = " + post.getComments());

                        if (post.getLikes() < 0) {
                            post.setLikes(0);
                        }

                        if (post.getUser() == null) {
                            post.setLikedByUser(false);
                        }

                        allPosts.add(post);
                    }

                    // 🔀 aleatorización inicial
                    java.util.Collections.shuffle(allPosts, new Random(System.currentTimeMillis()));

                    postList.clear();
                    shownPosts.clear();
                    currentIndex = 0;

                    cargarSiguientePagina();

                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("API", "Fallo: " + t.getMessage());
            }
        });
    }

    // ================= PAGINACIÓN =================
    private void cargarSiguientePagina() {

        int count = 0;

        while (currentIndex < allPosts.size() && count < PAGE_SIZE) {

            Post post = allPosts.get(currentIndex);

            if (!shownPosts.contains(post)) {
                postList.add(post);
                shownPosts.add(post);
                count++;
            }

            currentIndex++;
        }

        adapter.notifyDataSetChanged();
    }
}