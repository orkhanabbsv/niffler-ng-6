package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;
import qa.guru.FriendsOfFriendsQuery;

import static guru.qa.niffler.model.rest.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FriendsGraphQlTests extends BaseGraphQlTest {

    @Test
    @ApiLogin
    @User(
            spendings = {
                    @Spending(description = "Food", amount = 100, category = "Test", currency = RUB),
                    @Spending(description = "Groceries", amount = 100, category = "NotTest", currency = EUR),
                    @Spending(description = "Clothes", amount = 100, category = "Test4", currency = USD)
            }
    )
    void shouldNotAllowToShowFriends(@Token String bearerToken) {
        ApolloCall<FriendsOfFriendsQuery.Data> userBySearchQuery = apolloClient.query(
                        new FriendsOfFriendsQuery(1, 5))
                .addHttpHeader("authorization", bearerToken);
        ApolloResponse<FriendsOfFriendsQuery.Data> response = Rx2Apollo.single(userBySearchQuery).blockingGet();
        assertNotNull(response.errors);
        assertEquals("Can`t fetch over 1 friends sub-queries", response.errors.getFirst().getMessage());
    }
}
