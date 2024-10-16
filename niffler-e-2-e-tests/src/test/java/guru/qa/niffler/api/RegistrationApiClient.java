package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegistrationApiClient {
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().spendUrl())
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

        final String cookieHeader = formResponse.headers().get("Set-Cookie");
        if (cookieHeader == null || !cookieHeader.contains("XSRF-TOKEN")) {
            throw new AssertionError();
        }

        String csrfToken = null;
        for (String cookie : cookieHeader.split(";")) {
            if (cookie.contains("XSRF-TOKEN")) {
                csrfToken = cookie.split("=")[1].trim();
                break;
            }
        }

        if (csrfToken == null) {
            throw new AssertionError();
        }

        final Response<Void> registerResponse;
        try {
            registerResponse = registrationApi.register(username, password, password, csrfToken).execute();
        } catch (IOException e) {
            throw new AssertionError();
        }

        assertEquals(201, registerResponse.code());
    }
}
