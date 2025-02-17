package guru.qa.niffler.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.equalToMessage;
import static org.wiremock.grpc.dsl.WireMockGrpc.message;
import static org.wiremock.grpc.dsl.WireMockGrpc.method;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CurrencyControllerTest {
    private final WireMockServer wiremock = new WireMockServer(
            new WireMockConfiguration()
                    .port(8093)
                    .withRootDirectory("src/test/resources/wiremock")
                    .extensions(new GrpcExtensionFactory())
    );

    private final WireMockGrpcService mockGrpcService =
            new WireMockGrpcService(
                    new WireMock(wiremock.port()),
                    "guru.qa.niffler.grpc.NifflerCurrencyService"
            );

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void startWireMock() {
        wiremock.start();
    }

    @AfterEach
    void stopWireMock() {
        wiremock.stop();
    }

    @Test
    void getAllCurrencies() throws Exception {
        mockGrpcService.stubFor(
                method("GetAllCurrencies")
                        .withRequestMessage(equalToMessage(Empty.getDefaultInstance()))
                        .willReturn(message(CurrencyResponse.newBuilder()
                                .addAllCurrencies(Currency.newBuilder().setCurrency(CurrencyValues.RUB)
                                        .setCurrencyRate(1.0)
                                        .build())
                                .build()))

        );

        mockMvc.perform(get("/api/currencies/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].currencyRate").value(1.0));
    }
}