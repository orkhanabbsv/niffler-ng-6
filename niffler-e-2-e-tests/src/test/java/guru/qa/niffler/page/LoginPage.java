package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement registerButton = $("a[href='/register']");
  private final SelenideElement errorContainer = $(".form__error");

  @Step("Перейти на страницу регистрации")
  public RegisterPage doRegister() {
    registerButton.click();
    return new RegisterPage();
  }

  @Step("Успешная авторизация с пользователем: {username}")
  public MainPage successLogin(String username, String password) {
    login(username, password);
    return new MainPage();
  }

  public void login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
  }

  @Step("Отображения ошибки: {error}")
  public LoginPage checkError(String error) {
    errorContainer.shouldHave(text(error));
    return this;
  }
}
