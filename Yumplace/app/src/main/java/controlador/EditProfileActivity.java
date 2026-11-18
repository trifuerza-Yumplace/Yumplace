package controlador;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;

import dto.request.UpdateUserRequest;
import dto.response.UserResponse;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ApiService apiService;
    private TokenManager tokenManager;

    private ImageView imgEditProfile;
    private EditText etName, etBio;
    private Button btnSave, btnLogout;
    private TextView tvChangePhoto, btnDeleteAccount;

    private String currentPhotoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tokenManager = new TokenManager(this);

        // Seguridad: Si no hay token, al login
        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        // ================= CABECERA (REGRESO) =================
        ImageView ivBack = findViewById(R.id.ivBackEdit);
        TextView tvCancel = findViewById(R.id.tvCancelEdit);
        LinearLayout layoutBack = findViewById(R.id.layoutBackEdit);

        ivBack.setOnClickListener(v -> finish());
        tvCancel.setOnClickListener(v -> finish());
        layoutBack.setOnClickListener(v -> finish());

        // ================= INICIALIZAR VISTAS =================
        imgEditProfile = findViewById(R.id.imgEditProfile);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);
        etName = findViewById(R.id.etEditName);
        etBio = findViewById(R.id.etEditBio);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        // ================= LÓGICA =================
        loadCurrentProfile();

        btnSave.setOnClickListener(v -> updateProfile());
        tvChangePhoto.setOnClickListener(v -> showChangePhotoDialog());
        btnLogout.setOnClickListener(v -> logout());
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void loadCurrentProfile() {
        apiService.getMyProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    etName.setText(user.getUsername());
                    etBio.setText(user.getBiography() != null ? user.getBiography() : "");
                    currentPhotoUrl = user.getProfilePhoto();

                    if (currentPhotoUrl != null && !currentPhotoUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(currentPhotoUrl)
                                .circleCrop()
                                .into(imgEditProfile);
                    } else {
                        imgEditProfile.setImageResource(R.drawable.user);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        String newBio = etBio.getText().toString().trim();

        if (newName.isEmpty()) {
            etName.setError("El nombre es obligatorio");
            return;
        }

        UpdateUserRequest request = new UpdateUserRequest(newName, currentPhotoUrl, null, newBio);

        apiService.updateUser(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChangePhotoDialog() {
        EditText inputUrl = new EditText(this);
        inputUrl.setHint("https://ejemplo.com/foto.jpg");
        inputUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        inputUrl.setPadding(50, 30, 50, 30);

        if (currentPhotoUrl != null) inputUrl.setText(currentPhotoUrl);

        new AlertDialog.Builder(this)
                .setTitle("URL de la foto de perfil")
                .setView(inputUrl)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cargar", (dialog, which) -> {
                    String url = inputUrl.getText().toString().trim();
                    if (url.startsWith("http")) {
                        currentPhotoUrl = url;
                        Glide.with(EditProfileActivity.this)
                                .load(currentPhotoUrl)
                                .circleCrop()
                                .error(R.drawable.user)
                                .into(imgEditProfile);
                    } else {
                        Toast.makeText(this, "URL no válida", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void logout() {
        tokenManager.clearToken();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Estás seguro? Esta acción borrará todas tus recetas.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar definitivamente", (dialog, which) -> deleteAccount())
                .show();
    }

    private void deleteAccount() {
        apiService.deleteMyAccount().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    logout();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error al conectar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}