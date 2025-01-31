package guru.qa.niffler.api;

import guru.qa.niffler.userdata.wsdl.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserdataSoapApi {

  @Headers(
          value = {
                  "Content-Type: text/xml",
                  "Accept-Charset: utf-8",
          }
  )
  @POST("ws")
  Call<UserResponse> currentUser(@Body CurrentUserRequest currentUser);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UsersResponse> getFriends(@Body FriendsPageRequest friends);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest removeFriend);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> sendInvitation(@Body SendInvitationRequest sendInvitation);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> acceptInvitation(@Body AcceptInvitationRequest sendInvitation);

    @Headers(
            value = {
                    "Content-Type: text/xml",
                    "Accept-Charset: utf-8",
            }
    )
    @POST("ws")
    Call<UserResponse> declineInvitation(@Body DeclineInvitationRequest sendInvitation);
}
