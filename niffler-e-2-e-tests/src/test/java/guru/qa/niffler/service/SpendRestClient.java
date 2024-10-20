package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRestClient implements SpendClient {
    private final guru.qa.niffler.api.SpendApiClient apiClient = new guru.qa.niffler.api.SpendApiClient();

    @Step("Создать новую трату с данными: {spendJson}")
    @Override
    public SpendJson createSpend(SpendJson spendJson) {
        return apiClient.createSpend(spendJson);
    }

    @Step("Обновить трату с данными: {spendJson}")
    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return apiClient.editSpend(spendJson);
    }

    @Step("Создать категорию с данными: {categoryJson}")
    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return apiClient.createCategory(categoryJson);
    }

    @Step("Найти категорию по ID: {id}")
    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Step("Найти категорию по имени пользователя: {username} и имени траты: {name}")
    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name) {
        throw new UnsupportedOperationException();
    }

    @Step("Найти трату по ID: {id}")
    @Override
    public Optional<SpendJson> findSpendById(UUID id) {
        return Optional.ofNullable(apiClient.getSpend(id.toString()));
    }

    @Step("Найти траты по имени пользователя: {username} и описанию траты: {description}")
    @Override
    public List<SpendJson> findSpendByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException();
    }

    @Step("Удалить трату: {spend}")
    @Override
    public void deleteSpend(SpendJson spend) {
        apiClient.removeSpends(spend.username(), String.valueOf(spend.id()));
    }

    @Step("Удалить категорию: {category}")
    @Override
    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException();
    }
}
