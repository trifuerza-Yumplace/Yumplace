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
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;

    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Inicializamos Retrofit y el gestor del token
        apiService = RetrofitClient.getApiService(this);
        tokenManager = new TokenManager(this);

        // Botón/texto para volver a la pantalla anterior
        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());

        // Obtenemos los campos del formulario desde el contenedor del XML
        etUsername = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(0);
        etEmail = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(1);
        etPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(2);
        etConfirmPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(3);

        // Botón para registrar al usuario
        btnRegister = findViewById(R.id.btnRegister);

        // Al pulsar el botón, se validan los campos y se registra el usuario
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Recogemos los datos introducidos por el usuario
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Limpiamos errores anteriores
        etUsername.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);

        // Guardamos el primer campo con error para enfocar ahí al usuario
        EditText primerCampoConError = null;

        // Validación del nombre de usuario
        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es obligatorio");
            primerCampoConError = etUsername;
        }

        // Validación del email
        if (email.isEmpty()) {
            etEmail.setError("El email es obligatorio");

            if (primerCampoConError == null) {
                primerCampoConError = etEmail;
            }

        } else if (!email.endsWith("@gmail.com")) {
            etEmail.setError("El email debe ser de @gmail.com");

            if (primerCampoConError == null) {
                primerCampoConError = etEmail;
            }
        }

        // Validación de la contraseña
        if (password.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");

            if (primerCampoConError == null) {
                primerCampoConError = etPassword;
            }

        } else if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");

            if (primerCampoConError == null) {
                primerCampoConError = etPassword;
            }
        }

        // Validación de confirmar contraseña
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirma la contraseña");

            if (primerCampoConError == null) {
                primerCampoConError = etConfirmPassword;
            }

        } else if (!password.isEmpty() && !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");

            if (primerCampoConError == null) {
                primerCampoConError = etConfirmPassword;
            }
        }

        // Si hay algún error, enfocamos el primer campo incorrecto y detenemos el proceso
        if (primerCampoConError != null) {
            primerCampoConError.requestFocus();
            return;
        }

        // Creamos el objeto que se enviará al backend
        RegisterRequest request = new RegisterRequest(email, username, password);

        // Llamada al endpoint de registro
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Recibimos la respuesta del backend
                    AuthResponse authResponse = response.body();

                    // Guardamos el token JWT para mantener la sesión iniciada
                    tokenManager.saveToken(authResponse.getToken());

                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    // Si el registro es correcto, pasamos directamente al feed
                    startActivity(new Intent(RegisterActivity.this, FeedActivity.class));
                    finish();

                } else {
                    Toast.makeText(
                            RegisterActivity.this,
                            "Error al registrar. Código: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}