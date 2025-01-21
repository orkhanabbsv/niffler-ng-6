package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;
import qa.guru.AllPeopleAndFriendsQuery;

import static com.apollographql.apollo.api.Optional.present;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CategoriesGraphQlTests extends BaseGraphQlTest {

    @Test
    @ApiLogin
    @User
    void shouldNotAllowShowCategoriesForDifferentUser(@Token String bearerToken) {
        ApolloCall<AllPeopleAndFriendsQuery.Data> userBySearchQuery = apolloClient.query(
                        new AllPeopleAndFriendsQuery(0, 1, null, present("duck")))
                .addHttpHeader("authorization", bearerToken);
        ApolloResponse<AllPeopleAndFriendsQuery.Data> response = Rx2Apollo.single(userBySearchQuery).blockingGet();
        assertNotNull(response.errors);
        assertEquals("Can`t query categories for another user", response.errors.getFirst().getMessage());
    }
}
