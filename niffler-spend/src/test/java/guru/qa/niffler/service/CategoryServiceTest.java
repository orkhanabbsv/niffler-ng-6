package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    public static final String USERNAME = "duck";

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "not_found";
        final UUID id = UUID.randomUUID();

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    @Test
    void getAllCategoriesShouldFilteredArchivedCategories(@Mock CategoryRepository categoryRepository) {
        //Arrange
        String username = "duck";
        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
                .thenReturn(getCategoryList(username));

        //Act
        List<CategoryJson> categories = categoryService.getAllCategories(username, true);

        //Assert
        assertEquals(1, categories.size());
        assertEquals("Магазины", categories.getFirst().name());
        assertEquals(username, categories.getFirst().username());
    }

    @Test
    void updateShouldThrowExceptionWhenCategoryDoesNotExist(@Mock CategoryRepository categoryRepository) {
        //Arrange
        CategoryService categoryService = new CategoryService(categoryRepository);
        final UUID unknownCategory = UUID.randomUUID();

        when(categoryRepository.findByUsernameAndId(eq(USERNAME), eq(unknownCategory)))
                .thenReturn(Optional.empty());

        CategoryJson categoryJson = new CategoryJson(
                unknownCategory,
                "Магазины",
                USERNAME,
                false
        );

        //Act
        CategoryNotFoundException categoryNotFoundException =
                assertThrows(CategoryNotFoundException.class, () -> categoryService.update(categoryJson));

        //Assert
        assertEquals("Can`t find category by id: '" + unknownCategory + "'",
                categoryNotFoundException.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTooManyCategoriesExist(@Mock CategoryRepository categoryRepository) {
        //Arrange
        CategoryService categoryService = new CategoryService(categoryRepository);
        final CategoryEntity cat = getCategoryList(USERNAME).getLast();

        when(categoryRepository.findByUsernameAndId(eq(USERNAME), eq(cat.getId())))
                .thenReturn(Optional.of(cat));
        when(categoryRepository.countByUsernameAndArchived(eq(USERNAME), eq(false)))
                .thenReturn(15L);

        CategoryJson categoryJson = new CategoryJson(
                cat.getId(),
                "Магазины",
                USERNAME,
                false
        );

        //Act
        TooManyCategoriesException tooManyCategoriesException =
                assertThrows(TooManyCategoriesException.class, () -> categoryService.update(categoryJson));

        //Assert
        assertEquals("Can`t unarchive category for user: '" + USERNAME + "'",
                tooManyCategoriesException.getMessage());
    }

    @Test
    void categoryShouldUpdate(@Mock CategoryRepository categoryRepository) {
        final CategoryEntity cat = getCategoryList(USERNAME).getFirst();
        final String newCategoryName = "Бары";
        Mockito.when(categoryRepository.findByUsernameAndId(eq(USERNAME), eq(cat.getId())))
                .thenReturn(Optional.of(
                        cat
                ));
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                cat.getId(),
                newCategoryName,
                USERNAME,
                true
        );

        CategoryJson updatedCategory = categoryService.update(categoryJson);
        assertEquals("Бары", updatedCategory.name());
        assertEquals(USERNAME, updatedCategory.username());
        assertTrue(updatedCategory.archived());
        assertEquals(cat.getId(), updatedCategory.id());
    }

    @Test
    void saveShouldThrowExceptionWhenTooManyCategoriesExist(@Mock CategoryRepository categoryRepository) {
        //Arrange
        CategoryService categoryService = new CategoryService(categoryRepository);
        final CategoryEntity cat = getCategoryList(USERNAME).getLast();

        when(categoryRepository.countByUsernameAndArchived(eq(USERNAME), eq(false)))
                .thenReturn(15L);

        CategoryJson categoryJson = new CategoryJson(
                cat.getId(),
                "Магазины",
                USERNAME,
                false
        );

        //Act
        TooManyCategoriesException tooManyCategoriesException =
                assertThrows(TooManyCategoriesException.class, () -> categoryService.save(categoryJson));

        //Assert
        assertEquals("Can`t add over than 8 categories for user: '" + USERNAME + "'",
                tooManyCategoriesException.getMessage());
    }

    @Test
    void categoryShouldSave(@Mock CategoryRepository categoryRepository) {
        //Arrange
        final CategoryEntity cat = getCategoryList(USERNAME).getLast();
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);
        CategoryJson categoryJson = CategoryJson.fromEntity(cat);

        //Act
        CategoryEntity savedCategory = categoryService.save(categoryJson);

        //Assert
        assertEquals(cat.getName(), savedCategory.getName());
        assertEquals(cat.getUsername(), savedCategory.getUsername());
        assertFalse(savedCategory.isArchived());
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();

        Mockito.when(categoryRepository.findByUsernameAndId(eq(USERNAME), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                catName,
                USERNAME,
                true
        );

        InvalidCategoryNameException ex = assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + catName + "'",
                ex.getMessage()
        );
    }

    @Test
    void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "Бары",
                username,
                true
        );

        categoryService.update(categoryJson);
        ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(argumentCaptor.capture());
        assertEquals("Бары", argumentCaptor.getValue().getName());
        assertEquals("duck", argumentCaptor.getValue().getUsername());
        assertTrue(argumentCaptor.getValue().isArchived());
        assertEquals(id, argumentCaptor.getValue().getId());
    }

    private static List<CategoryEntity> getCategoryList(String username) {
        CategoryEntity firstCategory = new CategoryEntity();
        firstCategory.setId(UUID.randomUUID());
        firstCategory.setName("Магазины");
        firstCategory.setUsername(username);
        firstCategory.setArchived(false);

        CategoryEntity secondCategory = new CategoryEntity();
        secondCategory.setId(UUID.randomUUID());
        secondCategory.setName("Бары");
        secondCategory.setUsername(username);
        secondCategory.setArchived(true);


        return List.of(
                firstCategory,
                secondCategory
        );
    }
}