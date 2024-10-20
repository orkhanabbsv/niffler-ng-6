package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class RegistrationApiClient {
    private final ThreadSafeCookieJar cookieJar = new ThreadSafeCookieJar();
    public static final String AUTH_URL = Config.getInstance().authUrl();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(AUTH_URL)
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final RegistrationApi registrationApi = retrofit.create(RegistrationApi.class);

    public void registerUser(String username, String password) {
        final Response<Void> formResponse;
        try {
            formResponse = registrationApi.requestRegisterForm().execute();
        } catch (IOException e) {
            throw new AssertionError();
        }

        assertEquals(200, formResponse.code());

        final Response<Void> registerResponse;
        try {
            registerResponse = registrationApi.register(username, password, password, getToken()).execute();
        } catch (IOException e) {
            throw new AssertionError();
        }

        assertEquals(201, registerResponse.code());
    }

    public String getToken() {
        List<Cookie> cookies =
                cookieJar.loadForRequest(Objects.requireNonNull(HttpUrl.parse(AUTH_URL)));

        for (Cookie cookie : cookies) {
            if ("XSRF-TOKEN".equals(cookie.name())) {
                return cookie.value();
            }
        }
        throw new AssertionError("Token not found");
    }
}
