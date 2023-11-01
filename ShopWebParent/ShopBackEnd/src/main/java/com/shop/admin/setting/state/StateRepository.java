package com.shop.admin.setting.state;

import com.shop.common.entity.Country;
import com.shop.common.entity.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StateRepository extends CrudRepository<State, Integer> {
    public List<State> findByCountryOrderByNameAsc(Country country);
}
