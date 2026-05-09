package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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

import modelo.Category;
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
    private Spinner spinnerCategories;
    private List<Category> categoriesList = new ArrayList<>();
    private String selectedCategory = "Todas las categorías";
    private TextView tvNoResults;
    private Button btnClearFilters;

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

        // ================= Inicializar vistas =================
        btnBackSearch = findViewById(R.id.btnBackSearch);
        etSearch = findViewById(R.id.etSearch);
        gridRecipes = findViewById(R.id.gridRecipes);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        tvNoResults = findViewById(R.id.tvNoResults);
        btnClearFilters = findViewById(R.id.btnClearFilters);

        // Ocultar limpiar filtros al inicio
        btnClearFilters.setVisibility(View.GONE);

        btnBackSearch.setOnClickListener(v -> finish());

        // ================= Lógica para limpiar filtros =================
        btnClearFilters.setOnClickListener(v -> {
            etSearch.setText("");
            spinnerCategories.setSelection(0);
            selectedCategory = "Todas las categorías";
            filtrarRecetas("");
        });

        ApiService api = RetrofitClient.getApiService(this);

        // 1. CARGAR POSTS
        api.getAllPosts().enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allPosts = response.body();
                    Collections.shuffle(allPosts);

                    cargarPosts(allPosts);

                    filtrarRecetas(etSearch.getText().toString());
                }
            }
            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 2. CARGAR CATEGORÍAS
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList = response.body();
                    configureSpinner();
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                t.printStackTrace();
            }
        });

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

        bottomNavigation.setSelectedItemId(R.id.nav_search);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {

                Intent intent = new Intent(this, FeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
                finish();

                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_add) {
                startActivity(new Intent(this, PublicPostActivity.class));
                return true;

            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    // ================= Cargar grid =================
    private void cargarPosts(List<Post> posts) {
        LayoutInflater inflater = LayoutInflater.from(this);
        gridRecipes.removeAllViews();

        for (Post post : posts) {
            View itemView = inflater.inflate(R.layout.item_search_recipe, gridRecipes, false);

            ImageView imgRecipe = itemView.findViewById(R.id.imgRecipe);
            TextView tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            TextView tvRecipeLikes = itemView.findViewById(R.id.tvRecipeLikes);

            tvRecipeName.setText(post.getTitle());
            tvRecipeLikes.setText(post.getLikes() + " me gusta");

            if (post.getPostImage() != null && !post.getPostImage().isEmpty()) {
                Glide.with(this)
                        .load(post.getPostImage())
                        .placeholder(R.drawable.pasta)
                        .into(imgRecipe);
            } else {
                imgRecipe.setImageResource(R.drawable.pasta);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
                intent.putExtra("username", post.getUser() != null ? post.getUser().getUsername() : "usuario");
                intent.putExtra("postImage", post.getPostImage());
                intent.putExtra("likes", post.getLikes());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("postId", post.getId());
                intent.putExtra("stepsText", post.getSteps());

                if (post.getIngredients() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (RecipeIngredientResponse ri : post.getIngredients()) {
                        if (ri != null && ri.getIngredient() != null) {
                            sb.append("• ").append(ri.getIngredient().getName()).append("\n");
                        }
                    }
                    intent.putExtra("ingredientsText", sb.toString());
                }
                startActivity(intent);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 0);
            itemView.setLayoutParams(params);

            gridRecipes.addView(itemView);
        }
    }

    // ================= Filtrar =================
    private void filtrarRecetas(String text) {
        String q = text.toLowerCase().trim();
        List<Post> filtered = new ArrayList<>();

        for (Post post : allPosts) {
            boolean textCoincides = post.getTitle().toLowerCase().contains(q) ||
                    (post.getUser() != null && post.getUser().getUsername().toLowerCase().contains(q));

            boolean categoryCoincides = false;
            if (selectedCategory.equals("Todas las categorías")) {
                categoryCoincides = true;
            } else if (post.getCategory() != null &&
                    post.getCategory().getCategoryName().equalsIgnoreCase(selectedCategory)) {
                categoryCoincides = true;
            }

            if (textCoincides && categoryCoincides) {
                filtered.add(post);
            }
        }

        // Lógica de visibilidad del botón Limpiar
        if (!q.isEmpty() || !selectedCategory.equals("Todas las categorías")) {
            btnClearFilters.setVisibility(View.VISIBLE);
        } else {
            btnClearFilters.setVisibility(View.GONE);
        }

        // Mensaje de no resultados
        if (filtered.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            gridRecipes.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            gridRecipes.setVisibility(View.VISIBLE);
        }

        cargarPosts(filtered);
    }

    private void configureSpinner() {
        List<String> names = new ArrayList<>();
        names.add("Todas las categorías");

        for (Category cat : categoriesList) {
            names.add(cat.getCategoryName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = names.get(position);
                filtrarRecetas(etSearch.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}