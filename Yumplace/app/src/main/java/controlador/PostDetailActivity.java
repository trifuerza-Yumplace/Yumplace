package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import modelo.TokenManager;

public class PostDetailActivity extends AppCompatActivity {

    ImageView btnBack;

    private final String DEFAULT_IMAGE =
            "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcci%C3%B3n.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        TokenManager tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // ================= BACK =================
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // ================= VISTAS =================
        TextView tvUsername = findViewById(R.id.tvUsernameDetail);
        ImageView imgProfile = findViewById(R.id.imgProfileDetail);
        ImageView imgPost = findViewById(R.id.imgPostDetail);
        TextView tvLikes = findViewById(R.id.tvLikesDetail);
        TextView tvIngredients = findViewById(R.id.tvIngredients);
        TextView btnExpandIngredients = findViewById(R.id.btnExpandIngredients);
        TextView tvSteps = findViewById(R.id.tvSteps);
        TextView btnExpandSteps = findViewById(R.id.btnExpandSteps);
        ImageView imgComment = findViewById(R.id.imgCommentDetail);

        TextView tvTitle = findViewById(R.id.tvTitleRecipe);
        TextView tvTime = findViewById(R.id.tvTimeRecipe);

        // ================= DATOS (SAFE) =================
        String username = getIntent().getStringExtra("username");

        String postImage = getIntent().getStringExtra("postImage");
        if (postImage == null || postImage.isEmpty()) {
            postImage = DEFAULT_IMAGE;
        }
        final String finalPostImage = postImage;

        int likes = getIntent().getIntExtra("likes", 0);

        String stepsText = getIntent().getStringExtra("stepsText");
        if (stepsText == null) stepsText = "";
        final String finalStepsText = stepsText;

        String title = getIntent().getStringExtra("title");
        if (title == null) title = "";

        String time = getIntent().getStringExtra("time");
        if (time == null) time = "";

        String ingredientsText = getIntent().getStringExtra("ingredientsText");
        if (ingredientsText == null) ingredientsText = "";
        final String finalIngredientsText = ingredientsText;

        // ================= SET DATA =================
        tvUsername.setText(username != null ? username : "");
        tvLikes.setText(likes + " me gusta");
        tvTitle.setText(title);
        tvTime.setText(!time.isEmpty() ? time + " min" : "");

        imgProfile.setImageResource(R.drawable.user);

        Glide.with(this)
                .load(finalPostImage)
                .placeholder(R.drawable.pasta)
                .into(imgPost);

        // ================= PERFIL CLICK =================
        findViewById(R.id.headerDetail).setOnClickListener(v -> {
            Intent intent = new Intent(PostDetailActivity.this, OtherProfileActivity.class);
            startActivity(intent);
        });

        // ================= INGREDIENTES =================
        String[] ingredientLines = finalIngredientsText.split("\n");
        StringBuilder shortIngredientsBuilder = new StringBuilder();

        for (int i = 0; i < Math.min(4, ingredientLines.length); i++) {
            shortIngredientsBuilder.append(ingredientLines[i]).append("\n");
        }

        String shortIngredients = shortIngredientsBuilder.toString();
        final boolean[] ingredientsExpanded = {false};

        tvIngredients.setText(shortIngredients);

        btnExpandIngredients.setOnClickListener(v -> {

            if (ingredientsExpanded[0]) {
                tvIngredients.setText(shortIngredients);
                btnExpandIngredients.setText("Ver más");
            } else {
                tvIngredients.setText(finalIngredientsText);
                btnExpandIngredients.setText("Ver menos");
            }

            ingredientsExpanded[0] = !ingredientsExpanded[0];
        });

        // ================= PASOS =================
        String[] stepLines = finalStepsText.split("\n");
        StringBuilder shortStepsBuilder = new StringBuilder();

        for (int i = 0; i < Math.min(4, stepLines.length); i++) {
            shortStepsBuilder.append(stepLines[i]).append("\n");
        }

        String shortSteps = shortStepsBuilder.toString();
        final boolean[] stepsExpanded = {false};

        tvSteps.setText(shortSteps);

        btnExpandSteps.setOnClickListener(v -> {

            if (stepsExpanded[0]) {
                tvSteps.setText(shortSteps);
                btnExpandSteps.setText("Ver más");
            } else {
                tvSteps.setText(finalStepsText);
                btnExpandSteps.setText("Ver menos");
            }

            stepsExpanded[0] = !stepsExpanded[0];
        });

        // ================= COMENTARIOS =================
        imgComment.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(this);
            dialog.setContentView(R.layout.bottom_comments);
            dialog.show();
        });
    }
}