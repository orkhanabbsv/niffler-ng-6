package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @User(
      categories = @Category(
          archived = true
      )
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded();

    Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
        .checkArchivedCategoryExists(user.testData().categories().getFirst().name());
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), user.testData().password())
        .checkThatPageLoaded();

    Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
        .checkCategoryExists(user.testData().categories().getFirst().name());
  }

  @User
  @Test
  void changeName(UserJson user) {
    String name = randomName();

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), user.testData().password())
            .getHeader()
            .toProfilePage()
            .setName(name)
            .checkName(name)
            .submitProfile()
            .checkAlertMessage("Profile successfully updated");
  }
}
