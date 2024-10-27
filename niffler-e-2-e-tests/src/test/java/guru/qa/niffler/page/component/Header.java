package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {
    private final SelenideElement menu = $("ul[role='menu']");

    public Header() {
        super($("#root header"));
    }

    @Step("Перейти на \"Friends\" страницу")
    public FriendsPage toFriendsPage() {
        self.$("button").click();
        menu.$$("li").find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Перейти на \"All People\" страницу")
    public PeoplePage toAllPeoplesPage() {
        self.$("button").click();
        menu.$$("li").find(text("All People")).click();
        return new PeoplePage();
    }

    @Step("Перейти на страницу профиля")
    public ProfilePage toProfilePage() {
        self.$("[aria-label='Menu']").click();
        menu.$(byText("Profile")).click();
        return new ProfilePage();
    }

    @Step("Перейти на главную страницу")
    public MainPage toMainPage() {
        self.$(".MuiToolbar-gutters").click();
        return new MainPage();
    }

    @Step("Разлогинить пользователя")
    public LoginPage signOut() {
        self.$("[aria-label='Menu']").click();
        menu.$(byText("Sign out")).click();
        return new LoginPage();
    }

    @Step("Добавить новую трату")
    public EditSpendingPage addSpendingPage() {
        self.$(byText("New spending")).click();
        return new EditSpendingPage();
    }
}
