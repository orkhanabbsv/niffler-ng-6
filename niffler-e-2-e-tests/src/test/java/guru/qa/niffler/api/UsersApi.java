package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface UsersApi {
    @GET("/current")
    Call<UserJson> currentUser(@Query("username") String username);

    @GET("/all")
    Call<List<UserJson>> allUsers(@Query("username") String username);

    @POST("/update")
    Call<UserJson> updateUserInfo(@Body UserJson user);


    @POST("/send")
    Call<UserJson> sendInvitation(@Query("username") String username,
                                  @Query("targetUsername") String targetUsername);

    @POST("/accept")
    Call<UserJson> acceptInvitation(@Query("username") String username,
                                    @Query("targetUsername") String targetUsername);
}

