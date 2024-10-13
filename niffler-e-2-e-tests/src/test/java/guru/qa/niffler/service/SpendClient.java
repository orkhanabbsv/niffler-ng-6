package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendClient {
    SpendJson createSpend(SpendJson spendJson);

    SpendJson updateSpend(SpendJson spendJson);

    CategoryJson createCategory(CategoryJson categoryJson);

    Optional<CategoryJson> findCategoryById(UUID id);

    Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name);

    Optional<SpendJson> findSpendById(UUID id);

    List<SpendJson> findSpendByUsernameAndSpendDescription(String username, String description);

    void deleteSpend(SpendJson spend);

    void deleteCategory(CategoryJson category);

}
