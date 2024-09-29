package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);
}
