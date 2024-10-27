package guru.qa.niffler.api;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient {
  private final SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    spendApi = create(SpendApi.class);
  }

  public @Nullable SpendJson createSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return response.body();
  }

  public @Nullable SpendJson editSpend(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nullable SpendJson getSpend(String id) {
    final Response<SpendJson> response;
    try {
      response = spendApi.getSpend(id)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<SpendJson> allSpends(String username,
                                   @Nullable CurrencyValues currency,
                                   @Nullable String from,
                                   @Nullable String to) {
    final Response<List<SpendJson>> response;
    try {
      response = spendApi.allSpends(username, currency, from, to)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
            ? response.body()
            : Collections.emptyList();
  }

  public void removeSpends(String username, String... ids) {
    final Response<Void> response;
    try {
      response = spendApi.removeSpends(username, Arrays.stream(ids).toList())
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  public @Nullable CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public @Nullable CategoryJson updateCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.updateCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  public List<CategoryJson> allCategory(String username) {
    final Response<List<CategoryJson>> response;
    try {
      response = spendApi.allCategories(username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null
            ? response.body()
            : Collections.emptyList();
  }
}
