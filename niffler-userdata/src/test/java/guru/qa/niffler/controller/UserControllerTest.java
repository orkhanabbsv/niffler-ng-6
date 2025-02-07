package guru.qa.niffler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository usersRepository;

    @Sql(scripts = "/currentUserShouldBeReturned.sql")
    @Test
    void currentUserShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"))
                .andExpect(jsonPath("$.fullname").value("Dmitrii Tuchs"))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.photo").isNotEmpty())
                .andExpect(jsonPath("$.photoSmall").isNotEmpty());
    }

    @Test
    void allUsersEndpoint() throws Exception {
        usersRepository.deleteAll();
        UserEntity userDataEntity = getRandomUserEntity();

        UserEntity user1DataEntity = getRandomUserEntity();

        UserEntity user2DataEntity = getRandomUserEntity();

        mockMvc.perform(get("/internal/users/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", userDataEntity.getUsername())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder(user1DataEntity.getUsername(),
                        user2DataEntity.getUsername())));
    }

    @Test
    void updateUserEndpoint() throws Exception {
        UserEntity userDataEntity = getRandomUserEntity();

        UserJson updatedUser = new UserJson(
                userDataEntity.getId(),
                userDataEntity.getUsername(),
                null,
                null,
                "Sasha Rasulov",
                CurrencyValues.USD,
                null,
                null,
                null
        );

        final String contentBody = new ObjectMapper().writeValueAsString(updatedUser);


        mockMvc.perform(post("/internal/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUser.username()))
                .andExpect(jsonPath("$.currency").value(updatedUser.currency().name()))
                .andExpect(jsonPath("$.fullname").value(updatedUser.fullname()));
    }

    private UserEntity getRandomUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("user-" + new Random().nextInt());
        userEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userEntity);
        return userEntity;
    }
}