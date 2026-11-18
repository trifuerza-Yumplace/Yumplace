package dto.response;

public class UserResponse {
    private Integer id;
    private String email;
    private String username;
    private String profilePhoto;
    private String phoneNumber;
    private String biography;
    private String registerDate;

    // Getters
    public Integer getId() { return id; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getProfilePhoto() { return profilePhoto; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBiography() { return biography; }
    public String getRegisterDate() { return registerDate; }
}
