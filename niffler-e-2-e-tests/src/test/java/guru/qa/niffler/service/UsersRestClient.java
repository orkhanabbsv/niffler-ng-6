package guru.qa.niffler.service;

import guru.qa.niffler.api.RegistrationApiClient;
import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.model.UserJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersRestClient implements UsersClient {

    private final RegistrationApiClient registrationApi = new RegistrationApiClient();
    private final UsersApiClient usersApi = new UsersApiClient();

    @Override
    public UserJson createUser(String username, String password) {
        registrationApi.registerUser(username, password);
        return usersApi.currentUser(username);
    }

    @Override
    public Optional<UserJson> findById(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<UserJson> findByUsername(String username) {
        return Optional.ofNullable(usersApi.allUsers(username).getFirst());
    }

    @Override
    public List<UserJson> createIncomeInvitation(UserJson requester, int count) {
        List<UserJson> incomes = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                UserJson addressee = createUser(username, "12345");

                usersApi.sendInvitation(requester.username(), addressee.username());
                incomes.add(addressee);
            }
        }
        return incomes;
    }

    @Override
    public List<UserJson> createOutcomeInvitation(UserJson requester, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                UserJson targetUser = createUser(username, "12345");

                usersApi.sendInvitation(targetUser.username(), requester.username());
                outcomeInvitations.add(targetUser);
            }
        }
        return outcomeInvitations;
    }

    @Override
    public List<UserJson> addFriend(UserJson requester, int count) {
        List<UserJson> addedFriends = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = randomUsername();
                UserJson addressee = createUser(username, "12345");
                usersApi.sendInvitation(requester.username(), addressee.username());
                usersApi.sendInvitation(addressee.username(), requester.username());
                usersApi.acceptInvitation(requester.username(), addressee.username());
                addedFriends.add(addressee);
            }
        }
        return addedFriends;
    }

    @Override
    public void deleteUser(UserJson user) {
        throw new UnsupportedOperationException();
    }
}
