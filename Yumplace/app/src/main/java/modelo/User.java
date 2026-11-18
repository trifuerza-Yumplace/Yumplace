package modelo;

import com.google.gson.annotations.SerializedName;

public class User {

    private int id;
    private String username;

    @SerializedName("profilePhoto")
    private String photo;

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }
}