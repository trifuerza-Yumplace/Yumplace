package remote;

import java.util.List;

import dto.request.UpdateUserRequest;
import modelo.Post;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import dto.response.AuthResponse;
import dto.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ===== AUTENTICACIÓN =====
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // ===== POSTS (RECETAS) =====
    @GET("posts")
    Call<List<Post>> getAllPosts();

    @POST("posts")
    Call<Post> createPost(@Body Post post);

    @GET("posts/user/{userId}")
    Call<List<Post>> getPostsByUser(@Path("userId") Integer userId);

    // ===== INTERACCIONES =====
    @POST("posts/{id}/like")
    Call<Void> likePost(@Path("id") Integer id);

    @GET("posts/{id}/comments")
    Call<List<Object>> getComments(@Path("id") Integer postId); // Ajustar tipo según tu DTO Comment

    // ===== USUARIOS =====
    @GET("users/{id}")
    Call<UserResponse> getUserProfile(@Path("id") Integer id);

    @GET("users/me")
    public Call<UserResponse> getMyProfile();

    @PUT("users/update")
    public Call<UserResponse> updateUser(@Body UpdateUserRequest request);
}