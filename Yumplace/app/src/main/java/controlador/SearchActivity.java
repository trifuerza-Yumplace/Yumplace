package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;

import modelo.Post;
import modelo.RecipeIngredientResponse;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private ImageView btnBackSearch;
    private EditText etSearch;
    private GridLayout gridRecipes;
    private BottomNavigationView bottomNavigation;

    private List<Post> allPosts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        btnBackSearch = findViewById(R.id.btnBackSearch);
        etSearch = findViewById(R.id.etSearch);
        gridRecipes = findViewById(R.id.gridRecipes);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // ================= BACK BUTTON =================
        btnBackSearch.setOnClickListener(v -> finish());

        // ================= LOAD POSTS FROM API (FIX REAL) =================
        ApiService api = RetrofitClient.getApiService(this);

        api.getAllPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    allPosts = response.body();

                    // random como Feed
                    Collections.shuffle(allPosts);

                    cargarPosts(allPosts);
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // ================= SEARCH FILTER =================
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ================= BOTTOM NAV =================
        bottomNavigation.setSelectedItemId(R.id.nav_search);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, FeedActivity.class));
                finish();
                return true;

            } else if (id == R.id.nav_search) {
                return true;

            } else if (id == R.id.nav_add) {
                return true;

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }

            return false;
        });
    }

    // ================= LOAD GRID =================
    private void cargarPosts(List<Post> posts) {

        LayoutInflater inflater = LayoutInflater.from(this);
        gridRecipes.removeAllViews();

        for (Post post : posts) {

            View itemView = inflater.inflate(R.layout.item_search_recipe, gridRecipes, false);

            ImageView imgRecipe = itemView.findViewById(R.id.imgRecipe);
            TextView tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            TextView tvRecipeLikes = itemView.findViewById(R.id.tvRecipeLikes);

            // TITLE
            tvRecipeName.setText(post.getTitle());

            // LIKES
            tvRecipeLikes.setText(post.getLikes() + " me gusta");

            // IMAGE
            if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
                Glide.with(this)
                        .load(post.getPostImage())
                        .placeholder(R.drawable.pasta)
                        .into(imgRecipe);
            } else {
                imgRecipe.setImageResource(R.drawable.pasta);
            }

            // CLICK DETAIL
            itemView.setOnClickListener(v -> {

                Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);

                intent.putExtra("username",
                        post.getUser() != null ? post.getUser().getUsername() : "usuario");

                intent.putExtra("postImage", post.getPostImage());
                intent.putExtra("likes", post.getLikes());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("postId", post.getId());
                intent.putExtra("stepsText", post.getSteps());

                // INGREDIENTS
                if (post.getIngredients() != null) {

                    StringBuilder sb = new StringBuilder();

                    for (RecipeIngredientResponse ri : post.getIngredients()) {
                        if (ri != null && ri.getIngredient() != null) {
                            sb.append("• ")
                                    .append(ri.getIngredient().getName())
                                    .append("\n");
                        }
                    }

                    intent.putExtra("ingredientsText", sb.toString());
                }

                startActivity(intent);
            });

            // GRID LAYOUT
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 0);

            itemView.setLayoutParams(params);

            gridRecipes.addView(itemView);
        }
    }

    // ================= FILTER =================
    private void filtrarRecetas(String texto) {

        String q = texto.toLowerCase().trim();

        List<Post> filtrados = new ArrayList<>();

        for (Post post : allPosts) {

            boolean matchTitle = post.getTitle() != null &&
                    post.getTitle().toLowerCase().contains(q);

            boolean matchDesc = post.getDescription() != null &&
                    post.getDescription().toLowerCase().contains(q);

            boolean matchUser = post.getUser() != null &&
                    post.getUser().getUsername().toLowerCase().contains(q);

            boolean matchIngredients = false;

            if (post.getIngredients() != null) {
                for (RecipeIngredientResponse ing : post.getIngredients()) {
                    if (ing != null &&
                            ing.getIngredient() != null &&
                            ing.getIngredient().getName().toLowerCase().contains(q)) {
                        matchIngredients = true;
                        break;
                    }
                }
            }

            if (matchTitle || matchDesc || matchUser || matchIngredients) {
                filtrados.add(post);
            }
        }

        cargarPosts(filtrados);
    }
}