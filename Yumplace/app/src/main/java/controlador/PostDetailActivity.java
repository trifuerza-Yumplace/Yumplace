package controlador;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.content.Intent;
import controlador.OtherProfileActivity;

public class PostDetailActivity extends AppCompatActivity {

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Botón para volver a la pantalla anterior
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Referencias de la vista
        TextView tvUsername = findViewById(R.id.tvUsernameDetail);
        ImageView imgProfile = findViewById(R.id.imgProfileDetail);
        ImageView imgPost = findViewById(R.id.imgPostDetail);
        TextView tvLikes = findViewById(R.id.tvLikesDetail);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView btnExpandIngredients = findViewById(R.id.btnExpandIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);
        TextView btnExpandSteps = findViewById(R.id.btnExpandSteps);
        ImageView imgComment = findViewById(R.id.imgCommentDetail);

        // Recoger datos enviados desde el post seleccionado
        String username = getIntent().getStringExtra("username");
        int profileImage = getIntent().getIntExtra("profileImage", R.drawable.user);
        int postImage = getIntent().getIntExtra("postImage", R.drawable.pasta);
        int likes = getIntent().getIntExtra("likes", 0);
        String ingredientsText = getIntent().getStringExtra("ingredientsText");
        String stepsText = getIntent().getStringExtra("stepsText");

        // Evitar errores si algún dato viene vacío
        if (ingredientsText == null) ingredientsText = "";
        if (stepsText == null) stepsText = "";

        // Mostrar datos básicos de la publicación
        tvUsername.setText(username);
        imgProfile.setImageResource(profileImage);
        imgPost.setImageResource(postImage);
        tvLikes.setText(likes + " me gusta");

        findViewById(R.id.headerDetail).setOnClickListener(v -> {
            android.util.Log.d("CLICK", "HEADER CLICKED");
            Intent intent = new Intent(PostDetailActivity.this, OtherProfileActivity.class);
            startActivity(intent);
        });

        // ===== INGREDIENTES =====
        // Crear versión reducida de ingredientes (máximo 4 líneas)
        String fullIngredients = ingredientsText;
        String[] ingredientLines = fullIngredients.split("\n");
        StringBuilder sbIngredients = new StringBuilder();

        for (int i = 0; i < Math.min(4, ingredientLines.length); i++) {
            sbIngredients.append(ingredientLines[i]).append("\n");
        }

        String shortIngredients = sbIngredients.toString();
        final boolean[] ingredientsExpanded = {false};

        // Mostrar ingredientes reducidos al entrar
        tvIngredients.setText(shortIngredients);

        // Expandir o contraer ingredientes
        btnExpandIngredients.setOnClickListener(v -> {
            if (ingredientsExpanded[0]) {
                tvIngredients.setText(shortIngredients);
                btnExpandIngredients.setText("Ver más");
                ingredientsExpanded[0] = false;
            } else {
                tvIngredients.setText(fullIngredients);
                btnExpandIngredients.setText("Ver menos");
                ingredientsExpanded[0] = true;
            }
        });

        // ===== PREPARACIÓN =====
        // Crear versión reducida de preparación (máximo 4 pasos)
        String fullSteps = stepsText;
        String[] stepLines = fullSteps.split("\n\n");
        StringBuilder sbSteps = new StringBuilder();

        for (int i = 0; i < Math.min(4, stepLines.length); i++) {
            sbSteps.append(stepLines[i]).append("\n\n");
        }

        String shortSteps = sbSteps.toString();
        final boolean[] stepsExpanded = {false};

        // Mostrar pasos reducidos al entrar
        tvSteps.setText(shortSteps);

        // Expandir o contraer preparación
        btnExpandSteps.setOnClickListener(v -> {
            if (stepsExpanded[0]) {
                tvSteps.setText(shortSteps);
                btnExpandSteps.setText("Ver más");
                stepsExpanded[0] = false;
            } else {
                tvSteps.setText(fullSteps);
                btnExpandSteps.setText("Ver menos");
                stepsExpanded[0] = true;
            }
        });

        // ===== COMENTARIOS =====
        // Mostrar comentarios en panel inferior tipo Instagram
        imgComment.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.bottom_comments);
            dialog.show();
        });
    }
}
