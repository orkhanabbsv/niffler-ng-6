package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.GatewayApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GatewayApiClient extends RestClient {

  private final GatewayApi gatewayApi;

  public GatewayApiClient() {
    super(CFG.gatewayUrl());
    this.gatewayApi = create(GatewayApi.class);
  }

  @Step("send /api/friends/all GET request to niffler-gateway")
  public List<UserJson> allFriends(@Nonnull String bearerToken,
                                   @Nullable String searchQuery) {
    final Response<List<UserJson>> response;
    try {
      response = gatewayApi.allFriends(bearerToken, searchQuery).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Step("send /api/friends/remove DELETE request to niffler-gateway")
  public void removeFriend(@Nonnull String bearerToken, @Nonnull String friendUsername) {
    final Response<Void> response;
    try {
      response = gatewayApi.removeFriend(bearerToken, friendUsername).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  @Step("send api/invitations/accept POST request to niffler-gateway")
  public void acceptInvitation(@Nonnull String bearerToken, @Nonnull String username) {
    final Response<UserJson> response;
    final FriendJson targetFriend = new FriendJson(username);
    try {
      response = gatewayApi.acceptInvitation(bearerToken, targetFriend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  @Step("send api/invitations/decline POST request to niffler-gateway")
  public void declineInvitation(@Nonnull String bearerToken, @Nonnull String username) {
    final Response<UserJson> response;
    final FriendJson targetFriend = new FriendJson(username);
    try {
      response = gatewayApi.declineInvitation(bearerToken, targetFriend).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }
}
