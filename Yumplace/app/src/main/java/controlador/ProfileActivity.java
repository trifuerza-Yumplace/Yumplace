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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import vista.RecipeGridAdapter;

public class ProfileActivity extends AppCompatActivity {

    private RecyclerView rvMyRecipes;
    private RecipeGridAdapter adapter;
    private List<Integer> myImages;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        rvMyRecipes = findViewById(R.id.rvMyRecipes);
        bottomNavigation = findViewById(R.id.bottomNavProfile);
        Button btnEditProfile = findViewById(R.id.btnEditProfile);

        myImages = new ArrayList<>();
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);
        myImages.add(R.drawable.pasta);

        rvMyRecipes.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new RecipeGridAdapter(this, myImages);
        rvMyRecipes.setAdapter(adapter);

        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Función de editar perfil próximamente", Toast.LENGTH_SHORT).show();
        });

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

}