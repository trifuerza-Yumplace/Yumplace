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
        if (postImage == null || postImage.isEmpty()) {
            postImage = DEFAULT_IMAGE;
        }
        String profilePhoto =
                getIntent().getStringExtra("profilePhoto");

        int postId = getIntent().getIntExtra("postId", -1);
        int userId = getIntent().getIntExtra("userId", -1);

        likes = getIntent().getIntExtra("likes", 0);
        likedByUser = getIntent().getBooleanExtra("likedByUser", false);

        String stepsText = getIntent().getStringExtra("stepsText");
        if (stepsText == null) stepsText = "";

        final String finalStepsText = stepsText;

        String title = getIntent().getStringExtra("title");
        if (title == null) title = "";

        String time = getIntent().getStringExtra("time");
        if (time == null) time = "";

        // ================= UI =================
        tvUsername.setText(username != null ? username : "");
        tvLikes.setText(likes + " me gusta");
        tvTitle.setText(title);
        tvTime.setText(!time.isEmpty() ? time + " min" : "");

        Glide.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .circleCrop()
                .into(imgProfile);

        Glide.with(this)
                .load(postImage)
                .placeholder(R.drawable.pasta)
                .into(imgPost);

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

        String[] stepLines = stepsText.split("\n");

        StringBuilder shortStepsBuilder = new StringBuilder();

        SpannableStringBuilder fullStepsBuilder = new SpannableStringBuilder();
        SpannableStringBuilder shortStepsFormatted = new SpannableStringBuilder();

        for (int i = 0; i < stepLines.length; i++) {

            String step = stepLines[i].trim();
            if (step.isEmpty()) continue;

            String number = (i + 1) + ". ";

            // ===== número estilizado =====
            SpannableString numSpan = new SpannableString(number);
            numSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6F00")),
                    0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            numSpan.setSpan(new RelativeSizeSpan(1.4f),
                    0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            numSpan.setSpan(new StyleSpan(Typeface.BOLD),
                    0, number.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // ===== línea completa =====
            SpannableStringBuilder line = new SpannableStringBuilder();
            line.append(numSpan);
            line.append(step);
            line.append("\n\n");

            fullStepsBuilder.append(line);

            // ===== versión corta =====
            if (i < 4) {
                shortStepsFormatted.append(line);
            }
        }

        final boolean[] stepsExpanded = {false};

// mostrar versión corta inicial
        tvSteps.setText(shortStepsFormatted.length() == 0 ? fullStepsBuilder : shortStepsFormatted);

        btnExpandSteps.setOnClickListener(v -> {

            if (stepsExpanded[0]) {
                tvSteps.setText(shortStepsFormatted.length() == 0 ? fullStepsBuilder : shortStepsFormatted);
                btnExpandSteps.setText("Ver más");
            } else {
                tvSteps.setText(fullStepsBuilder);
                btnExpandSteps.setText("Ver menos");
            }

            stepsExpanded[0] = !stepsExpanded[0];
        });

        // ================= INGREDIENTES =================
        if (postId != -1) {

            api.getPostIngredients(postId).enqueue(new Callback<List<RecipeIngredientResponse>>() {
                @Override
                public void onResponse(Call<List<RecipeIngredientResponse>> call,
                                       Response<List<RecipeIngredientResponse>> response) {

                    if (!response.isSuccessful() || response.body() == null) {
                        tvIngredients.setText("Sin ingredientes");
                        return;
                    }

                    StringBuilder sb = new StringBuilder();

                    for (RecipeIngredientResponse ri : response.body()) {
                        if (ri != null && ri.getIngredient() != null) {

                            sb.append("• ")
                                    .append(ri.getIngredient().getName());

                            if (ri.getQuantity() != null && !ri.getQuantity().isEmpty()) {
                                sb.append(" - ").append(ri.getQuantity());
                            }

                            sb.append("\n");
                        }
                    }

                    tvIngredients.setText(sb.toString());
                }

                @Override
                public void onFailure(Call<List<RecipeIngredientResponse>> call, Throwable t) {
                    tvIngredients.setText("Error cargando ingredientes");
                }
            });

        } else {
            tvIngredients.setText("Sin ingredientes");
        }
    }
}