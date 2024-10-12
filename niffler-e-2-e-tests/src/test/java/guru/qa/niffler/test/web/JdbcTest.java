package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class JdbcTest {
    private final UsersClient usersDbClient = new UsersDbClient();

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-2",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        "duck"
                )
        );

        System.out.println(spend);
    }

    @Test
    void createUserAndSendInvitation() {
        String username = RandomDataUtils.randomUsername();
        UserJson userJson = usersDbClient.createUser(username, "12345");

        usersDbClient.createIncomeInvitation(userJson, 3);
        usersDbClient.createOutcomeInvitation(userJson, 4);

    }
}
