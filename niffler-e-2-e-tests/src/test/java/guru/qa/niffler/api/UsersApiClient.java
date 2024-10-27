package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient {
    private final UsersApi usersApi;

    public UsersApiClient() {
        super(CFG.userdataUrl());
        usersApi = create(UsersApi.class);
    }

    public @Nullable UserJson currentUser(String username) {
        final Response<UserJson> response;
        try {
            response = usersApi.currentUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public List<UserJson> allUsers(String username) {
        final Response<List<UserJson>> response;
        try {
            response = usersApi.allUsers(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    public @Nullable UserJson updateUserInfo(UserJson user) {
        final Response<UserJson> response;
        try {
            response = usersApi.updateUserInfo(user)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public @Nullable UserJson sendInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = usersApi.sendInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    public @Nullable UserJson acceptInvitation(String username, String targetUsername) {
        final Response<UserJson> response;
        try {
            response = usersApi.acceptInvitation(username, targetUsername)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}

