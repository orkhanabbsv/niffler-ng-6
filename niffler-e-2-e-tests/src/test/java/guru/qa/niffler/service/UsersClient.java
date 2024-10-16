package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersClient {
    UserJson createUser(String username, String password);

    Optional<UserJson> findById(UUID id);

    Optional<UserJson> findByUsername(String username);

    List<UserJson> createIncomeInvitation(UserJson requester, int count);

    List<UserJson> createOutcomeInvitation(UserJson requester, int count);

    List<UserJson> addFriend(UserJson requester, int count);

    void deleteUser(UserJson user);
}
