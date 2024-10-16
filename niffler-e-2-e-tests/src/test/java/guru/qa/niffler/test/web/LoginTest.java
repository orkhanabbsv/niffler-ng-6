package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @User(
          categories = {
                  @Category(name = "CategoryName-74", archived = true),
                  @Category(name = "CategoryName-85", archived = false)
          },
          spendings = {
                  @Spending(
                          category = "CategoryName-98",
                          description = "test-category",
                          amount = 100
                  )
          }
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson userJson) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(userJson.username(), userJson.testData().password())
        .checkThatPageLoaded();
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    LoginPage loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
    loginPage.login(randomUsername(), "BAD");
    loginPage.checkError("Bad credentials");
  }
}
