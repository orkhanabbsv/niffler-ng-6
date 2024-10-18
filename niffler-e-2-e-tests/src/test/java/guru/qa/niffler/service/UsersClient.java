package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.Optional;
import java.util.UUID;

public interface UsersClient {
    UserJson createUser(String username, String password);

    Optional<UserJson> findById(UUID id);

    Optional<UserJson> findByUsername(String username);

    void createIncomeInvitation(UserJson requester, int count);

    void createOutcomeInvitation(UserJson requester, int count);

    void addFriend(UserJson requester, int count);

    void deleteUser(UserJson user);
}
