package modelo;

import java.util.List;

public class Post {

    public int profileImage;
    public String username;
    public String time;
    public int postImage;
    public int likes;
    public int comments;

    // 👉 NUEVO
    public boolean isLiked;
    public List<String> ingredients;
    public List<String> steps;
    public Post(int profileImage, String username, String time,
                int postImage, int likes, int comments, List<String> ingredients, List<String> steps) {
        this.profileImage = profileImage;
        this.username = username;
        this.time = time;
        this.postImage = postImage;
        this.likes = likes;
        this.comments = comments;
        this.ingredients = ingredients;
        this.steps = steps;

        // 👉 Por defecto NO está likeado
        this.isLiked = false;
    }
}


