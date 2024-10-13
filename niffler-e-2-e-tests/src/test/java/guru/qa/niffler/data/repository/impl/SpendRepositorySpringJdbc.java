package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositorySpringJdbc implements SpendRepository {
    private final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO \"spend\" " +
                                    "(username, spend_date, currency, amount, description, category_id) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, spend.getUsername());
                    ps.setDate(2, new Date(spend.getSpendDate().getTime()));
                    ps.setString(3, spend.getCurrency().name());
                    ps.setDouble(4, spend.getAmount());
                    ps.setString(5, spend.getDescription());
                    ps.setObject(6, spend.getCategory().getId());
                    return ps;
                }, kh
        );
        final UUID generatedKey;
        generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            """
                                         update "spend"
                                            set spend_date = ?,
                                            currency = ?,
                                            amount = ?,
                                            description = ?
                                            where id = ?
                                    """
                    );
                    ps.setDate(1, new Date(spend.getSpendDate().getTime()));
                    ps.setObject(2, spend.getCurrency());
                    ps.setDouble(3, spend.getAmount());
                    ps.setString(3, spend.getDescription());
                    ps.setObject(4, spend.getId());

                    ps.execute();
                    return ps;
                }
        );
        return spend;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                             INSERT INTO category (username, name, archived)
                             VALUES (?, ?, ?)
                        """,
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
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        "SELECT * FROM \"category\" WHERE id = ?",
                        CategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        """
                                select * from "category" where username=? and name=?
                                   """,
                        CategoryEntityRowMapper.instance,
                        username,
                        name
                )
        );
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return Optional.ofNullable(jdbcTemplate.queryForObject(
                        """
                                select * from "spend" where username=? and name=?
                                """,
                        SpendEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public List<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                """
                        select * from "spend" where username=? and description=?
                        """,
                SpendEntityRowMapper.instance,
                username,
                description
        );
    }

    @Override
    public void remove(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
                """
                        delete * from "spend" where id=?
                        """,
                spend.getId()
        );
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update(
                """
                        delete from "spend" where category_id=?
                        """,
                category.getId()
        );

        jdbcTemplate.update(
                """
                        delete from "category" where id=?
                        """,
                category.getId()
        );
    }
}
