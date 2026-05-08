package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

import dto.request.ResetPasswordRequest;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    // Campos del formulario de recuperación de contraseña
    private EditText emailEditText, usernameEditText, newPasswordEditText, confirmPasswordEditText;
    private Button resetPasswordButton;
    private TextView backToLoginTextView;

    // Servicio de Retrofit para comunicarnos con el backend
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Asociamos las variables Java con los elementos del XML
        emailEditText = findViewById(R.id.resetEmailEditText);
        usernameEditText = findViewById(R.id.resetUsernameEditText);
        newPasswordEditText = findViewById(R.id.resetPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.resetConfirmPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        backToLoginTextView = findViewById(R.id.backToLoginTextView);

        // Inicializamos el servicio de la API
        apiService = RetrofitClient.getApiService(this);

        // Al pulsar el botón, se validan los datos y se solicita el cambio de contraseña
        resetPasswordButton.setOnClickListener(v -> resetPassword());

        // Volvemos a la pantalla anterior, en este caso login
        backToLoginTextView.setOnClickListener(v -> {
            finish();
        });
    }

    private void resetPassword() {
        // Recogemos los valores introducidos por el usuario
        String email = emailEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Limpiamos errores anteriores para no mostrar mensajes antiguos
        emailEditText.setError(null);
        usernameEditText.setError(null);
        newPasswordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        // Guardamos el primer campo con error para enfocar ahí al usuario
        EditText primerCampoConError = null;

        // Validación del email
        if (email.isEmpty()) {
            emailEditText.setError("El email es obligatorio");
            primerCampoConError = emailEditText;

        } else if (!email.endsWith("@gmail.com")) {
            emailEditText.setError("El email debe ser de @gmail.com");

            if (primerCampoConError == null) {
                primerCampoConError = emailEditText;
            }
        }

        // Validación del nombre de usuario
        if (username.isEmpty()) {
            usernameEditText.setError("El nombre de usuario es obligatorio");

            if (primerCampoConError == null) {
                primerCampoConError = usernameEditText;
            }
        }

        // Validación de la nueva contraseña
        if (newPassword.isEmpty()) {
            newPasswordEditText.setError("La nueva contraseña es obligatoria");

            if (primerCampoConError == null) {
                primerCampoConError = newPasswordEditText;
            }

        } else if (newPassword.length() < 6) {
            newPasswordEditText.setError("Mínimo 6 caracteres");

            if (primerCampoConError == null) {
                primerCampoConError = newPasswordEditText;
            }
        }

        // Validación de la confirmación de contraseña
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.setError("Confirma la nueva contraseña");

            if (primerCampoConError == null) {
                primerCampoConError = confirmPasswordEditText;
            }

        } else if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Las contraseñas no coinciden");

            if (primerCampoConError == null) {
                primerCampoConError = confirmPasswordEditText;
            }
        }

        // Si hay algún error, enfocamos el primer campo incorrecto y detenemos el proceso
        if (primerCampoConError != null) {
            primerCampoConError.requestFocus();
            return;
        }

        // Creamos el objeto que se enviará al backend
        ResetPasswordRequest request = new ResetPasswordRequest(email, username, newPassword);

        // Llamada al endpoint del backend para actualizar la contraseña
        apiService.resetPassword(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    // Si la contraseña se actualiza correctamente, volvemos al login
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Contraseña actualizada correctamente",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else if (response.code() == 400) {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "Revisa los campos obligatorios",
                            Toast.LENGTH_LONG
                    ).show();

                } else if (response.code() == 404) {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "No existe una cuenta con ese email y usuario",
                            Toast.LENGTH_LONG
                    ).show();

                } else if (response.code() == 403) {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "No tienes permiso para realizar esta acción",
                            Toast.LENGTH_LONG
                    ).show();

                } else {
                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "No se pudo actualizar la contraseña. Código: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Error de conexión con el servidor
                Toast.makeText(
                        ForgotPasswordActivity.this,
                        "No se pudo conectar con el servidor",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}