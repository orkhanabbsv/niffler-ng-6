package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage extends BasePage<RegisterPage> {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement proceedLoginButton = $(".form_sign-in");
  private final SelenideElement errorContainer = $(".form__error");

  @Step("Ввод данных пользователя: логин {login}, пароль {password}")
  public RegisterPage fillRegisterPage(String login, String password, String passwordSubmit) {
    usernameInput.setValue(login);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  @Step("Успешная отправка данных и переход на страницу логина")
  public LoginPage successSubmit() {
    submit();
    proceedLoginButton.click();
    return new LoginPage();
  }

  @Step("Отправка регистрационной формы")
  public void submit() {
    submitButton.click();
  }

  @Step("Проверка сообщения об ошибке: {errorMessage}")
  public RegisterPage checkAlertMessage(String errorMessage) {
    errorContainer.shouldHave(text(errorMessage));
    return this;
  }
}
