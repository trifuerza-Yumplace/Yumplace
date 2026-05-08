package remote;

import java.util.List;
import java.util.Map;

import dto.request.UpdateUserRequest;
import modelo.Category;
import modelo.Comment;
import modelo.Post;
import dto.request.LoginRequest;
import dto.request.RegisterRequest;
import dto.response.AuthResponse;
import dto.response.UserResponse;
import dto.request.ResetPasswordRequest;
import modelo.RecipeIngredientResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiService {

    // =========================
    // AUTENTICACIÓN
    // =========================

    // Inicia sesión con email y contraseña. Devuelve el token JWT y datos básicos del usuario.
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    // Registra un nuevo usuario en la aplicación.
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    // Restablece la contraseña usando email, nombre de usuario y nueva contraseña.
    @POST("auth/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);


    // =========================
    // POSTS / RECETAS
    // =========================

    // Obtiene todas las publicaciones del feed.
    @GET("posts")
    Call<List<Post>> getAllPosts();

    // Crea una nueva publicación o receta.
    @POST("posts")
    Call<Post> createPost(@Body Map<String, Object> post);

    // Obtiene las publicaciones de un usuario concreto.
    @GET("users/{id}/posts")
    Call<List<Post>> getPostsByUser(@Path("id") Integer userId);

    // Obtiene las publicaciones del usuario que ha iniciado sesión.
    @GET("posts/me")
    Call<List<Post>> getMyPosts();


    // =========================
    // INTERACCIONES
    // =========================

    // Añade un like a una publicación.
    @POST("posts/{id}/like")
    Call<Void> likePost(@Path("id") Integer id);

    // Elimina el like de una publicación.
    @DELETE("posts/{id}/unlike")
    Call<Void> unlikePost(@Path("id") Integer id);

    // Obtiene los comentarios de una publicación.
    @GET("posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id") Integer postId);

    // Añade un comentario a una publicación.
    @POST("posts/{id}/comments")
    Call<Comment> addComment(@Path("id") Integer postId, @Body Comment comment);

    // Obtiene los ingredientes asociados a una publicación.
    @GET("posts/{id}/ingredients")
    Call<List<RecipeIngredientResponse>> getPostIngredients(@Path("id") Integer postId);


    // =========================
    // USUARIOS
    // =========================

    // Obtiene el perfil público de un usuario concreto.
    @GET("users/{id}")
    Call<UserResponse> getUserProfile(@Path("id") Integer id);

    // Obtiene el perfil del usuario que ha iniciado sesión.
    @GET("users/me")
    public Call<UserResponse> getMyProfile();

    // Actualiza los datos del perfil del usuario que ha iniciado sesión.
    @PUT("users/me")
    public Call<UserResponse> updateUser(@Body UpdateUserRequest request);

    // Obtiene la lista de seguidores de un usuario.
    @GET("users/{id}/followers")
    Call<List<UserResponse>> getFollowers(@Path("id") Integer id);

    // Obtiene la lista de usuarios a los que sigue un usuario.
    @GET("users/{id}/following")
    Call<List<UserResponse>> getFollowing(@Path("id") Integer id);

    // Sigue a un usuario.
    @POST("users/{id}/follow")
    Call<Void> followUser(@Path("id") Integer id);

    // Deja de seguir a un usuario.
    @DELETE("users/{id}/unfollow")
    Call<Void> unfollowUser(@Path("id") Integer id);

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    // Eliminar cuenta
    @DELETE("users/me")
    Call<Void> deleteMyAccount();
}