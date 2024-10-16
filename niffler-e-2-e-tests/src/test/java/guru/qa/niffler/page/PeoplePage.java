package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class PeoplePage {

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement peopleTable = $("#all");
  private final SelenideElement searchField = $("input[type='text']");

  public PeoplePage checkInvitationSentToUser(List<String> usernames) {
    for (String username : usernames) {
      searchFriend(username);
      SelenideElement friendRow = peopleTable.$$("tr").find(text(username));
      friendRow.shouldHave(text("Waiting..."));
    }
    return this;
  }

  public PeoplePage searchFriend(String username) {
    searchField.sendKeys(username);
    searchField.sendKeys(Keys.ENTER);
    return this;
  }
}
