package remote;

import java.io.IOException;

import modelo.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 1. Obtenemos la petición original
        Request originalRequest = chain.request();

        // 2. Recuperamos el token guardado en el móvil
        String token = tokenManager.getToken();

        // 3. Si tenemos token, lo añadimos a la cabecera "Authorization"
        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }

        // 4. Si no hay token, la petición sigue su curso normal
        return chain.proceed(originalRequest);
    }
}
