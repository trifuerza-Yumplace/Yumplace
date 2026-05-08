package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;

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
    private Button btnSave, btnLogout, btnDeleteAccount;
    private TextView tvChangePhoto;

    private String currentPhotoUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tokenManager = new TokenManager(this);

        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        // Asociamos las variables Java con los elementos del XML
        ImageView btnBack = findViewById(R.id.btnBackEdit);
        imgEditProfile = findViewById(R.id.imgEditProfile);
        tvChangePhoto = findViewById(R.id.tvChangePhoto);

        btnSave = findViewById(R.id.btnSaveProfile);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        etName = findViewById(R.id.etEditName);
        etBio = findViewById(R.id.etEditBio);

        // Volver a la pantalla anterior
        btnBack.setOnClickListener(v -> finish());

        // Cargamos los datos actuales del usuario
        loadCurrentProfile();

        // Guardar cambios del perfil
        btnSave.setOnClickListener(v -> updateProfile());

        // cambiamos foto
        tvChangePhoto.setOnClickListener(v -> showChangePhotoDialog());

        // Cerrar sesión
        btnLogout.setOnClickListener(v -> logout());

        // Eliminar cuenta
        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void loadCurrentProfile() {
        apiService.getMyProfile().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    UserResponse user = response.body();

                    // Cargamos nombre de usuario y biografía actuales
                    etName.setText(user.getUsername());

                    if (user.getBiography() != null && !user.getBiography().isEmpty()) {
                        etBio.setText(user.getBiography());
                    } else {
                        etBio.setText("");
                    }

                    // Guardamos la foto actual para no perderla al actualizar el perfil
                    currentPhotoUrl = user.getProfilePhoto();

                    // Mostramos la foto actual igual que en la pantalla de perfil
                    if (currentPhotoUrl != null && !currentPhotoUrl.isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(currentPhotoUrl)
                                .circleCrop()
                                .into(imgEditProfile);
                    } else {
                        imgEditProfile.setImageResource(R.drawable.user);
                    }

                } else {
                    Toast.makeText(
                            EditProfileActivity.this,
                            "Error al cargar perfil: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(
                        EditProfileActivity.this,
                        "Error de conexión al cargar perfil",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void updateProfile() {
        String newName = etName.getText().toString().trim();
        String newBio = etBio.getText().toString().trim();

        etName.setError(null);

        if (newName.isEmpty()) {
            etName.setError("El nombre de usuario no puede estar vacío");
            etName.requestFocus();
            return;
        }

        // Mantenemos currentPhotoUrl para no borrar la foto existente al guardar
        UpdateUserRequest request = new UpdateUserRequest(
                newName,
                currentPhotoUrl, // foto actual o nueva foto seleccionada
                null,            // teléfono
                newBio
        );

        apiService.updateUser(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(
                            EditProfileActivity.this,
                            "Cambios guardados correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();

                } else {
                    Toast.makeText(
                            EditProfileActivity.this,
                            "Error al actualizar: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(
                        EditProfileActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // cambiar foto
    private void showChangePhotoDialog() {
        EditText inputUrl = new EditText(this);
        inputUrl.setHint("Pega la URL de la imagen");
        inputUrl.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        inputUrl.setPadding(40, 20, 40, 20);

        if (currentPhotoUrl != null && !currentPhotoUrl.isEmpty()) {
            inputUrl.setText(currentPhotoUrl);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cambiar foto de perfil")
                .setMessage("Introduce una URL de imagen para usarla como foto de perfil.")
                .setView(inputUrl)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Aceptar", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnAccept = dialog.getButton(AlertDialog.BUTTON_POSITIVE);

            btnAccept.setOnClickListener(v -> {
                String photoUrl = inputUrl.getText().toString().trim();

                if (photoUrl.isEmpty()) {
                    inputUrl.setError("Introduce una URL");
                    return;
                }

                if (!photoUrl.startsWith("http://") && !photoUrl.startsWith("https://")) {
                    inputUrl.setError("La URL debe empezar por http:// o https://");
                    return;
                }

                // Guardamos temporalmente la nueva URL
                currentPhotoUrl = photoUrl;

                // Mostramos la imagen en la pantalla antes de guardar
                Glide.with(EditProfileActivity.this)
                        .load(currentPhotoUrl)
                        .circleCrop()
                        .error(R.drawable.user)
                        .into(imgEditProfile);

                Toast.makeText(
                        EditProfileActivity.this,
                        "Imagen seleccionada. Pulsa Guardar Cambios para aplicarla",
                        Toast.LENGTH_LONG
                ).show();

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    //eliminar cuenta
    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Seguro que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setNegativeButton("No, cancelar", null)
                .setPositiveButton("Sí, eliminar", (dialog, which) -> deleteAccount())
                .show();
    }

    private void deleteAccount() {
        apiService.deleteMyAccount().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    tokenManager.clearToken();

                    Toast.makeText(
                            EditProfileActivity.this,
                            "Cuenta eliminada correctamente",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(
                            EditProfileActivity.this,
                            "No se pudo eliminar la cuenta. Código: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(
                        EditProfileActivity.this,
                        "Error de conexión al eliminar la cuenta",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void logout() {
        tokenManager.clearToken();

        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}