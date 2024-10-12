package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;


public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

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

    @Override
    public SpendJson updateSpend(SpendJson spendJson) {
        return jdbcTxTemplate.execute(() -> SpendJson.fromEntity(
                        spendRepository.update(
                                SpendEntity.fromJson(spendJson)
                        )
                )
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson categoryJson) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(
                        spendRepository.createCategory(
                                CategoryEntity.fromJson(categoryJson)
                        )
                )
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> spend = spendRepository.findCategoryById(id);
                    return spend.map(CategoryJson::fromEntity);
                }
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> spend = spendRepository.findCategoryByUsernameAndSpendName(username, name);
                    return spend.map(CategoryJson::fromEntity);
                }
        );
    }

    @Override
    public Optional<SpendJson> findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<SpendEntity> spend = spendRepository.findById(id);
                    return spend.map(SpendJson::fromEntity);
                }
        );
    }

    @Override
    public Optional<SpendJson> findSpendByUsernameAndSpendDescription(String username, String description) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<SpendEntity> spend = spendRepository.findByUsernameAndSpendDescription(username, description);
                    return spend.map(SpendJson::fromEntity);
                }
        );
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        jdbcTxTemplate.execute(() -> {
                    spendRepository.remove(SpendEntity.fromJson(spend));
                    return null;
                }
        );
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() -> {
                    spendRepository.removeCategory(CategoryEntity.fromJson(category));
                    return null;
                }
        );
    }
}
