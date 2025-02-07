package guru.qa.niffler.controller;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.FriendshipEntity;
import guru.qa.niffler.data.FriendshipStatus;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.List;
import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InvitationsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository usersRepository;

    @Test
    void sendInvitation() throws Exception {
        UserEntity userDataEntity = getRandomUserEntity();

        UserEntity friendDataEntity = getRandomUserEntity();

        mockMvc.perform(post("/internal/invitations/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", userDataEntity.getUsername())
                        .param("targetUsername", friendDataEntity.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(friendDataEntity.getUsername()))
                .andExpect(jsonPath("$.friendshipStatus").value("INVITE_SENT"));
    }

    @Test
    void acceptInvitation() throws Exception {
        UserEntity userAddressee = getRandomUserEntity();

        UserEntity userRequester = getRandomUserEntity();

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setRequester(userAddressee);
        friendshipEntity.setAddressee(userRequester);
        friendshipEntity.setStatus(FriendshipStatus.PENDING);
        friendshipEntity.setCreatedDate(new Date(System.currentTimeMillis()));

        userRequester.setFriendshipRequests(List.of(friendshipEntity));
        usersRepository.save(userRequester);

        mockMvc.perform(post("/internal/invitations/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", userRequester.getUsername())
                        .param("targetUsername", userAddressee.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userAddressee.getUsername()))
                .andExpect(jsonPath("$.friendshipStatus").value("FRIEND"));
    }

    @Test
    void declineInvitation() throws Exception {
        UserEntity userRequester = getRandomUserEntity();

        UserEntity userAddressee = getRandomUserEntity();

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setRequester(userRequester);
        friendshipEntity.setAddressee(userAddressee);
        friendshipEntity.setStatus(FriendshipStatus.PENDING);
        friendshipEntity.setCreatedDate(new Date(System.currentTimeMillis()));

        userAddressee.setFriendshipRequests(List.of(friendshipEntity));
        usersRepository.save(userAddressee);

        mockMvc.perform(post("/internal/invitations/decline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", userRequester.getUsername())
                        .param("targetUsername", userAddressee.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userAddressee.getUsername()))
                .andExpect(jsonPath("$.friendshipStatus").doesNotExist());
    }

    private UserEntity getRandomUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user" + new Random().nextInt(1000));
        userEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userEntity);
        return userEntity;
    }
}
