package modelo;

import com.google.gson.annotations.SerializedName;

public class Post {

    private int id;

    private String title;
    private String description;

    @SerializedName("photo")
    private String postImage; // URL de la imagen

    private int prepTime;
    private String difficulty;

    private String steps; // ⚠️ viene como String del backend

    @SerializedName("publicationDate")
    private String time;

    private User user;

    // 🔹 NO viene del backend directamente
    private int likes;
    private int comments;
    private boolean isLiked = false;

    // ================= GETTERS =================

    public int getId() {
        return id;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : "Usuario";
    }

    public String getPostImage() {
        return postImage;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSteps() {
        return steps;
    }

    public String getTime() {
        return time;
    }

    public int getLikes() {
        return likes;
    }

    public int getComments() {
        return comments;
    }

    public boolean isLiked() {
        return isLiked;
    }

    // ================= SETTERS =================

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}