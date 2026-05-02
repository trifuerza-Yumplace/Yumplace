package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicPostActivity extends AppCompatActivity {

    private LinearLayout containerIngredients;
    private LinearLayout containerSteps;
    private TextView btnAddIngredient;
    private TextView btnAddStep;
    private EditText etTitle;
    private EditText etTime;
    private EditText etTags;
    private Button btnPublish;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_post);

        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        containerIngredients = findViewById(R.id.containerIngredients);
        containerSteps = findViewById(R.id.containerSteps);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddStep = findViewById(R.id.btnAddStep);

        etTitle = findViewById(R.id.etTitle);
        etTime = findViewById(R.id.etTime);
        etTags = findViewById(R.id.etTags);
        btnPublish = findViewById(R.id.btnPublish);

        addIngredient();
        addStep();

        btnAddIngredient.setOnClickListener(v -> addIngredient());
        btnAddStep.setOnClickListener(v -> addStep());
        btnPublish.setOnClickListener(v -> publicarPost());
    }

    private void addIngredient() {
        View ingredientView = LayoutInflater.from(this)
                .inflate(R.layout.item_ingredient, containerIngredients, false);

        EditText etIngredient = ingredientView.findViewById(R.id.etIngredient);
        TextView btnDeleteIngredient = ingredientView.findViewById(R.id.btnDeleteIngredient);

        int ingredientNumber = containerIngredients.getChildCount() + 1;
        etIngredient.setHint("Ingrediente " + ingredientNumber);

        btnDeleteIngredient.setOnClickListener(v -> {
            containerIngredients.removeView(ingredientView);
            renumberIngredients();
        });

        containerIngredients.addView(ingredientView);
    }

    private void renumberIngredients() {
        for (int i = 0; i < containerIngredients.getChildCount(); i++) {
            View v = containerIngredients.getChildAt(i);
            EditText et = v.findViewById(R.id.etIngredient);
            et.setHint("Ingrediente " + (i + 1));
        }
    }

    private void addStep() {
        View stepView = LayoutInflater.from(this)
                .inflate(R.layout.item_paso, containerSteps, false);

        TextView tvStepNumber = stepView.findViewById(R.id.tvStepNumber);
        EditText etStep = stepView.findViewById(R.id.etStep);
        TextView btnDeleteStep = stepView.findViewById(R.id.btnDeleteStep);

        int stepNumber = containerSteps.getChildCount() + 1;
        tvStepNumber.setText(stepNumber + ".");
        etStep.setHint("Paso " + stepNumber);

        btnDeleteStep.setOnClickListener(v -> {
            containerSteps.removeView(stepView);
            renumberSteps();
        });

        containerSteps.addView(stepView);
    }

    private void renumberSteps() {
        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            View v = containerSteps.getChildAt(i);
            TextView tv = v.findViewById(R.id.tvStepNumber);
            EditText et = v.findViewById(R.id.etStep);

            tv.setText((i + 1) + ".");
            et.setHint("Paso " + (i + 1));
        }
    }

    private void publicarPost() {

        String titulo = etTitle.getText().toString().trim();
        String descripcion = etTags.getText().toString().trim();

        List<String> pasos = new ArrayList<>();

        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            View v = containerSteps.getChildAt(i);
            EditText et = v.findViewById(R.id.etStep);

            String text = et.getText().toString().trim();
            if (!text.isEmpty()) pasos.add(text);
        }

        if (titulo.isEmpty()) {
            etTitle.setError("Introduce un título");
            return;
        }

        if (pasos.isEmpty()) {
            Toast.makeText(this, "Añade pasos", Toast.LENGTH_SHORT).show();
            return;
        }

        // ⚠️ IMPORTANTE: steps es STRING en backend
        String stepsFinal = String.join("\n", pasos);

        // Crear objeto Post real
        modelo.Post post = new modelo.Post();
        post.setTitle(titulo);
        post.setDescription(descripcion);
        post.setSteps(stepsFinal);
        post.setPhoto(""); // si luego subes imágenes, aquí irá URL
        post.setPrepTime(0);
        post.setDifficulty("easy");

        apiService.createPost(post).enqueue(new Callback<modelo.Post>() {
            @Override
            public void onResponse(Call<modelo.Post> call, Response<modelo.Post> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(PublicPostActivity.this,
                            "Post creado",
                            Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Toast.makeText(PublicPostActivity.this,
                            "Error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<modelo.Post> call, Throwable t) {
                Toast.makeText(PublicPostActivity.this,
                        "Error conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}