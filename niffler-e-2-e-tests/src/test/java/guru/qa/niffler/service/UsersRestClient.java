package guru.qa.niffler.service;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.RegistrationApiClient;
import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@ParametersAreNonnullByDefault
public class UsersRestClient implements UsersClient {

    private final RegistrationApiClient registrationApi = new RegistrationApiClient();
    private final UsersApiClient usersApi = new UsersApiClient();

    @Step("Создать пользователя с данными username: {username} и паролем: {password}")
    @Override
    public UserJson createUser(String username, String password) {
        registrationApi.registerUser(username, password);

        long maxWaitTime = 5_000L;
        Stopwatch sw = Stopwatch.createStarted();

        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            try {
                UserJson userJson = usersApi.currentUser(username);
                if (userJson != null && userJson.id() != null) {
                    return userJson;
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Ошибка при выполнении запроса на получение пользователя или ожидании", e);
            }
        }
        throw new AssertionError("Созданный пользователь не был найден");
    }

    @Step("Найти пользователя по id: {id}")
    @Override
    public Optional<UserJson> findById(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Step("Найти пользователя по имени: {username}")
    @Override
    public Optional<UserJson> findByUsername(String username) {
        return Optional.ofNullable(usersApi.allUsers(username).getFirst());
    }

    @Step("Создать приглашения в количестве: {count}")
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

    @Step("Создать приглашения в количестве: {count}")
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

    @Step("Добавить друзей в количестве: {count}")
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

    @Step("Удалить пользоветеля")
    @Override
    public void deleteUser(UserJson user) {
        throw new UnsupportedOperationException();
    }
}
