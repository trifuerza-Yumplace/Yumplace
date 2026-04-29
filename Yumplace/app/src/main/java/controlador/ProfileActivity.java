package controlador;

import android.content.Intent;
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
import modelo.PostRepository;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.RecipeGridAdapter;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView rvMyRecipes;
    private RecipeGridAdapter adapter;
    private List<Integer> myImages;
    private BottomNavigationView bottomNavigation;

    private ApiService apiService;
    private TextView tvProfileName, tvProfileBio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        apiService = RetrofitClient.getApiService(this);

        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        bottomNavigation = findViewById(R.id.bottomNavProfile);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileBio = findViewById(R.id.tvProfileBio);

        cargarPerfil();

        myImages = new ArrayList<>();

        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);

        for (Post post : PostRepository.postsPublicados) {
            myImages.add(0, post.postImage);
        }

        rvMyRecipes.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new RecipeGridAdapter(this, myImages);
        rvMyRecipes.setAdapter(adapter);

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
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

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

                } else {
                    Toast.makeText(ProfileActivity.this, "Error perfil: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPerfil();
    }
}