package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.service.UsersRestClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@Disabled
public class JdbcTest {
    private final UsersClient usersDbClient = new UsersDbClient();
    private final SpendClient client = new SpendDbClient();
    private final UsersRestClient usersRestClient = new UsersRestClient();

    @Test
    void createUser() {
        usersRestClient.createUser(randomUsername(), "12345");
    }

    @Test
    void sendInvitationsAndAddFriend() {
        String username = randomUsername();
        System.out.println(username);
        UserJson user = usersRestClient.createUser(username, "12345");

        usersRestClient.addFriend(user, 1);
    }

    @Test
    void sendInvitations() {
        String username = randomUsername();
        System.out.println(username);
        UserJson user = usersRestClient.createUser(username, "12345");

        usersRestClient.createIncomeInvitation(user, 1);
        usersRestClient.createOutcomeInvitation(user, 1);
    }

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                randomCategoryName(),
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "duck"
                )
        );

        SpendJson spendJson = spendDbClient.updateSpend(
                new SpendJson(
                        spend.id(),
                        new Date(),
                        new CategoryJson(
                                spend.category().id(),
                                randomCategoryName(),
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx-1",
                        "duck"
                )
        );

        Optional<SpendJson> spendById = spendDbClient.findSpendById(spendJson.id());
        spendById.ifPresent(System.out::println);
        spendDbClient.findSpendByUsernameAndSpendDescription(spendJson.username(), spendJson.description());
        System.out.println(spend);

        spendDbClient.deleteSpend(spendJson);
    }

    @Test
    void createUserAndSendInvitation() {
        String username = randomUsername();
        UserJson userJson = usersDbClient.createUser(username, "12345");

        usersDbClient.createIncomeInvitation(userJson, 3);
        usersDbClient.createOutcomeInvitation(userJson, 4);

        System.out.println("==================");
        usersDbClient.addFriend(userJson, 2);
    }

    @Test
    void deleteUser() {
        String username = randomUsername();
        UserJson userJson = usersDbClient.createUser(username, "12345");

        usersDbClient.deleteUser(userJson);
    }

    @Test
    void findByIdAndUsername() {
        String username = randomUsername();
        UserJson userJson = usersDbClient.createUser(username, "12345");
        Optional<UserJson> byUsername = usersDbClient.findByUsername(username);
        byUsername.ifPresent(System.out::println);
        System.out.println("-----------------------");
        Optional<UserJson> byId = usersDbClient.findById(userJson.id());
        byUsername.ifPresent(System.out::println);
    }

    @Test
    void category() {
        CategoryJson categoryJson = client.createCategory(
                new CategoryJson(
                        null,
                        "name-category-2222",
                        "duck",
                        true
                )
        );

        Optional<CategoryJson> categoryById = client.findCategoryById(categoryJson.id());
        categoryById.ifPresent(System.out::println);
        Optional<CategoryJson> categoryByUsernameAndSpendName = client.findCategoryByUsernameAndSpendName(categoryJson.username(), categoryJson.name());
        categoryByUsernameAndSpendName.ifPresent(System.out::println);

        client.deleteCategory(categoryJson);
    }
}
