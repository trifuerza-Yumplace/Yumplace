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

import modelo.Post;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.PostAdapter;

public class FeedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter adapter;
    List<Post> postList = new ArrayList<>();

    boolean isLoading = false;

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

        // 🔥 CARGAR POSTS DESDE API
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

        // ================= SCROLL =================
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager != null) {
                    int totalItems = layoutManager.getItemCount();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();

                    if (!isLoading && lastVisible >= totalItems - 2) {
                        isLoading = true;

                        // ⚠️ Tu API no tiene paginación → recargamos
                        cargarPosts();
                    }
                }
            }
        });
    }

    // ================= API CALL =================
    private void cargarPosts() {

        ApiService api = RetrofitClient.getApiService(this);

        Call<List<Post>> call = api.getAllPosts();

        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    List<Post> nuevosPosts = response.body();

                    postList.clear(); // recarga completa
                    postList.addAll(nuevosPosts);

                    adapter.notifyDataSetChanged();

                    isLoading = false;

                } else {
                    Log.e("API", "Error: " + response.code());
                    isLoading = false;
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.e("API", "Fallo: " + t.getMessage());
                isLoading = false;
            }
        });
    }
}