package controlador;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import modelo.Category;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.engiri.yumplace.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelo.Post;
import modelo.TokenManager;
import remote.ApiService;
import remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicPostActivity extends AppCompatActivity {

    private LinearLayout containerIngredients, containerSteps;
    private TextView btnAddIngredient, btnAddStep;
    private EditText etTitle, etTime, etPhotoUrl;
    private Spinner spinnerCategories, spinnerTimeUnit;
    private List<Category> categoriesList = new ArrayList<>();
    private Integer selectedCategoryId = null;
    private ImageView ivRecipePhoto;
    private Button btnPublish;
    private ApiService apiService;

    // Variable para guardar la URI de la imagen seleccionada de la galería
    private Uri selectedImageUri;

    // Lanzador para abrir la galería
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    // Limpiamos el campo URL para que no haya confusión
                    etPhotoUrl.setText("");
                    // Cargamos la imagen de la galería en el preview
                    Glide.with(this).load(selectedImageUri).into(ivRecipePhoto);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_post);

        TokenManager tokenManager = new TokenManager(this);
        if (tokenManager.getToken() == null || tokenManager.getToken().isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        apiService = RetrofitClient.getApiService(this);

        // Inicializar Vistas
        containerIngredients = findViewById(R.id.containerIngredients);
        containerSteps = findViewById(R.id.containerSteps);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnAddStep = findViewById(R.id.btnAddStep);
        etTitle = findViewById(R.id.etTitle);
        etTime = findViewById(R.id.etTime);
        spinnerCategories = findViewById(R.id.spinnerCategories);
        spinnerTimeUnit = findViewById(R.id.spinnerTimeUnit);
        etPhotoUrl = findViewById(R.id.etPhotoUrl);
        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        btnPublish = findViewById(R.id.btnPublish);

        setupTimeUnitSpinner();
        loadCategories();

        // ================= SELECCIONAR DESDE GALERÍA =================
        // Al tocar la imagen o el layout de la foto, abrimos galería
        findViewById(R.id.layoutPhoto).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // ================= PREVIEW DESDE URL =================
        etPhotoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String url = s.toString().trim();
                if (url.startsWith("http")) {
                    selectedImageUri = null; // Priorizamos la URL si el usuario escribe una
                    Glide.with(PublicPostActivity.this)
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .into(ivRecipePhoto);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        addIngredient();
        addStep();

        btnAddIngredient.setOnClickListener(v -> addIngredient());
        btnAddStep.setOnClickListener(v -> addStep());
        btnPublish.setOnClickListener(v -> publicarPost());
    }

    private void setupTimeUnitSpinner() {

        String[] timeUnits = {"minutos", "horas"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                timeUnits
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTimeUnit.setAdapter(adapter);
    }

    private void loadCategories() {

        apiService.getAllCategories().enqueue(new Callback<List<Category>>() {

            @Override
            public void onResponse(Call<List<Category>> call,
                                   Response<List<Category>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    categoriesList = response.body();

                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("Selecciona categoría");

                    for (Category category : categoriesList) {
                        categoryNames.add(category.getCategoryName());
                    }

                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(
                                    PublicPostActivity.this,
                                    android.R.layout.simple_spinner_item,
                                    categoryNames
                            );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spinnerCategories.setAdapter(adapter);

                    spinnerCategories.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(AdapterView<?> parent,
                                                           View view,
                                                           int position,
                                                           long id) {

                                    if (position == 0) {
                                        selectedCategoryId = null;
                                    } else {
                                        selectedCategoryId =
                                                categoriesList.get(position - 1).getIdCategory();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(
                        PublicPostActivity.this,
                        "Error cargando categorías",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    // ================= INGREDIENTES Y PASOS (Igual que antes) =================
    private void addIngredient() {
        View view = LayoutInflater.from(this).inflate(R.layout.item_ingredient, containerIngredients, false);
        EditText et = view.findViewById(R.id.etIngredient);
        view.findViewById(R.id.btnDeleteIngredient).setOnClickListener(v -> {
            containerIngredients.removeView(view);
            renumberIngredients();
        });
        containerIngredients.addView(view);
        renumberIngredients();
    }

    private void renumberIngredients() {
        for (int i = 0; i < containerIngredients.getChildCount(); i++) {
            EditText et = containerIngredients.getChildAt(i).findViewById(R.id.etIngredient);
            et.setHint("Ingrediente " + (i + 1));
        }
    }

    private void addStep() {
        View view = LayoutInflater.from(this).inflate(R.layout.item_paso, containerSteps, false);
        view.findViewById(R.id.btnDeleteStep).setOnClickListener(v -> {
            containerSteps.removeView(view);
            renumberSteps();
        });
        containerSteps.addView(view);
        renumberSteps();
    }

    private void renumberSteps() {
        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            View v = containerSteps.getChildAt(i);
            ((TextView) v.findViewById(R.id.tvStepNumber)).setText((i + 1) + ".");
            ((EditText) v.findViewById(R.id.etStep)).setHint("Paso " + (i + 1));
        }
    }

    // ================= PUBLICAR CON VALIDACIONES ACTUALIZADAS =================
    private void publicarPost() {
        String titulo = etTitle.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String photoUrl = etPhotoUrl.getText().toString().trim();

        // 1. VALIDAR TÍTULO
        if (titulo.isEmpty()) {
            etTitle.setError("El título es obligatorio");
            return;
        }

        // 2. VALIDAR TIEMPO
        if (time.isEmpty()) {
            etTime.setError("El tiempo es obligatorio");
            return;
        }

        String selectedUnit = spinnerTimeUnit.getSelectedItem().toString();

        int prepTime;

        try {
            prepTime = parsePrepTime(time, selectedUnit);
        } catch (IllegalArgumentException e) {
            etTime.setError(e.getMessage());
            return;
        }

        if (selectedCategoryId == null) {
            Toast.makeText(this,
                    "Selecciona una categoría",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. VALIDAR FOTO (URL o Galería)
        if (photoUrl.isEmpty() && selectedImageUri == null) {
            Toast.makeText(this, "La foto es obligatoria (URL o Galería)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Listas de datos
        List<String> pasos = new ArrayList<>();
        List<String> ingredientes = new ArrayList<>();

        for (int i = 0; i < containerSteps.getChildCount(); i++) {
            String text = ((EditText) containerSteps.getChildAt(i).findViewById(R.id.etStep)).getText().toString().trim();
            if (!text.isEmpty()) pasos.add(text);
        }

        for (int i = 0; i < containerIngredients.getChildCount(); i++) {
            String text = ((EditText) containerIngredients.getChildAt(i).findViewById(R.id.etIngredient)).getText().toString().trim();
            if (!text.isEmpty()) ingredientes.add(text);
        }

        if (ingredientes.isEmpty() || pasos.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ingrediente y un paso", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalPhoto = photoUrl.isEmpty() ? "https://link-a-imagen-subida.com/foto.jpg" : photoUrl;

        Map<String, Object> body = new HashMap<>();
        body.put("title", titulo);
        body.put("description", "");
        body.put("categoryId", selectedCategoryId);
        body.put("photo", finalPhoto);
        body.put("prepTime", prepTime);
        body.put("difficulty", "easy");
        body.put("steps", String.join("\n", pasos));
        body.put("ingredients", ingredientes);

        apiService.createPost(body).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PublicPostActivity.this, "Receta publicada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Toast.makeText(PublicPostActivity.this, "Error conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int parsePrepTime(String timeText, String selectedUnit) {

        if (timeText == null || timeText.trim().isEmpty()) {
            throw new IllegalArgumentException("El tiempo es obligatorio");
        }

        String cleanTime = timeText.trim().replace(",", ".");

        if (selectedUnit.equals("minutos")) {

            if (cleanTime.contains(".")) {
                throw new IllegalArgumentException("En minutos usa un número entero, ej: 70");
            }

            int minutes = Integer.parseInt(cleanTime);

            if (minutes <= 0) {
                throw new IllegalArgumentException("El tiempo debe ser mayor que 0");
            }

            return minutes;
        }

        // Si selecciona horas:
        // 1      -> 60 minutos
        // 1.10   -> 1 h 10 min = 70 minutos
        // 1,30   -> 1 h 30 min = 90 minutos
        if (selectedUnit.equals("horas")) {

            if (cleanTime.contains(".")) {

                String[] parts = cleanTime.split("\\.");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Formato válido: 1,30");
                }

                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);

                if (hours < 0 || minutes < 0) {
                    throw new IllegalArgumentException("El tiempo debe ser mayor que 0");
                }

                if (minutes >= 60) {
                    throw new IllegalArgumentException("Los minutos deben ser menores de 60");
                }

                int totalMinutes = (hours * 60) + minutes;

                if (totalMinutes <= 0) {
                    throw new IllegalArgumentException("El tiempo debe ser mayor que 0");
                }

                return totalMinutes;

            } else {

                int hours = Integer.parseInt(cleanTime);

                if (hours <= 0) {
                    throw new IllegalArgumentException("El tiempo debe ser mayor que 0");
                }

                return hours * 60;
            }
        }

        throw new IllegalArgumentException("Unidad de tiempo no válida");
    }
}