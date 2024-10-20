package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());

    @Override
    public UserEntity create(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(UserEntity.class, id)
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        entityManager.joinTransaction();
        return Optional.ofNullable(
                entityManager.createQuery(
                                "SELECT u FROM UserEntity u WHERE u.username =: username",
                                UserEntity.class
                        ).setParameter("username", username)
                        .getSingleResult()
        );
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.PENDING, addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }

    @Override
    public void remove(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.remove(
                entityManager.contains(user)
                ? user
                : entityManager.merge(user)
        );
    }
}
