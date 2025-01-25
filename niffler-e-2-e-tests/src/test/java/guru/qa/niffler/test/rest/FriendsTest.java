package guru.qa.niffler.test.rest;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendshipStatus;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RestTest
public class FriendsTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

  @User(friends = 2, incomeInvitations = 1, outcomeInvitations = 1)
  @ApiLogin
  @Test
  void allFriendsAndIncomeInvitationsShouldBeReturnedFroUser(UserJson user, @Token String token) {
    final List<UserJson> expectedFriends = user.testData().friends();
    final List<UserJson> expectedInvitations = user.testData().incomeInvitations();

    final List<UserJson> result = gatewayApiClient.allFriends(
        token,
        null
    );

    Assertions.assertNotNull(result);
    assertEquals(3, result.size());

    final List<UserJson> friendsFromResponse = result.stream().filter(
        u -> u.friendshipStatus() == FriendshipStatus.FRIEND
    ).toList();

    final List<UserJson> invitationsFromResponse = result.stream().filter(
        u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    assertEquals(2, friendsFromResponse.size());
    assertEquals(1, invitationsFromResponse.size());

    assertEquals(
        expectedInvitations.getFirst().username(),
        invitationsFromResponse.getFirst().username()
    );

    final UserJson firstUserFromRequest = friendsFromResponse.getFirst();
    final UserJson secondUserFromRequest = friendsFromResponse.getLast();

    assertEquals(
        expectedFriends.getFirst().username(),
        firstUserFromRequest.username()
    );

    assertEquals(
        expectedFriends.getLast().username(),
        secondUserFromRequest.username()
    );
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void deleteFriend(UserJson user, @Token String token) {
    final List<UserJson> expectedFriends = user.testData().friends();

    gatewayApiClient.removeFriend(token, expectedFriends.getFirst().username());
    List<UserJson> userJsons = gatewayApiClient.allFriends(token, null);

    assertTrue(userJsons.isEmpty());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void acceptFriendInvitation(UserJson user, @Token String token) {
    final List<UserJson> expectedInvitation = user.testData().incomeInvitations();

    gatewayApiClient.acceptInvitation(token, expectedInvitation.getFirst().username());
    List<UserJson> result = gatewayApiClient.allFriends(token, null);

    final List<UserJson> friendsFromResponse = result.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.FRIEND
    ).toList();

    final List<UserJson> incomeInvitationFromResponse = result.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();
    String expectedUsername = user.testData().incomeInvitations().getLast().username();

    assertEquals(1, friendsFromResponse.size());
    assertEquals(0, incomeInvitationFromResponse.size());
    assertEquals(expectedUsername, friendsFromResponse.getFirst().username());
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void declineFriendInvitation(UserJson user, @Token String token) {
    final List<UserJson> expectedInvitation = user.testData().incomeInvitations();

    gatewayApiClient.declineInvitation(token, expectedInvitation.getFirst().username());
    List<UserJson> result = gatewayApiClient.allFriends(token, null);

    final List<UserJson> friendsFromResponse = result.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.FRIEND
    ).toList();

    final List<UserJson> incomeInvitationFromResponse = result.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    assertEquals(0, friendsFromResponse.size());
    assertEquals(0, incomeInvitationFromResponse.size());
  }

  @User(outcomeInvitations = 1)
  @ApiLogin
  @Test
  void receiveInvitation(UserJson user, @Token String token) {
    final String expectedUsername = user.testData().outcomeInvitations().getLast().username();
    final String expectedUserPassword = "12345";

    final AuthApiClient authApiClient = new AuthApiClient();
    ThreadSafeCookieStore.INSTANCE.removeAll();
    String expectedUserToken = "Bearer " + authApiClient.login(expectedUsername, expectedUserPassword);

    List<UserJson> friendResult = gatewayApiClient.allFriends(expectedUserToken, null);
    final List<UserJson> incomeInvitationFromResponse = friendResult.stream().filter(
            u -> u.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED
    ).toList();

    assertEquals(1, incomeInvitationFromResponse.size());
    assertEquals(user.username(), incomeInvitationFromResponse.getFirst().username());
  }
}
