package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage extends BasePage<ProfilePage> {

  private final SelenideElement avatar = $("#image__input").parent().$("img");
  private final SelenideElement userName = $("#username");
  private final SelenideElement nameInput = $("#name");
  private final SelenideElement photoInput = $("input[type='file']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement categoryInput = $("input[name='category']");
  private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
  private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
  private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

  @Step("Ввод имени: {name}")
  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  @Step("Загрузка фотографии")
  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Добавление категории: {category}")
  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  @Step("Проверка что категория {category} существует")
  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Проверка что архивная категория {category} существует")
  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Проверка username: {username}")
  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  @Step("Проверка имени: {name}")
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  @Step("Проверка наличия фотографии")
  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  @Step("Проверка что поле категории отключено")
  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }

  @Step("Сохранения изменений в профиле")
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }
}
