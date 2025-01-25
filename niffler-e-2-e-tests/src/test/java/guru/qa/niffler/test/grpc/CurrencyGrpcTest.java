package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.grpc.CurrencyValues.*;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
    }

    @ParameterizedTest
    @MethodSource
    void correctCalculateRateShouldBeReturned(double amount,
                                       CurrencyValues spendCurrency,
                                       CurrencyValues desiredCurrency,
                                       double expectedAmount
    ) {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setDesiredCurrency(spendCurrency)
                .setSpendCurrency(desiredCurrency)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(request);

        Assertions.assertEquals(expectedAmount, response.getCalculatedAmount());
    }

    private static Stream<Arguments> correctCalculateRateShouldBeReturned() {
        return Stream.of(
                Arguments.of(2344553, RUB, KZT, 328237.42),
                Arguments.of(0.01, KZT, RUB, 0.07),
                Arguments.of(2.03, KZT, USD, 966.67),
                Arguments.of(200, USD, KZT, 0.42),
                Arguments.of(52.3, USD, EUR, 56.48),
                Arguments.of(363.34, EUR, USD, 336.43),
                Arguments.of(4.02, EUR, KZT, 0.01),
                Arguments.of(445.44, EUR, RUB, 6.19),
                Arguments.of(264, RUB, RUB, 264.00), // same currency values
                Arguments.of(0.02, EUR, KZT, 0.0) // zero amount (strange behavior)
        );
    }
}
