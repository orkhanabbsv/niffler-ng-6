package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {
    private final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }

            spend.setId(generatedKey);
            return spend;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "update \"spend\" " +
                        "set spend_date = ?," +
                        "currency = ?," +
                        "amount = ?," +
                        "description = ? " +
                        "where id = ?"
        )) {
            ps.setDate(1, new Date(spend.getSpendDate().getTime()));
            ps.setObject(2, spend.getCurrency());
            ps.setDouble(3, spend.getAmount());
            ps.setString(3, spend.getDescription());
            ps.setObject(4, spend.getId());

            ps.execute();
            return spend;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id from ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"category\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("id", UUID.class));
                    category.setName(rs.getString("name"));
                    category.setArchived(rs.getBoolean("archived"));
                    category.setUsername(rs.getString("username"));
                    return Optional.of(category);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "select * from \"category\" where username=? and name=?"
        )) {
            ps.setString(1, username);
            ps.setString(2, name);
            ps.execute();

            CategoryEntity category = null;
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    category = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                }

                if (category == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(category);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id=?"
        )) {
            ps.setObject(1, id);

            ps.execute();
            SpendEntity spend = null;
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    spend = SpendEntityRowMapper.instance.mapRow(rs, 1);
                }
                if (spend != null) {
                    return Optional.of(spend);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "select * from \"spend\" where username=? and description=?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute();

            SpendEntity spend = null;
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    spend = SpendEntityRowMapper.instance.mapRow(rs, 1);
                }

                if (spend == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(spend);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "delete from \"spend\" where id=?"
        )) {
            ps.setObject(1, spend.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        try (PreparedStatement psSpend = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "delete from \"spend\" where category_id=?"
        );
             PreparedStatement psCategory = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                     "delete from \"category\" where id=?"
             )) {
            psSpend.setObject(1, category.getId());
            psSpend.execute();

            psCategory.setObject(1, category.getId());
            psCategory.execute();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }
}
