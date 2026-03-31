package controlador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import java.util.ArrayList;
import java.util.List;

import modelo.Post;
import modelo.PostRepository;

public class PublicPostActivity extends AppCompatActivity {

    private LinearLayout containerIngredients;
    private LinearLayout containerSteps;
    private TextView btnAddIngredient;
    private TextView btnAddStep;
    private EditText etTitle;
    private EditText etTime;
    private EditText etTags;
    private Button btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_post);

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
            View ingredientView = containerIngredients.getChildAt(i);
            EditText etIngredient = ingredientView.findViewById(R.id.etIngredient);
            etIngredient.setHint("Ingrediente " + (i + 1));
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
            View stepView = containerSteps.getChildAt(i);
            TextView tvStepNumber = stepView.findViewById(R.id.tvStepNumber);
            EditText etStep = stepView.findViewById(R.id.etStep);

            tvStepNumber.setText((i + 1) + ".");
            etStep.setHint("Paso " + (i + 1));
        }
    }

    private void publicarPost() {
        String titulo = etTitle.getText().toString().trim();
        String tiempoElaboracion = etTime.getText().toString().trim();
        String etiquetas = etTags.getText().toString().trim();

        List<String> ingredientes = new ArrayList<>();
        List<String> pasos = new ArrayList<>();

        for (int i = 0; i < containerIngredients.getChildCount(); i++) {
            View ingredientView = containerIngredients.getChildAt(i);
            EditText etIngredient = ingredientView.findViewById(R.id.etIngredient);
            String ingrediente = etIngredient.getText().toString().trim();

            if (!ingrediente.isEmpty()) {
                ingredientes.add(ingrediente);
            }
        }

        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            View stepView = containerSteps.getChildAt(i);
            EditText etStep = stepView.findViewById(R.id.etStep);
            String paso = etStep.getText().toString().trim();

            if (!paso.isEmpty()) {
                pasos.add(paso);
            }
        }

        if (titulo.isEmpty()) {
            etTitle.setError("Introduce un título");
            return;
        }

        if (ingredientes.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ingrediente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pasos.isEmpty()) {
            Toast.makeText(this, "Añade al menos un paso", Toast.LENGTH_SHORT).show();
            return;
        }

        Post nuevoPost = new Post(
                R.drawable.user,
                "tu_usuario",
                "Ahora",
                R.drawable.pasta,
                0,
                0,
                ingredientes,
                pasos
        );

        PostRepository.postsPublicados.add(0, nuevoPost);

        Toast.makeText(this, "Publicación creada", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(PublicPostActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}