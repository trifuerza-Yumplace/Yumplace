package dto.response;

public class AuthResponse {
    private String token;
    private Integer userId;
    private String username;

    public String getToken() { return token; }
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
}
