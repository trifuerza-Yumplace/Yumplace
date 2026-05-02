package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvProfileName, tvProfileBio;
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

        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        bottomNavigation = findViewById(R.id.bottomNavProfile);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileBio = findViewById(R.id.tvProfileBio);
        imgProfileCircle = findViewById(R.id.imgProfileCircle);

        // ================= GRID =================
        rvMyRecipes.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ProfileGridAdapter(this, postList);
        rvMyRecipes.setAdapter(adapter);

        // ================= LOAD DATA =================
        cargarPerfil();
        cargarMisPosts();

        // ================= NAV =================
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, FeedActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, PublicPostActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
    }

    // ================= PERFIL =================
    private void cargarPerfil() {

        apiService.getMyProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    UserResponse user = response.body();

                    tvProfileName.setText(user.getUsername());

                    if (user.getBiography() != null && !user.getBiography().isEmpty()) {
                        tvProfileBio.setText(user.getBiography());
                    } else {
                        tvProfileBio.setText("Sin biografía");
                    }

                    // FOTO PERFIL
                    String photoUrl = user.getProfilePhoto();

                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(photoUrl)
                                .circleCrop()
                                .into(imgProfileCircle);
                    } else {
                        imgProfileCircle.setImageResource(R.drawable.user);
                    }

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Error perfil: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================= POSTS =================
    private void cargarMisPosts() {

        apiService.getAllPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    postList.clear();
                    postList.addAll(response.body());

                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(ProfileActivity.this,
                            "Error posts",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
        cargarMisPosts();
    }
}