package com.shop.shipping;

import com.shop.common.entity.Country;
import com.shop.common.entity.ShippingRate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShippingRateRepositoryTests {
    @Autowired private  ShippingRateRepository repo;
    @Test
    public void testFindByCountryAndState()
    {
        Country usa = new Country(234);
        String state = "New York";
        ShippingRate shippingRate = repo.findByCountryAndState(usa, state);
        Assertions.assertThat(shippingRate).isNotNull();
        System.out.println(shippingRate);
    }
}
