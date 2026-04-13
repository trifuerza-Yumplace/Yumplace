package controlador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import modelo.Post;
import vista.ProfileGridAdapter;

public class OtherProfileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProfileGridAdapter adapter;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);

        recyclerView = findViewById(R.id.recyclerGrid);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Datos fake (solo visual)
        postList = new ArrayList<>();
        postList.add(new Post(R.drawable.user, "chef", "", R.drawable.pasta, 0, 0, null, null));
        postList.add(new Post(R.drawable.user, "chef", "", R.drawable.pasta, 0, 0, null, null));

        adapter = new ProfileGridAdapter(this, postList);
        recyclerView.setAdapter(adapter);

        // Bottom nav (igual que tu Feed)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }
}
