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
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("sasha");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userDataEntity);

        UserEntity friendDataEntity = new UserEntity();
        friendDataEntity.setUsername("dima");
        friendDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(friendDataEntity);

        mockMvc.perform(post("/internal/invitations/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "sasha")
                        .param("targetUsername", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"))
                .andExpect(jsonPath("$.friendState").value("INVITE_SENT"));
    }

    @Test
    void acceptInvitation() throws Exception {
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("sasha");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        UserEntity userRequester = usersRepository.save(userDataEntity);

        UserEntity friendDataEntity = new UserEntity();
        friendDataEntity.setUsername("dima");
        friendDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(friendDataEntity);

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setRequester(userRequester);
        friendshipEntity.setAddressee(friendDataEntity);
        friendshipEntity.setStatus(FriendshipStatus.PENDING);
        friendshipEntity.setCreatedDate(new Date(System.currentTimeMillis()));

        friendDataEntity.setFriendshipRequests(List.of(friendshipEntity));
        usersRepository.save(friendDataEntity);

        mockMvc.perform(post("/internal/invitations/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                        .param("targetUsername", "sasha")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sasha"))
                .andExpect(jsonPath("$.friendState").value("FRIEND"));
    }

    @Test
    void declineInvitation() throws Exception {
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("sasha");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        UserEntity userRequester = usersRepository.save(userDataEntity);

        UserEntity friendDataEntity = new UserEntity();
        friendDataEntity.setUsername("dima");
        friendDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(friendDataEntity);

        FriendshipEntity friendshipEntity = new FriendshipEntity();
        friendshipEntity.setRequester(userRequester);
        friendshipEntity.setAddressee(friendDataEntity);
        friendshipEntity.setStatus(FriendshipStatus.PENDING);
        friendshipEntity.setCreatedDate(new Date(System.currentTimeMillis()));

        friendDataEntity.setFriendshipRequests(List.of(friendshipEntity));
        usersRepository.save(friendDataEntity);

        mockMvc.perform(post("/internal/invitations/decline")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                        .param("targetUsername", "sasha")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("sasha"))
                .andExpect(jsonPath("$.friendState").doesNotExist());
    }
}
