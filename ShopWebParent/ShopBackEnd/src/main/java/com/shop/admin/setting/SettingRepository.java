package com.shop.admin.setting;

import com.shop.common.entity.setting.Setting;
import com.shop.common.entity.setting.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {

    public List<Setting> findByCategory(SettingCategory category);


}
