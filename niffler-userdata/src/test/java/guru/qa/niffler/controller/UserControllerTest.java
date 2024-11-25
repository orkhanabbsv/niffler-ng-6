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
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository usersRepository;

  @Test
  void currentUserEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("dima");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(userDataEntity);

    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"));
  }

  @Test
  void allUsersEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("dima");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(userDataEntity);

    UserEntity user1DataEntity = new UserEntity();
    user1DataEntity.setUsername("sasha");
    user1DataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(user1DataEntity);

    UserEntity user2DataEntity = new UserEntity();
    user2DataEntity.setUsername("vadim");
    user2DataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(user2DataEntity);

    mockMvc.perform(get("/internal/users/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "dima")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].username", containsInAnyOrder("sasha", "vadim")));
  }

  @Test
  void updateUserEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("dima");
    userDataEntity.setFullname("Dmitry Kuznetsov");
    userDataEntity.setSurname("Kuznetsov");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    UserEntity save = usersRepository.save(userDataEntity);

    UserJson updatedUser = new UserJson(
            save.getId(),
            save.getUsername(),
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
            .andExpect(jsonPath("$.username").value("dima"))
            .andExpect(jsonPath("$.currency").value("USD"))
            .andExpect(jsonPath("$.fullname").value("Sasha Rasulov"));
  }
}