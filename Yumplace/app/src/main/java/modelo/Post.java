package modelo;

public class Post {

    public int profileImage;
    public String username;
    public String time;
    public int postImage;
    public int likes;
    public int comments;

    // 👉 NUEVO
    public boolean isLiked;

    public Post(int profileImage, String username, String time,
                int postImage, int likes, int comments) {
        this.profileImage = profileImage;
        this.username = username;
        this.time = time;
        this.postImage = postImage;
        this.likes = likes;
        this.comments = comments;

        // 👉 Por defecto NO está likeado
        this.isLiked = false;
    }
}


