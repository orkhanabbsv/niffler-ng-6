package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface UsersApi {
    @GET("/internal/users/current")
    Call<UserJson> currentUser(@Query("username") String username);

    @GET("/internal/users/all")
    Call<List<UserJson>> allUsers(@Query("username") String username);

    @POST("/internal/users/update")
    Call<UserJson> updateUserInfo(@Body UserJson user);


    @POST("/internal/invitations/send")
    Call<UserJson> sendInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

    @POST("/internal/invitations/accept")
    Call<UserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);
}

