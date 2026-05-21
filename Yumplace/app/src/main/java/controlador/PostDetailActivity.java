package controlador;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import modelo.Comment;
import modelo.RecipeIngredientResponse;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vista.CommentAdapter;

public class PostDetailActivity extends AppCompatActivity {

    ImageView btnBack;

    private int likes;
    private boolean likedByUser;

    private final String DEFAULT_IMAGE =
            "https://media.istockphoto.com/id/165598110/es/vector/solar-de-construcción.jpg?s=612x612&w=0&k=20&c=CHRUil8J-yeXtkUvetIPKBdXS_mi4fBq7yLPQzpTwfU=";

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

        RecyclerView rvCommentsPreview =
                findViewById(R.id.rvCommentsPreview);

        ImageView imgComment = findViewById(R.id.imgCommentDetail);
        ImageView imgLike = findViewById(R.id.imgLikeDetail);

        TextView tvTitle = findViewById(R.id.tvTitleRecipe);
        TextView tvTime = findViewById(R.id.tvTimeRecipe);
        LinearLayout containerCategories = findViewById(R.id.containerCategories);

        // ================= DATOS =================
        String username = getIntent().getStringExtra("username");

        String postImage = getIntent().getStringExtra("postImage");
        String profilePhoto =
                getIntent().getStringExtra("profilePhoto");

        int postId = getIntent().getIntExtra("postId", -1);
        int userId = getIntent().getIntExtra("userId", -1);

        likes = getIntent().getIntExtra("likes", 0);
        likedByUser = getIntent().getBooleanExtra("likedByUser", false);

        String stepsText = getIntent().getStringExtra("stepsText");
        if (stepsText == null) stepsText = "";

        String ingredientsExtra = getIntent().getStringExtra("ingredientsText");
        final String ingredientsFromIntent = ingredientsExtra != null ? ingredientsExtra : "";

        final String finalStepsText = stepsText;

        String title = getIntent().getStringExtra("title");
        if (title == null) title = "";

        String time = getIntent().getStringExtra("time");
        if (time == null) time = "";

        // ================= UI =================
        tvUsername.setText(username != null ? username : "");
        tvLikes.setText(likes + " me gusta");
        tvTitle.setText(title);
        if (!time.isEmpty()) {
            try {
                int prepMinutes = Integer.parseInt(time);
                tvTime.setText(formatPrepTime(prepMinutes));
            } catch (NumberFormatException e) {
                tvTime.setText(time + " min");
            }
        } else {
            tvTime.setText("");
        }

        Glide.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .circleCrop()
                .into(imgProfile);

        if (postImage != null && !postImage.isEmpty()) {
            Glide.with(this)
                    .load(postImage)
                    .placeholder(R.drawable.loading_post)
                    .error(R.drawable.no_image)
                    .into(imgPost);
        } else {
            imgPost.setImageResource(R.drawable.no_image);
        }

        imgLike.setImageResource(likedByUser ? R.drawable.likellen : R.drawable.likevac);

        ApiService api = RetrofitClient.getApiService(this);

        // ================= CATEGORÍAS =================
        containerCategories.removeAllViews();

        String categoryName = getIntent().getStringExtra("category");

        if (categoryName != null && !categoryName.isEmpty()) {

            TextView tag = new TextView(this);
            tag.setText("#" + categoryName.toLowerCase());
            tag.setBackgroundColor(Color.parseColor("#FDE2C5"));
            tag.setTextColor(Color.parseColor("#FF6F00"));
            tag.setPadding(20, 10, 20, 10);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            params.setMarginEnd(16);

            tag.setLayoutParams(params);

            containerCategories.addView(tag);
        }

        // ================= COMENTARIOS PREVIEW =================
        rvCommentsPreview.setLayoutManager(
                new LinearLayoutManager(this)
        );

        List<Comment> commentsPreview = new ArrayList<>();

        CommentAdapter commentsAdapter =
                new CommentAdapter(commentsPreview);

        rvCommentsPreview.setAdapter(commentsAdapter);

        api.getComments(postId).enqueue(new Callback<List<Comment>>() {

            @Override
            public void onResponse(Call<List<Comment>> call,
                                   Response<List<Comment>> response) {

                if (response.isSuccessful()
                        && response.body() != null) {

                    commentsPreview.clear();
                    commentsPreview.addAll(response.body());

                    commentsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call,
                                  Throwable t) {

            }
        });

        // ================= LIKE =================
        imgLike.setOnClickListener(v -> {

            if (likedByUser) {

                api.unlikePost(postId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            likedByUser = false;
                            likes--;
                            imgLike.setImageResource(R.drawable.likevac);
                            tvLikes.setText(likes + " me gusta");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });

            } else {

                api.likePost(postId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            likedByUser = true;
                            likes++;
                            imgLike.setImageResource(R.drawable.likellen);
                            tvLikes.setText(likes + " me gusta");
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {}
                });
            }
        });

        // ================= PERFIL =================
        findViewById(R.id.headerDetail).setOnClickListener(v -> {
            if (userId == -1) return;

            Intent intent = new Intent(this, OtherProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        // ================= COMENTARIOS =================
        imgComment.setOnClickListener(v -> {

            BottomSheetDialog dialog = new BottomSheetDialog(this);
            View view = getLayoutInflater().inflate(R.layout.bottom_comments, null);
            dialog.setContentView(view);

            RecyclerView rv = view.findViewById(R.id.rvComments);
            EditText etComment = view.findViewById(R.id.etComment);
            ImageView btnSend = view.findViewById(R.id.btnSendComment);

            rv.setLayoutManager(new LinearLayoutManager(this));

            List<Comment> commentList = new ArrayList<>();
            CommentAdapter adapter = new CommentAdapter(commentList);
            rv.setAdapter(adapter);

            api.getComments(postId).enqueue(new Callback<List<Comment>>() {
                @Override
                public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        commentList.clear();
                        commentList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<Comment>> call, Throwable t) {}
            });

            btnSend.setOnClickListener(v1 -> {

                if (etComment.getText().toString().trim().isEmpty()) return;

                Comment comment = new Comment();
                comment.setText(etComment.getText().toString());

                api.addComment(postId, comment).enqueue(new Callback<Comment>() {
                    @Override
                    public void onResponse(Call<Comment> call, Response<Comment> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            commentList.add(response.body());
                            adapter.notifyItemInserted(commentList.size() - 1);

                            etComment.setText("");
                        }
                    }

                    @Override
                    public void onFailure(Call<Comment> call, Throwable t) {}
                });
            });

            dialog.show();
        });

        // ================= PASOS (FORMATO PRO) =================

        List<String> stepsList = splitSteps(stepsText);

        final int STEP_LIMIT = 4;

        SpannableStringBuilder fullStepsBuilder =
                buildStepsSpannable(stepsList, stepsList.size());

        SpannableStringBuilder shortStepsBuilder =
                buildStepsSpannable(stepsList, Math.min(STEP_LIMIT, stepsList.size()));

        final boolean[] stepsExpanded = {false};

        if (stepsList.isEmpty()) {

            tvSteps.setText("Sin pasos de preparación");
            btnExpandSteps.setVisibility(View.GONE);

        } else if (stepsList.size() <= STEP_LIMIT) {

            tvSteps.setText(fullStepsBuilder);
            btnExpandSteps.setVisibility(View.GONE);

        } else {

            tvSteps.setText(shortStepsBuilder);
            btnExpandSteps.setVisibility(View.VISIBLE);
            btnExpandSteps.setText("Ver más");

            btnExpandSteps.setOnClickListener(v -> {

                if (stepsExpanded[0]) {
                    tvSteps.setText(shortStepsBuilder);
                    btnExpandSteps.setText("Ver más");
                } else {
                    tvSteps.setText(fullStepsBuilder);
                    btnExpandSteps.setText("Ver menos");
                }

                stepsExpanded[0] = !stepsExpanded[0];
            });
        }

        // ================= INGREDIENTES =================
        if (postId != -1) {

            api.getPostIngredients(postId).enqueue(new Callback<List<RecipeIngredientResponse>>() {
                @Override
                public void onResponse(Call<List<RecipeIngredientResponse>> call,
                                       Response<List<RecipeIngredientResponse>> response) {

                    List<String> ingredientLines = new ArrayList<>();

                    if (response.isSuccessful() && response.body() != null) {

                        for (RecipeIngredientResponse ri : response.body()) {
                            if (ri != null && ri.getIngredient() != null) {

                                StringBuilder line = new StringBuilder();

                                line.append("• ")
                                        .append(ri.getIngredient().getName());

                                if (ri.getQuantity() != null && !ri.getQuantity().isEmpty()) {
                                    line.append(" - ").append(ri.getQuantity());
                                }

                                ingredientLines.add(line.toString());
                            }
                        }
                    }

                    // Si la API no devuelve ingredientes, usamos los que ya venían desde el PostAdapter
                    if (ingredientLines.isEmpty()) {
                        ingredientLines.addAll(splitIngredients(ingredientsFromIntent));
                    }

                    showExpandableIngredients(
                            tvIngredients,
                            btnExpandIngredients,
                            ingredientLines
                    );
                }

                @Override
                public void onFailure(Call<List<RecipeIngredientResponse>> call, Throwable t) {

                    List<String> ingredientLines = splitIngredients(ingredientsFromIntent);

                    showExpandableIngredients(
                            tvIngredients,
                            btnExpandIngredients,
                            ingredientLines
                    );
                }
            });

        } else {

            List<String> ingredientLines = splitIngredients(ingredientsFromIntent);

            showExpandableIngredients(
                    tvIngredients,
                    btnExpandIngredients,
                    ingredientLines
            );
        }
    }
    private List<String> splitSteps(String stepsText) {

        List<String> steps = new ArrayList<>();

        if (stepsText == null || stepsText.trim().isEmpty()) {
            return steps;
        }

        String normalized = stepsText
                .replace("\r", "")
                .trim();

        // Si vienen pasos en una sola línea tipo:
        // 1. Lavar... 2. Cocer... 3. Servir...
        // los separamos en líneas distintas
        normalized = normalized.replaceAll("\\s+(?=\\d+\\s*[\\.)]\\s+)", "\n");

        String[] parts = normalized.split("\\n+");

        for (String part : parts) {

            String step = part.trim();

            // Quitamos números que ya vengan del backend o del usuario:
            // 1. texto
            // 2) texto
            step = step.replaceFirst("^\\d+\\s*[\\.)]\\s*", "");

            // Quitamos posibles viñetas
            step = step.replaceFirst("^[-•]\\s*", "");

            if (!step.isEmpty()) {
                steps.add(step);
            }
        }

        return steps;
    }

    // tiempo horas o minutos
    private String formatPrepTime(int totalMinutes) {

        if (totalMinutes < 60) {
            return totalMinutes + " min";
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (minutes == 0) {
            return hours + " h";
        }

        return hours + " h " + minutes + " min";
    }
    private SpannableStringBuilder buildStepsSpannable(List<String> steps, int maxSteps) {

        SpannableStringBuilder builder = new SpannableStringBuilder();

        int limit = Math.min(maxSteps, steps.size());

        for (int i = 0; i < limit; i++) {

            String number = (i + 1) + ". ";

            SpannableString numSpan = new SpannableString(number);

            numSpan.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#FF6F00")),
                    0,
                    number.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            numSpan.setSpan(
                    new RelativeSizeSpan(1.3f),
                    0,
                    number.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            numSpan.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    0,
                    number.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            builder.append(numSpan);
            builder.append(steps.get(i));

            // Solo un salto de línea para que no quede tan separado
            if (i < limit - 1) {
                builder.append("\n");
            }
        }

        return builder;
    }
    private List<String> splitIngredients(String ingredientsText) {

        List<String> ingredients = new ArrayList<>();

        if (ingredientsText == null || ingredientsText.trim().isEmpty()) {
            return ingredients;
        }

        String normalized = ingredientsText
                .replace("\r", "")
                .trim();

        String[] lines = normalized.split("\\n+");

        for (String line : lines) {

            String ingredient = line.trim();

            if (ingredient.isEmpty()) continue;

            ingredient = ingredient.replaceFirst("^[-•]\\s*", "");

            ingredients.add("• " + ingredient);
        }

        return ingredients;
    }
    private void showExpandableIngredients(TextView tvIngredients,
                                           TextView btnExpandIngredients,
                                           List<String> ingredients) {

        final int INGREDIENT_LIMIT = 4;

        if (ingredients == null || ingredients.isEmpty()) {
            tvIngredients.setText("Sin ingredientes");
            btnExpandIngredients.setVisibility(View.GONE);
            return;
        }

        String fullText = buildIngredientsText(ingredients, ingredients.size());
        String shortText = buildIngredientsText(
                ingredients,
                Math.min(INGREDIENT_LIMIT, ingredients.size())
        );

        final boolean[] ingredientsExpanded = {false};

        if (ingredients.size() <= INGREDIENT_LIMIT) {

            tvIngredients.setText(fullText);
            btnExpandIngredients.setVisibility(View.GONE);

        } else {

            tvIngredients.setText(shortText);
            btnExpandIngredients.setVisibility(View.VISIBLE);
            btnExpandIngredients.setText("Ver más");

            btnExpandIngredients.setOnClickListener(v -> {

                if (ingredientsExpanded[0]) {
                    tvIngredients.setText(shortText);
                    btnExpandIngredients.setText("Ver más");
                } else {
                    tvIngredients.setText(fullText);
                    btnExpandIngredients.setText("Ver menos");
                }

                ingredientsExpanded[0] = !ingredientsExpanded[0];
            });
        }
    }
    private String buildIngredientsText(List<String> ingredients, int maxItems) {

        StringBuilder sb = new StringBuilder();

        int limit = Math.min(maxItems, ingredients.size());

        for (int i = 0; i < limit; i++) {
            sb.append(ingredients.get(i));

            if (i < limit - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}