package guru.qa.niffler.test.gql.data;

import org.junit.jupiter.params.provider.Arguments;
import qa.guru.type.CurrencyValues;
import qa.guru.type.FilterPeriod;

import java.util.stream.Stream;

public class Data {

    public static Stream<Arguments> filteredData() {
        return Stream.of(
                Arguments.of(CurrencyValues.RUB, null, null , 400.0, 1, CurrencyValues.RUB),
                Arguments.of(null, CurrencyValues.EUR, null , 148.15, 3, CurrencyValues.EUR),
                Arguments.of(null, null, FilterPeriod.TODAY, 10666.67, 3, CurrencyValues.RUB)
        );
    }
}
