package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GhApiClient extends RestClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final GhApi ghApi;

  public GhApiClient() {
    super(CFG.ghUrl());
    ghApi = create(GhApi.class);
  }

  public @NonNull String issueState(@NonNull String issueNumber) {
    final Response<JsonNode> response;
    try {
      response = ghApi.issue(
              "Bearer " + System.getenv(GH_TOKEN_ENV),
              issueNumber
      ).execute();
    } catch (IOException e) {
        throw new AssertionError();
    }
    assertEquals(200, response.code());
    return Objects.requireNonNull(response.body()).get("state").asText();
  }
}
