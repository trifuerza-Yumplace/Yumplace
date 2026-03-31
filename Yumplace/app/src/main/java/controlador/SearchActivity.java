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

import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;

public class SearchActivity extends AppCompatActivity {

    private ImageView btnBackSearch;
    private EditText etSearch;
    private GridLayout gridRecipes;
    private BottomNavigationView bottomNavigation;
    private List<RecipeItem> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnBackSearch = findViewById(R.id.btnBackSearch);
        etSearch = findViewById(R.id.etSearch);
        gridRecipes = findViewById(R.id.gridRecipes);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Botón volver atrás
        btnBackSearch.setOnClickListener(v -> finish());

        // Datos de ejemplo
        recipes = new ArrayList<>();
        recipes.add(new RecipeItem("Tarta de Chocolate", 823, R.drawable.pasta));
        recipes.add(new RecipeItem("Sushi Roll Casero", 734, R.drawable.pasta));
        recipes.add(new RecipeItem("Hamburguesa Doble", 612, R.drawable.pasta));
        recipes.add(new RecipeItem("Pizza Margarita", 527, R.drawable.pasta));
        recipes.add(new RecipeItem("Bowl Saludable", 490, R.drawable.pasta));
        recipes.add(new RecipeItem("Pasta Arrabbiata", 451, R.drawable.pasta));

        cargarRecetas(recipes);

        //buscador filtre
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarRecetas(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Marcar búsqueda como seleccionada
        bottomNavigation.setSelectedItemId(R.id.nav_search);

        // Navegación del menú inferior
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

    private void cargarRecetas(List<RecipeItem> recipes) {
        LayoutInflater inflater = LayoutInflater.from(this);
        gridRecipes.removeAllViews();

        for (RecipeItem recipe : recipes) {
            View itemView = inflater.inflate(R.layout.item_search_recipe, gridRecipes, false);

            ImageView imgRecipe = itemView.findViewById(R.id.imgRecipe);
            TextView tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            TextView tvRecipeLikes = itemView.findViewById(R.id.tvRecipeLikes);

            imgRecipe.setImageResource(recipe.imageResId);
            tvRecipeName.setText(recipe.name);
            tvRecipeLikes.setText(recipe.likes + " me gusta");

            // Al pulsar una receta → abrir detalle
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(SearchActivity.this, PostDetailActivity.class);
                intent.putExtra("username", "usuario_busqueda");
                intent.putExtra("profileImage", R.drawable.user);
                intent.putExtra("postImage", recipe.imageResId);
                intent.putExtra("likes", recipe.likes);

                intent.putExtra("ingredientsText",
                        "• 400g de espaguetis\n" +
                                "• 200g de panceta o guanciale\n" +
                                "• 4 huevos\n" +
                                "• 100g de queso pecorino\n" +
                                "• Pimienta negra\n" +
                                "• Sal");

                intent.putExtra("stepsText",
                        "1. Hierve agua con sal y cocina los espaguetis.\n\n" +
                                "2. Corta la panceta y fríela hasta que quede crujiente.\n\n" +
                                "3. Bate los huevos con el queso y la pimienta.\n\n" +
                                "4. Escurre la pasta reservando un poco de agua.\n\n" +
                                "5. Mezcla la pasta con la panceta y añade la mezcla de huevo.\n\n" +
                                "6. Ajusta con agua de cocción si hace falta.\n\n" +
                                "7. Sirve con más queso y pimienta.");

                startActivity(intent);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);

// 👇 IMPORTANTE (separación real)
            params.setMargins(16, 16, 16, 0);

            itemView.setLayoutParams(params);

            gridRecipes.addView(itemView);
        }
    }
    private void filtrarRecetas(String texto) {
        List<RecipeItem> recetasFiltradas = new ArrayList<>();

        for (RecipeItem recipe : recipes) {
            if (recipe.name.toLowerCase().contains(texto.toLowerCase())) {
                recetasFiltradas.add(recipe);
            }
        }

        cargarRecetas(recetasFiltradas);
    }
    private static class RecipeItem {
        String name;
        int likes;
        int imageResId;

        RecipeItem(String name, int likes, int imageResId) {
            this.name = name;
            this.likes = likes;
            this.imageResId = imageResId;
        }
    }
}