package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

/**
 * LoginActivity es la pantalla de inicio de sesión de la aplicación Yumplace.
 * <p>
 * Permite al usuario ingresar su email y contraseña para acceder a la aplicación.
 * También proporciona un enlace para navegar a la pantalla de registro.
 * </p>
 * <p>
 * Contiene referencias a los elementos de la interfaz:
 * - EditText para email y contraseña
 * - Button para iniciar sesión
 * - TextView para navegar al registro
 * </p>
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    /**
     * Método llamado al crear la actividad.
     * <p>
     * Inicializa los elementos de la interfaz de usuario y configura los listeners:
     * - El botón de login valida que los campos no estén vacíos y muestra un Toast.
     * - El TextView de registro inicia la actividad RegisterActivity.
     * </p>
     *
     * @param savedInstanceState Si la actividad se está recreando, contiene el estado previo.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}

