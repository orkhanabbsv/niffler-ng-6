package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;


public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUser = authUserEntity(username, password);
            authUserRepository.create(authUser);
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        });
    }

    @Override
    public Optional<UserJson> findById(UUID id) {
        return xaTransactionTemplate.execute(() -> {
                    Optional<UserEntity> byId = userdataUserRepository.findById(id);
                    return byId.map(entity -> UserJson.fromEntity(entity, null));
                }
        );
    }

    @Override
    public Optional<UserJson> findByUsername(String username) {
        return xaTransactionTemplate.execute(() -> {
                    Optional<UserEntity> byId = userdataUserRepository.findByUsername(username);
                    return byId.map(entity -> UserJson.fromEntity(entity, null));
                }
        );
    }

    @Override
    public List<UserJson> createIncomeInvitation(UserJson requester, int count) {
        List<UserJson> incomeUsers = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    requester.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            incomeUsers.add(UserJson.fromEntity(adressee, null));
                            return null;
                        }
                );
            }
        }
        return incomeUsers;
    }

    @Override
    public List<UserJson> createOutcomeInvitation(UserJson requester, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();
        if (count > 0) {
            UserEntity adressee = userdataUserRepository.findById(
                    requester.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity targetEntity = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.sendInvitation(targetEntity, adressee);
                            outcomeInvitations.add(UserJson.fromEntity(targetEntity, null));
                            return null;
                        }
                );
            }
        }
        return outcomeInvitations;
    }

    @Override
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        List<UserJson> addedFriends = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity adressee = userdataUserRepository.create(userEntity(username));
                            userdataUserRepository.addFriend(targetEntity, adressee);
                            return null;
                        }
                );
            }
        }
        return addedFriends;
    }

    public void addFriend(UserJson targetUser, UserJson addedFriend) {
        UserEntity targetEntity = userdataUserRepository.findById(
                targetUser.id()
        ).orElseThrow();

        UserEntity addedFriendEntity = userdataUserRepository.findById(
                addedFriend.id()
        ).orElseThrow();

        xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.addFriend(targetEntity, addedFriendEntity);
                    return null;
                }
        );
    }

    public void createIncomeInvitation(UserJson targetUser, UserJson addressee) {
        UserEntity targetEntity = userdataUserRepository.findById(
                targetUser.id()
        ).orElseThrow();

        UserEntity addresseeEntity = userdataUserRepository.findById(
                addressee.id()
        ).orElseThrow();

        xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.sendInvitation(targetEntity, addresseeEntity);
                    return null;
                }
        );
    }

    public void createOutcomeInvitation(UserJson targetUser, UserJson requester) {
        UserEntity targetEntity = userdataUserRepository.findById(
                targetUser.id()
        ).orElseThrow();

        UserEntity requesterEntity = userdataUserRepository.findById(
                requester.id()
        ).orElseThrow();

        xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.sendInvitation(requesterEntity, targetEntity);
                    return null;
                }
        );
    }

    @Override
    public void deleteUser(UserJson user) {
        xaTransactionTemplate.execute(() -> {
                    userdataUserRepository.remove(UserEntity.fromJson(user));
                    return null;
                }
        );
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}
