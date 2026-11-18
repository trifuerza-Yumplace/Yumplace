package controlador;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dto.response.UserResponse;
import modelo.Post;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.ProfileGridAdapter;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView rvMyRecipes;
    private ProfileGridAdapter adapter;
    private List<Post> postList = new ArrayList<>();
    private BottomNavigationView bottomNavigation;
    private ApiService apiService;
    private TokenManager tokenManager;

    private TextView tvProfileName, tvProfileBio, tvProfileHeaderUsername;
    private TextView tvMyPostsCount, tvMyFollowersCount, tvMyFollowingCount, tvNoMyPosts;
    private ImageView imgProfileCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = new TokenManager(this);

        // Control de sesión
        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        // 1. Inicializar vistas
        initViews();

        // 2. Configurar RecyclerView con espaciado
        setupRecyclerView();

        // 3. Configurar navegación
        setupBottomNavigation();

        // 4. Cargar datos
        cargarPerfil();
    }

    private void initViews() {
        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        bottomNavigation = findViewById(R.id.bottomNavProfile);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        imgProfileCircle = findViewById(R.id.imgProfileCircle);
        tvProfileHeaderUsername = findViewById(R.id.tvProfileHeaderUsername);
        tvMyPostsCount = findViewById(R.id.tvMyPostsCount);
        tvMyFollowersCount = findViewById(R.id.tvMyFollowersCount);
        tvMyFollowingCount = findViewById(R.id.tvMyFollowingCount);
        tvNoMyPosts = findViewById(R.id.tvNoMyPosts);

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );
    }

    private void setupRecyclerView() {
        int numberOfColumns = 2;
        rvMyRecipes.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // Espaciado de 12dp entre fotos
        int spacingInPixels = (int) (12 * getResources().getDisplayMetrics().density);
        rvMyRecipes.addItemDecoration(new GridSpacingItemDecoration(numberOfColumns, spacingInPixels, true));

        adapter = new ProfileGridAdapter(this, postList);
        rvMyRecipes.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, FeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, PublicPostActivity.class));
                return true;
            }
            return id == R.id.nav_profile;
        });
    }

    private void cargarPerfil() {
        apiService.getMyProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    tvProfileName.setText(user.getUsername());
                    tvProfileHeaderUsername.setText(user.getUsername());
                    tvProfileBio.setText(user.getBiography() != null && !user.getBiography().isEmpty()
                            ? user.getBiography() : "Sin biografía");

                    if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
                        Glide.with(ProfileActivity.this).load(user.getProfilePhoto()).circleCrop().into(imgProfileCircle);
                    } else {
                        imgProfileCircle.setImageResource(R.drawable.user);
                    }

                    cargarMisPosts();
                    cargarSeguidores(user.getId());
                    cargarSeguidos(user.getId());
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMisPosts() {
        apiService.getMyPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                postList.clear();
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    if (!posts.isEmpty()) {
                        postList.addAll(posts);
                        tvNoMyPosts.setVisibility(View.GONE);
                        rvMyRecipes.setVisibility(View.VISIBLE);
                    } else {
                        tvNoMyPosts.setVisibility(View.VISIBLE);
                        rvMyRecipes.setVisibility(View.GONE);
                    }
                    tvMyPostsCount.setText(String.valueOf(postList.size()));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                tvNoMyPosts.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cargarSeguidores(int userId) {
        apiService.getFollowers(userId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvMyFollowersCount.setText(String.valueOf(response.body().size()));
                }
            }
            @Override public void onFailure(Call<List<UserResponse>> call, Throwable t) {}
        });
    }

    private void cargarSeguidos(int userId) {
        apiService.getFollowing(userId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvMyFollowingCount.setText(String.valueOf(response.body().size()));
                }
            }
            @Override public void onFailure(Call<List<UserResponse>> call, Throwable t) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }

    // Decorador para el espacio entre fotos
    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) outRect.top = spacing;
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) outRect.top = spacing;
                outRect.bottom = spacing;
            }
        }
    }
}