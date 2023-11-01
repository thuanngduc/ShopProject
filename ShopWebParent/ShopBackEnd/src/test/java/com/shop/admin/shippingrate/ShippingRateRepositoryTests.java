package com.shop.admin.shippingrate;

import com.shop.common.entity.Country;
import com.shop.common.entity.ShippingRate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTests {
    @Autowired private ShippingRateRepository repo;
    @Autowired TestEntityManager entityManager;

    @Test
    public void testCreateNew()
    {
        Country india = new Country(107);
        ShippingRate newRate = new ShippingRate();
        newRate.setCountry(india);
        newRate.setState("Maharashtra");
        newRate.setRate(8.25f);
        newRate.setDays(3);
        newRate.setCodSupported(true);

        ShippingRate savedRate = repo.save(newRate);
        Assertions.assertThat(savedRate).isNotNull();
        Assertions.assertThat(savedRate.getId()).isGreaterThan(0);
    }
    @Test
    public void testUpdate()
    {
        Integer rateId = 1;
        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        rate.setRate(9.15f);
        rate.setDays(2);
        ShippingRate updatedRate = repo.save(rate);

        Assertions.assertThat(updatedRate.getRate()).isEqualTo(9.15f);
        Assertions.assertThat(updatedRate.getDays()).isEqualTo(2);
    }

    @Test
    public void testFindAll()
    {
        List<ShippingRate> rates = (List<ShippingRate>) repo.findAll();
        Assertions.assertThat(rates.size()).isGreaterThan(0);

        rates.forEach(System.out::println);
    }

    @Test
    public void testFindByCountryAndState()
    {
        Integer countryId = 106;
        String state = "Maharashtra";

        ShippingRate rate = repo.findByCountryAndState(countryId, state);

        Assertions.assertThat(rate).isNotNull();
        System.out.println(rate);

    }

    @Test
    public void testUpdateCODSupport()
    {
        Integer  rateId = 1;
        repo.updateCODSupport(rateId, false);

        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        Assertions.assertThat(rate.isCodSupported()).isFalse();
    }

    @Test
    public void testDelete()
    {
        Integer rateId = 2;
        repo.deleteById(rateId);

        ShippingRate rate = entityManager.find(ShippingRate.class, rateId);
        Assertions.assertThat(rate).isNull();
    }

}
