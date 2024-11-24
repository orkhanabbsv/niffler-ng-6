package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

  @User(
      categories = @Category(
          archived = true
      )
  )
  @ApiLogin
  @Test
  void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @ApiLogin
  @Test
  void activeCategoryShouldPresentInCategoriesList(UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkCategoryExists(categoryName);
  }

  @User
  @ApiLogin
  @Test
  void shouldUpdateProfileWithAllFieldsSet() {
    final String newName = randomName();

    ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
        .uploadPhotoFromClasspath("img/cat.jpeg")
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    Selenide.refresh();

    profilePage.checkName(newName)
        .checkPhotoExist();
  }

  @User
  @ApiLogin
  @Test
  void shouldUpdateProfileWithOnlyRequiredFields(UserJson user) {
    final String newName = randomName();

    ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    Selenide.refresh();

    profilePage.checkName(newName);
  }

  @User
  @ApiLogin
  @Test
  void shouldAddNewCategory(UserJson user) {
    String newCategory = randomCategoryName();

    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .addCategory(newCategory)
        .checkAlertMessage("You've added new category:")
        .checkCategoryExists(newCategory);
  }

  @User(
      categories = {
          @Category(name = "Food"),
          @Category(name = "Bars"),
          @Category(name = "Clothes"),
          @Category(name = "Friends"),
          @Category(name = "Music"),
          @Category(name = "Sports"),
          @Category(name = "Walks"),
          @Category(name = "Books")
      }
  )
  @ApiLogin
  @Test
  void shouldForbidAddingMoreThat8Categories(UserJson user) {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .checkThatCategoryInputDisabled();
  }
}
