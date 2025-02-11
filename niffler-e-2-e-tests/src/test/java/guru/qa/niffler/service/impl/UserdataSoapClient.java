package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.userdata.wsdl.*;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        userdataSoapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Crete user using SOAP API")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
        return requireNonNull(userdataSoapApi.currentUser(request).execute().body());
    }

    @NotNull
    @Step("Get list of friends using SOAP API")
    public UsersResponse listOfFriends(FriendsPageRequest friendsPageRequest) {
        try {
            return userdataSoapApi.getFriends(friendsPageRequest).execute().body();
        } catch (IOException e) {
            throw new RuntimeException("Error while getting list of friends", e);
        }
    }

    @Step("Remove friend using SOAP API")
    public void removeFriend(RemoveFriendRequest removeFriendRequest) {
        try {
            userdataSoapApi.removeFriend(removeFriendRequest).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Sending invitation using SOAP API")
    public UserResponse sendInvitation(SendInvitationRequest request) {
        try {
            return userdataSoapApi.sendInvitation(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Accept invitation using SOAP API")
    public UserResponse acceptInvitation(AcceptInvitationRequest request) {
        try {
            return userdataSoapApi.acceptInvitation(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Decline invitation using SOAP API")
    public UserResponse declineInvitation(DeclineInvitationRequest request) {
        try {
            return userdataSoapApi.declineInvitation(request).execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
