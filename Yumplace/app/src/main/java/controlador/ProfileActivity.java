package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.bumptech.glide.Glide;
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

    private TextView tvProfileName, tvProfileBio, tvProfileHeaderUsername;
    private TextView tvMyPostsCount, tvMyFollowersCount, tvMyFollowingCount, tvNoMyPosts;
    private ImageView imgProfileCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        // ================= VIEWS =================
        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        bottomNavigation = findViewById(R.id.bottomNavProfile);

        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        imgProfileCircle = findViewById(R.id.imgProfileCircle);
        tvProfileHeaderUsername = findViewById(R.id.tvProfileHeaderUsername);

        tvMyPostsCount = findViewById(R.id.tvMyPostsCount);
        tvMyFollowersCount = findViewById(R.id.tvMyFollowersCount);
        tvMyFollowingCount = findViewById(R.id.tvMyFollowingCount);
        tvNoMyPosts = findViewById(R.id.tvNoMyPosts);

        // ================= RECYCLER =================
        rvMyRecipes.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProfileGridAdapter(this, postList);
        rvMyRecipes.setAdapter(adapter);

        // ================= BOTTOM NAV =================
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

            } else return id == R.id.nav_profile;
        });

        // Ir a editar perfil
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class))
        );

        cargarPerfil();
    }

    // ================= PERFIL (/users/me) =================
    private void cargarPerfil() {

        apiService.getMyProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    UserResponse user = response.body();

                    tvProfileName.setText(user.getUsername());
                    tvProfileHeaderUsername.setText(user.getUsername());

                    tvProfileBio.setText(
                            user.getBiography() != null && !user.getBiography().isEmpty()
                                    ? user.getBiography()
                                    : "Sin biografía"
                    );

                    String photoUrl = user.getProfilePhoto();

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(photoUrl)
                                .circleCrop()
                                .into(imgProfileCircle);
                    } else {
                        imgProfileCircle.setImageResource(R.drawable.user);
                    }

                    cargarMisPosts();

                    // Cargamos seguidores y seguidos usando el id del usuario actual
                    cargarSeguidores(user.getId());
                    cargarSeguidos(user.getId());

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Error perfil: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Error conexión perfil",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= MIS POSTS (/posts/me) =================
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

                    tvMyPostsCount.setText(String.valueOf(posts.size()));

                } else {

                    tvNoMyPosts.setVisibility(View.VISIBLE);
                    rvMyRecipes.setVisibility(View.GONE);
                    tvMyPostsCount.setText("0");

                    Toast.makeText(ProfileActivity.this,
                            "Error posts: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {

                postList.clear();
                adapter.notifyDataSetChanged();

                tvNoMyPosts.setVisibility(View.VISIBLE);
                rvMyRecipes.setVisibility(View.GONE);
                tvMyPostsCount.setText("0");

                Toast.makeText(ProfileActivity.this,
                        "Error conexión posts",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= SEGUIDORES =================
    private void cargarSeguidores(int userId) {

        apiService.getFollowers(userId).enqueue(new Callback<List<UserResponse>>() {

            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    tvMyFollowersCount.setText(String.valueOf(response.body().size()));
                } else {
                    tvMyFollowersCount.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                tvMyFollowersCount.setText("0");

                Toast.makeText(ProfileActivity.this,
                        "Error seguidores",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= SEGUIDOS =================
    private void cargarSeguidos(int userId) {

        apiService.getFollowing(userId).enqueue(new Callback<List<UserResponse>>() {

            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    tvMyFollowingCount.setText(String.valueOf(response.body().size()));
                } else {
                    tvMyFollowingCount.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                tvMyFollowingCount.setText("0");

                Toast.makeText(ProfileActivity.this,
                        "Error seguidos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }
}