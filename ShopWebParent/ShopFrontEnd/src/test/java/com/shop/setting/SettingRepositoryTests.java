package com.shop.setting;

import com.shop.common.entity.setting.Setting;
import com.shop.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class SettingRepositoryTests {

    @Autowired SettingRepository repo;

    @Test
    public void testFindByTwoCategories()
    {
        List<Setting> settings = repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        settings.forEach(System.out::println);
    }
}
