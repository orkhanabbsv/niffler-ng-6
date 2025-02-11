package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import guru.qa.niffler.userdata.wsdl.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SoapTest
class SoapUsersTest {

    private final UserdataSoapClient client = new UserdataSoapClient();

    @Test
    @User
    void currentUserTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse response = client.currentUser(request);

        assertEquals(user.username(),
                response.getUser().getUsername()
        );
    }

    @Test
    @User(
            friends = 1
    )
    void removeFriendTest(UserJson user) throws IOException {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(user.username());
        request.setFriendToBeRemoved(user.testData().friends().getFirst().username());

        client.removeFriend(request);

        CurrentUserRequest currentUserRequest = new CurrentUserRequest();
        currentUserRequest.setUsername(user.username());
        UserResponse userResponse = client.currentUser(currentUserRequest);

        assertEquals(FriendshipStatus.VOID, userResponse.getUser().getFriendshipStatus());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void sendingInvitationTest(UserJson user) {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeRequested(user.testData().incomeInvitations().getFirst().username());

        UserResponse userResponse = client.sendInvitation(request);

        assertEquals(FriendshipStatus.INVITE_SENT, userResponse.getUser().getFriendshipStatus());
    }

    @Test
    @User(
            incomeInvitations = 1
    )
    void acceptInvitationTest(UserJson user) {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(user.username());
        request.setFriendToBeAdded(user.testData().incomeInvitations().getFirst().username());

        UserResponse userResponse = client.acceptInvitation(request);

        assertEquals(FriendshipStatus.FRIEND, userResponse.getUser().getFriendshipStatus());
    }

    @Test
    @User(
            outcomeInvitations = 1
    )
    void declineInvitation(UserJson user) {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(user.username());
        request.setInvitationToBeDeclined(user.testData().outcomeInvitations().getFirst().username());

        UserResponse userResponse = client.declineInvitation(request);

        assertEquals(FriendshipStatus.VOID, userResponse.getUser().getFriendshipStatus());
    }

    @ParameterizedTest
    @User(
            friends = 10
    )
    @CsvSource({
            "0, 8, 8",
            "1, 8, 2",
            "2, 8, 0"
    })
    void getFriendsListByPage(int page, int size, int expectedSize, UserJson user) {
        FriendsPageRequest friendsPageRequest = new FriendsPageRequest();
        friendsPageRequest.setUsername(user.username());

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        friendsPageRequest.setPageInfo(pageInfo);

        UsersResponse userResponse = client.listOfFriends(friendsPageRequest);

        assertEquals(expectedSize, userResponse.getUser().size());
    }

    @Test
    @User(
            friends = 10
    )
    void getFriendsListBySearchQuery(UserJson user) {
        String expectedFriend = user.testData().friends().getFirst().username();

        FriendsPageRequest friendsPageRequest = new FriendsPageRequest();
        friendsPageRequest.setUsername(user.username());
        friendsPageRequest.setSearchQuery(expectedFriend.substring(0,3));

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        friendsPageRequest.setPageInfo(pageInfo);

        UsersResponse userResponse = client.listOfFriends(friendsPageRequest);

        assertEquals(1, userResponse.getUser().size());
        assertEquals(expectedFriend, userResponse.getUser().getFirst().getUsername());
    }
}
