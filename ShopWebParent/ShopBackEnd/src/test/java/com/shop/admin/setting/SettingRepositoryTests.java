package com.shop.admin.setting;

import com.shop.common.entity.setting.Setting;
import com.shop.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {

    @Autowired SettingRepository repo;

    @Test
    public void testCreateGeneralSettings()
    {
//        Setting siteName = new Setting("SITE_NAME", "Shop", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "Shop.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2023 Sop Ltd.", SettingCategory.GENERAL);

        repo.saveAll(List.of(siteLogo, copyright));
        Iterable<Setting> iterable = repo.findAll();
//        Setting savedSetting = repo.save(siteName);

//        assertThat(savedSetting).isNotNull();

        assertThat(iterable).size().isGreaterThan(0);
    }
    @Test
    public void testCreateCurrencySettings()
    {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType ));
//        Iterable<Setting> iterable = repo.findAll();
//        assertThat(iterable).size().isGreaterThan(0);

    }

    @Test
    public void testListSettingByCategory()
    {
        List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);
        settings.forEach(System.out::println);

    }
}
