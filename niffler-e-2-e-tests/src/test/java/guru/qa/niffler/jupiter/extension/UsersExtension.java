package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersExtension.class);
    public static final String defaultPassword = "12345";
    private final UsersClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        String username = randomUsername();
                        UserJson testUser = usersClient.createUser(username, defaultPassword);

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                testUser.addTestData(
                                        new TestData(
                                                defaultPassword,
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>(),
                                                new ArrayList<>()
                                        )
                                )
                        );
                    }
                    UserJson userJson = context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);

                    UserJson user = userJson == null
                               ? usersClient.findByUsername(userAnno.username()).orElseThrow()
                               : userJson;

                    List<UserJson> incomeInvitation =
                                usersClient.createIncomeInvitation(user, userAnno.incomeInvitations());
                        user.testData().income().addAll(incomeInvitation.stream().map(UserJson::username).toList());

                        System.out.println("Income invitations: " + user.testData().income().size());

                        List<UserJson> outcomeInvitation =
                                usersClient.createOutcomeInvitation(user, userAnno.outcomeInvitations());
                        user.testData().outcome().addAll(outcomeInvitation.stream().map(UserJson::username).toList());

                        System.out.println("Outcome invitations: " + user.testData().outcome().size());


                        List<UserJson> addedFriends = usersClient.addFriend(user, userAnno.addedFriends());
                        user.testData().addedFriends().addAll(addedFriends.stream().map(UserJson::username).toList());
                        System.out.println("AddedFriends: " + user.testData().addedFriends().size());
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
    }
}
