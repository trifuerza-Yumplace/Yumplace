package controlador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.engiri.yumplace.R;

public class RegisterActivity extends AppCompatActivity {

    private TextView tvBack;
    private Button btnRegister;
    private EditText etName, etUsername, etEmail, etPassword, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        // Volver atrás
        tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());

        // Obtener referencias a los campos del formulario
        etName = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(0);
        etUsername = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(1);
        etEmail = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(2);
        etPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(3);
        etConfirmPassword = (EditText) ((android.widget.LinearLayout) findViewById(R.id.formContainer)).getChildAt(4);

        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {

            // Obtener textos de los campos
            String name = etName.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // Validar que ningún campo esté vacío
            if (name.isEmpty() || username.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirmPassword.isEmpty()) {

                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_SHORT).show();
                return; // No hacer nada más
            }

            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Registro exitoso → volver a Login
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
