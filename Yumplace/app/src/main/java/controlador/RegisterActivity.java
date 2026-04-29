package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import dto.request.RegisterRequest;
import dto.response.AuthResponse;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvBack;
    private Button btnRegister;
    private EditText etName, etUsername, etEmail, etPassword, etConfirmPassword;

    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        apiService = RetrofitClient.getApiService(this);
        tokenManager = new TokenManager(this);

        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());

        etName = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(0);
        etUsername = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(1);
        etEmail = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(2);
        etPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(3);
        etConfirmPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(4);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String name = etName.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest request = new RegisterRequest(email, username, password);

        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    AuthResponse authResponse = response.body();

                    tokenManager.saveToken(authResponse.getToken());

                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterActivity.this, FeedActivity.class));
                    finish();

                } else {
                    Toast.makeText(RegisterActivity.this, "Error al registrar. Código: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}