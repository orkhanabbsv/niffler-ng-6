package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement searchField = $("input[type='text']");

  public FriendsPage checkExistingFriends(List<String> expectedUsernames) {
    for (String expectedUsername : expectedUsernames) {
      searchFriend(expectedUsername);
      friendsTable.$$("tr").find(text(expectedUsername)).should(visible);
    }
    return this;
  }

  public FriendsPage checkNoExistingFriends() {
    friendsTable.$$("tr").shouldHave(size(0));
    return this;
  }

  public FriendsPage checkExistingInvitations(List<String> expectedUsernames) {
    for (String expectedUsername : expectedUsernames) {
      searchFriend(expectedUsername);
      requestsTable.$$("tr").find(text(expectedUsername)).should(visible);
    }
    return this;
  }

  public FriendsPage searchFriend(String username) {
    searchField.sendKeys(username);
    searchField.sendKeys(Keys.ENTER);
    return this;
  }
}
