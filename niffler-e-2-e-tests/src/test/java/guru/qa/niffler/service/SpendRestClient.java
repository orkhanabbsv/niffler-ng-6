package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRestClient implements SpendClient {
    private final guru.qa.niffler.api.SpendApiClient apiClient = new guru.qa.niffler.api.SpendApiClient();

    @Override
    public SpendJson createSpend(SpendJson spendJson) {
        return apiClient.createSpend(spendJson);
    }

    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return apiClient.editSpend(spendJson);
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return apiClient.createCategory(categoryJson);
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<SpendJson> findSpendById(UUID id) {
        return Optional.ofNullable(apiClient.getSpend(id.toString()));
    }

    @Override
    public List<SpendJson> findSpendByUsernameAndSpendDescription(String username, String description) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        apiClient.removeSpends(spend.username(), String.valueOf(spend.id()));
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        throw new UnsupportedOperationException();
    }
}
