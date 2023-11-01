package com.shop.shipping;

import com.shop.common.entity.Country;
import com.shop.common.entity.ShippingRate;
import org.springframework.data.repository.CrudRepository;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer> {
    public ShippingRate findByCountryAndState(Country country, String state);
}
