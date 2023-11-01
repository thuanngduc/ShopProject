package com.shop.admin.shippingrate;

import com.shop.admin.paging.SearchRepository;
import com.shop.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer>, CrudRepository<ShippingRate, Integer> {
    @Query("SELECT sr FROM ShippingRate sr WHERE sr.country.id = ?1 AND sr.state = ?2")
    public ShippingRate findByCountryAndState(Integer countryId, String state);

    @Query("UPDATE ShippingRate  sr SET sr.codSupported = ?2 WHERE sr.id = ?1")
    @Modifying
    public void updateCODSupport(Integer id, boolean enabled);

    @Query("SELECT sr FROM ShippingRate  sr WHERE sr.country.name LIKE %?1% OR sr.state LIKE %?1%")
    public Page<ShippingRate> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
