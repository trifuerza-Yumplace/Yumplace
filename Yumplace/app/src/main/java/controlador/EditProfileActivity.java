package controlador;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.engiri.yumplace.R;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView btnBack = findViewById(R.id.btnBackEdit);
        Button btnSave = findViewById(R.id.btnSaveProfile);
        EditText etName = findViewById(R.id.etEditName);
        EditText etBio = findViewById(R.id.etEditBio);

        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString();
            String newBio = etBio.getText().toString();

            if (newName.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Perfil actualizado: " + newName, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}