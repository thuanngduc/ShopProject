package com.shop.category;

import com.shop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CategoryRepositoryTests {
    @Autowired private CategoryRepository repo;

    @Test
    public void testListEnabledCategories()
    {
        List<Category> categories = repo.findAllEnabled();

        categories.forEach(category -> {
            Set<Category> children = category.getChildren();
            if (children == null || children.size() == 0) {
                System.out.println(category);
            }
            System.out.println(category.getName() + " (" + category.isEnabled() + ")");
        });
    }
    @Test
    public void testFindCategoryByAlias()
    {
        String alias = "electronics";
        Category category = repo.findByAliasEnabled(alias);
        assertThat(category).isNotNull();
    }
}
