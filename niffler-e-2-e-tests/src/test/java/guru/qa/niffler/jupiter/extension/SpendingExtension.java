package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        UserJson user = context.getStore(UsersExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
                        List<SpendJson> result = new ArrayList<>();

                        for (Spending spendAnno : userAnno.spendings()) {
                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            null,
                                            spendAnno.category(),
                                            getUsername(userAnno, user),
                                            false
                                    ),
                                    CurrencyValues.RUB,
                                    spendAnno.amount(),
                                    spendAnno.description(),
                                    getUsername(userAnno, user)
                            );

                            SpendJson createdSpend = spendClient.createSpend(spend);
                            result.add(createdSpend);
                        }


                        if (user != null) {
                            user.testData().spendings().addAll(result);
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    result
                            );
                        }
                    }
                });
    }

    private String getUsername(User userAnno, UserJson user) {
        return "".equals(userAnno.username()) ? user.username() : userAnno.username();
    }


    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        UserJson user = context.getStore(UsersExtension.NAMESPACE).get(context.getUniqueId(), UserJson.class);

        List<SpendJson> spendings = user != null
                                    ? user.testData().spendings()
                                    : context.getStore(NAMESPACE).get(context.getUniqueId(), List.class);


        for (SpendJson spendJson : spendings) {
            spendClient.deleteSpend(spendJson);
            spendClient.deleteCategory(spendJson.category());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (SpendJson[]) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class).toArray();
    }
}
