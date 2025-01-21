package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import qa.guru.StatQuery;
import qa.guru.type.CurrencyValues;
import qa.guru.type.FilterPeriod;

import static guru.qa.niffler.model.rest.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatGraphqlTests extends BaseGraphQlTest {

    private static final Config CFG = Config.getInstance();

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            spendings = {
                    @Spending(description = "Food", amount = 100, category = "Test", currency = USD),
                    @Spending(description = "Groceries", amount = 50, category = "NotTest", currency = EUR),
                    @Spending(description = "Clothes", amount = 400, category = "Test4", currency = RUB)
            }
    )
    @ParameterizedTest(name = "Фильтр-{index} => currency={0}, statCurrency={1}, period={2}, expectedTotal={3}")
    @MethodSource("guru.qa.niffler.test.gql.data.Data#filteredData")
    @ApiLogin
    void statTestByFilteredByFilter(CurrencyValues currency,
                                          CurrencyValues statCurrency,
                                          FilterPeriod period,
                                          double expectedTotal,
                                          int expectedSize,
                                          CurrencyValues expectedCurrencyValue,
                                          @Token String bearerToken
    ) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(currency)
                        .statCurrency(statCurrency)
                        .filterPeriod(period)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                expectedTotal,
                result.total
        );

        result.statByCategories.forEach(statByCategory -> {
            assertEquals(expectedCurrencyValue, statByCategory.currency);
        });
        assertEquals(
                expectedSize,
                result.statByCategories.size()
        );
    }
}
