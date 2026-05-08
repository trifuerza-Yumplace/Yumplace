package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import dto.request.LoginRequest;
import dto.response.AuthResponse;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView, forgotPasswordTextView;

    private ApiService apiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Asociamos las variables Java con los elementos del XML
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        // Inicializamos Retrofit para comunicarnos con el backend
        apiService = RetrofitClient.getApiService(this);

        // Inicializamos el gestor del token JWT
        tokenManager = new TokenManager(this);

        // Al pulsar iniciar sesión, validamos los datos y llamamos al backend
        loginButton.setOnClickListener(v -> hacerLogin());

        // Navegamos a la pantalla de registro
        registerTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Navegamos a la pantalla de recuperación de contraseña
        forgotPasswordTextView.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void hacerLogin() {
        // Recogemos los datos introducidos por el usuario
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Limpiamos errores anteriores
        emailEditText.setError(null);
        passwordEditText.setError(null);

        // Validamos que el email no esté vacío
        if (email.isEmpty()) {
            emailEditText.setError("Introduce tu email");
            emailEditText.requestFocus();
            return;
        }

        // Validamos que la contraseña no esté vacía
        if (password.isEmpty()) {
            passwordEditText.setError("Introduce tu contraseña");
            passwordEditText.requestFocus();
            return;
        }

        // Creamos el objeto que se enviará al backend
        LoginRequest request = new LoginRequest(email, password);

        // Llamada al endpoint de login
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Guardamos el token JWT recibido del backend
                    AuthResponse authResponse = response.body();
                    tokenManager.saveToken(authResponse.getToken());

                    Toast.makeText(
                            LoginActivity.this,
                            "Inicio de sesión correcto",
                            Toast.LENGTH_SHORT
                    ).show();

                    // Si el login es correcto, vamos al feed
                    startActivity(new Intent(LoginActivity.this, FeedActivity.class));
                    finish();

                } else {
                    String mensaje;

                    // Mensaje amigable si las credenciales son incorrectas
                    if (response.code() == 401 || response.code() == 403) {
                        mensaje = "Email o contraseña incorrectos";
                    } else {
                        mensaje = "No se pudo iniciar sesión. Inténtalo de nuevo";
                    }

                    Toast.makeText(
                            LoginActivity.this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                // Error de conexión con el servidor
                Toast.makeText(
                        LoginActivity.this,
                        "No se pudo conectar con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}