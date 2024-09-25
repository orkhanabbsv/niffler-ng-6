package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient {
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity category = categoryDao.createCategory(spendEntity.getCategory());
            spendEntity.setCategory(category);
        }

        return SpendJson.fromEntity(spendDao.createSpend(spendEntity));
    }

    public void deleteSpend(SpendEntity spend) {
        spendDao.deleteSpend(spend);
        if (spend.getCategory() != null) {
            categoryDao.deleteCategory(spend.getCategory());
        }
    }

    public List<SpendEntity> findAllByUsername(String username) {
        return spendDao.findAllByUsername(username);
    }

    public Optional<SpendEntity> findSpendById(UUID id) {
        return spendDao.findSpendById(id);
    }
}
