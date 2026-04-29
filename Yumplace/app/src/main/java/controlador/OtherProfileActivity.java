package controlador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import dto.response.UserResponse;
import modelo.Post;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.ProfileGridAdapter;

public class OtherProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProfileGridAdapter adapter;
    private List<Post> postList;

    private ApiService apiService;

    private TextView tvOtherUsername, tvOtherName, tvOtherBio;
    private TextView tvOtherPostsCount, tvOtherFollowersCount, tvOtherFollowingCount;
    private Button btnFollow;

    private boolean isFollowing = false;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        apiService = RetrofitClient.getApiService(this);

        tvOtherUsername = findViewById(R.id.tvOtherUsername);
        tvOtherName = findViewById(R.id.tvOtherName);
        tvOtherBio = findViewById(R.id.tvOtherBio);
        tvOtherPostsCount = findViewById(R.id.tvOtherPostsCount);
        tvOtherFollowersCount = findViewById(R.id.tvOtherFollowersCount);
        tvOtherFollowingCount = findViewById(R.id.tvOtherFollowingCount);
        btnFollow = findViewById(R.id.btnFollow);

        recyclerView = findViewById(R.id.recyclerGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        postList = new ArrayList<>();
        adapter = new ProfileGridAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);

        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "No se ha recibido el usuario", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarPerfilUsuario(userId);
        cargarPostsUsuario(userId);
        cargarSeguidores(userId);
        cargarSeguidos(userId);
        configurarBotonFollow();
    }

    private void cargarPerfilUsuario(int userId) {
        apiService.getUserProfile(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    tvOtherUsername.setText(user.getUsername());
                    tvOtherName.setText(user.getUsername());

                    if (user.getBiography() != null && !user.getBiography().isEmpty()) {
                        tvOtherBio.setText(user.getBiography());
                    } else {
                        tvOtherBio.setText("Sin biografía");
                    }

                } else {
                    Toast.makeText(OtherProfileActivity.this, "Error perfil: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(OtherProfileActivity.this, "Error conexión perfil", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPostsUsuario(int userId) {
        apiService.getPostsByUser(userId).enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList.clear();
                    postList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    tvOtherPostsCount.setText(String.valueOf(postList.size()));
                } else {
                    Toast.makeText(OtherProfileActivity.this, "Error posts: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Toast.makeText(OtherProfileActivity.this, "Error conexión posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSeguidores(int userId) {
        apiService.getFollowers(userId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvOtherFollowersCount.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(OtherProfileActivity.this, "Error seguidores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSeguidos(int userId) {
        apiService.getFollowing(userId).enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvOtherFollowingCount.setText(String.valueOf(response.body().size()));
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(OtherProfileActivity.this, "Error seguidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotonFollow() {
        btnFollow.setOnClickListener(v -> {
            if (!isFollowing) {
                apiService.followUser(userId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            isFollowing = true;
                            btnFollow.setText("Siguiendo");
                            Toast.makeText(OtherProfileActivity.this, "Siguiendo", Toast.LENGTH_SHORT).show();
                            cargarSeguidores(userId);
                        } else {
                            Toast.makeText(OtherProfileActivity.this, "Error follow: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(OtherProfileActivity.this, "Error follow", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                apiService.unfollowUser(userId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            isFollowing = false;
                            btnFollow.setText("Seguir");
                            Toast.makeText(OtherProfileActivity.this, "Dejado de seguir", Toast.LENGTH_SHORT).show();
                            cargarSeguidores(userId);
                        } else {
                            Toast.makeText(OtherProfileActivity.this, "Error unfollow: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(OtherProfileActivity.this, "Error unfollow", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
