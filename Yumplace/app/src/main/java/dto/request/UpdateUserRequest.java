package dto.request;

public class UpdateUserRequest {
    private String username;
    private String profilePhoto;
    private String telephone;
    private String biography;

    public UpdateUserRequest(String username, String profilePhoto, String telephone, String biography) {
        this.username = username;
        this.profilePhoto = profilePhoto;
        this.telephone = telephone;
        this.biography = biography;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
