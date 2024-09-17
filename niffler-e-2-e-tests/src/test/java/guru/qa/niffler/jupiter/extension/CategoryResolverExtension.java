package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import static guru.qa.niffler.jupiter.extension.CreateCategoryExtension.CATEGORY_NAMESPACE;

public class CategoryResolverExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        return extensionContext.getStore(CATEGORY_NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
