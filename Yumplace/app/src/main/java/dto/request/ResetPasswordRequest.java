package dto.request;

public class ResetPasswordRequest {

    private String email;
    private String username;
    private String newPassword;

    public ResetPasswordRequest(String email, String username, String newPassword) {
        this.email = email;
        this.username = username;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getNewPassword() {
        return newPassword;
    }
}