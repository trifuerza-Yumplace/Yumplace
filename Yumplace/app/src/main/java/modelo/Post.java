package modelo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {

    private int id;

    private String title;
    private String description;

    @SerializedName("photo")
    private String postImage;

    private Integer prepTime;
    private String difficulty;

    private String steps;

    @SerializedName("publicationDate")
    private String time;

    private User user;

    // ================= BACKEND STATE =================
    private int likes = 0;
    private int comments = 0;

    // 🔥 NUEVO (estado real del backend)
    @SerializedName("likedByUser")
    private boolean likedByUser;

    private List<String> ingredients;

    private Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // ================= CONSTRUCTOR =================
    public Post() {
        // requerido por Gson / Retrofit
    }

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

    public Integer getPrepTime() {
        return prepTime;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public User getUser() {
        return user;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    // ================= ❤️ LIKE STATE (PRO FIX) =================

    // 👉 ESTE es el que usa la app
    public boolean isLiked() {
        return likedByUser;
    }

    public void setLiked(boolean liked) {
        this.likedByUser = liked;
    }

    public boolean getLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean likedByUser) {
        this.likedByUser = likedByUser;
    }

    // ================= SETTERS =================

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setPhoto(String photo) {
        this.postImage = photo;
    }

    public void setPrepTime(Integer prepTime) {
        this.prepTime = prepTime;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}