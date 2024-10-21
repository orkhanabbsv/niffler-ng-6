package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate jdbcTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Step("Создать новую трату с данными: {spendJson}")
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
                    if (spendEntity.getCategory().getId() == null) {
                        CategoryEntity categoryEntity = spendRepository.createCategory(spendEntity.getCategory());
                        spendEntity.setCategory(categoryEntity);
                    }
                    return SpendJson.fromEntity(
                            spendRepository.create(spendEntity)
                    );
                }
        );
    }

    @Step("Обновить трату с данными: {spendJson}")
    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> SpendJson.fromEntity(
                        spendRepository.update(
                                SpendEntity.fromJson(spendJson)
                        )
                )
        );
    }

    @Step("Создать категорию с данными: {categoryJson}")
    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(
                        spendRepository.createCategory(
                                CategoryEntity.fromJson(categoryJson)
                        )
                )
        );
    }

    @Step("Найти категорию по ID: {id}")
    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> spend = spendRepository.findCategoryById(id);
                    return spend.map(CategoryJson::fromEntity);
                }
        );
    }

    @Step("Найти категорию по имени пользователя: {username} и имени траты: {name}")
    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> spend = spendRepository.findCategoryByUsernameAndSpendName(username, name);
                    return spend.map(CategoryJson::fromEntity);
                }
        );
    }

    @Step("Найти трату по ID: {id}")
    @Override
    public Optional<SpendJson> findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<SpendEntity> spend = spendRepository.findById(id);
                    return spend.map(SpendJson::fromEntity);
                }
        );
    }

    @Step("Найти траты по имени пользователя: {username} и описанию траты: {description}")
    @Override
    public List<SpendJson> findSpendByUsernameAndSpendDescription(String username, String description) {
        return jdbcTxTemplate.execute(() -> {
                    List<SpendEntity> byUsernameAndSpendDescription =
                            spendRepository.findByUsernameAndSpendDescription(username, description);
                    return byUsernameAndSpendDescription.stream()
                            .map(SpendJson::fromEntity)
                            .toList();
                }
        );
    }

    @Step("Удалить трату: {spend}")
    @Override
    public void deleteSpend(SpendJson spend) {
        jdbcTxTemplate.execute(() -> {
                    spendRepository.remove(SpendEntity.fromJson(spend));
                    return null;
                }
        );
    }

    @Step("Удалить категорию: {category}")
    @Override
    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> {
                    spendRepository.removeCategory(CategoryEntity.fromJson(category));
                    return null;
                }
        );
    }
}
