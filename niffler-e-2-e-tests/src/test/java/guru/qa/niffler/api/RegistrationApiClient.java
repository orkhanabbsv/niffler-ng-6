package guru.qa.niffler.api;

import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class RegistrationApiClient extends RestClient {
    private final RegistrationApi registrationApi;

    public RegistrationApiClient() {
        super(CFG.authUrl());
        registrationApi = create(RegistrationApi.class);
    }

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
            registerResponse = registrationApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
        } catch (IOException e) {
            throw new AssertionError();
        }
        assertEquals(201, registerResponse.code());
    }
}
