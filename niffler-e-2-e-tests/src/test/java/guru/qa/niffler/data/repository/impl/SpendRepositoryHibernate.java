package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryHibernate implements SpendRepository {
    private final Config CFG = Config.getInstance();
    private final EntityManager entityManager = EntityManagers.em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity create(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        entityManager.joinTransaction();
        return entityManager.merge(spend);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));

    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        return Optional.ofNullable(entityManager.createQuery(
                                """
                                        SELECT c FROM CategoryEntity c WHERE username=:username and name=:name
                                        """,
                                CategoryEntity.class
                        ).setParameter("username", username)
                        .setParameter("name", name)
                        .getSingleResult()
        );
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public List<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        return entityManager.createQuery(
                        """
                                SELECT s FROM SpendEntity s
                                 WHERE username=:username
                                 AND description=:description
                                """,
                        SpendEntity.class
                )
                .setParameter("username", username)
                .setParameter("description", description)
                .getResultList();
    }

    @Override
    public void remove(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(
                entityManager.contains(spend)
                ? spend
                : entityManager.merge(spend)
        );
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.remove(
                entityManager.contains(category)
                ? category
                : entityManager.merge(category)
        );
    }
}
