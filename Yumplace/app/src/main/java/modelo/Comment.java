package modelo;

public class Comment {

    private int id;
    private String text;
    private User user;

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }
    public void setText(String text) {
        this.text = text;
    }
}