package controlador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.engiri.yumplace.R;

import java.util.ArrayList;
import java.util.List;

import modelo.Post;
import vista.PostAdapter;

public class FeedActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PostAdapter adapter;
    List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        recyclerView = findViewById(R.id.recyclerPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postList.add(new Post(
                R.drawable.user,
                "chef_carlos",
                "hace 2 horas",
                R.drawable.pasta,
                342,
                28
        ));

        adapter = new PostAdapter(this, postList);
        recyclerView.setAdapter(adapter);
    }
}
