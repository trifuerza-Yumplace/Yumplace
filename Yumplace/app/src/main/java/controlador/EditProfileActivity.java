package controlador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import dto.request.UpdateUserRequest;
import dto.response.UserResponse;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = RetrofitClient.getApiService(this);

        ImageView btnBack = findViewById(R.id.btnBackEdit);
        Button btnSave = findViewById(R.id.btnSaveProfile);
        EditText etName = findViewById(R.id.etEditName);
        EditText etBio = findViewById(R.id.etEditBio);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {

            String newName = etName.getText().toString().trim();
            String newBio = etBio.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            // LLAMADA REAL AL BACKEND
            UpdateUserRequest request = new UpdateUserRequest(
                    newName,
                    null,   // telefono
                    null,   // foto
                    newBio
            );

            apiService.updateUser(request).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        finish(); // vuelve al profile → se recarga con onResume
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}