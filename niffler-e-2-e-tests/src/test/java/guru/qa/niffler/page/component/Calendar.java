package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class Calendar extends BaseComponent<Calendar> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public Calendar(SelenideElement self) {
        super(self);
    }

    public Calendar() {
        super($("input[name='date']"));
    }

    @Step("Выбор даты в календаре: {date}")
    public Calendar selectDateInCalendar(Date date) {
        String formattedDate = dateFormat.format(date);
        self.clear();
        self.setValue(formattedDate);
        return this;
    }
}
