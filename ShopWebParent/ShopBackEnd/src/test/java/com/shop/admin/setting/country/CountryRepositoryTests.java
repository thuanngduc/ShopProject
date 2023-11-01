package com.shop.admin.setting.country;

import com.shop.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class CountryRepositoryTests {
    @Autowired private CountryRepository repo;
    @Test
    public void testCreateCountry()
    {
//        Country country = repo.save(new Country("China", "CN"));
//        Country country = repo.save(new Country("United States", "US"));
//        Country country = repo.save(new Country("United Kingdom", "UK"));
//        Country country = repo.save(new Country("VietNam", "VN"));
        Country country = repo.save(new Country("ASDSAD", "DSF"));
        assertThat(country).isNotNull();
        assertThat(country.getId()).isGreaterThan(0);
    }
    @Test
    public void testListCountries()
    {
        List<Country> listCountries = repo.findAllByOrderByNameAsc();
        listCountries.forEach(System.out::println);
        assertThat(listCountries.size()).isGreaterThan(0);
    }
    @Test
    public void testUpdateCountry()
    {
        Integer id = 1;
        String name = "Republic of India";
        String code = "IN";
        Country country = repo.findById(id).get();
//        country.setName(name);
        country.setCode(code);

        Country updatedCountry = repo.save(country);
//        assertThat(updatedCountry.getName()).isEqualTo(name);
        assertThat(updatedCountry.getCode()).isEqualTo(code);
    }

    @Test
    public void testGetCountry()
    {
        Integer id = 1;
        Country country = repo.findById(id).get();
        assertThat(country).isNotNull();
    }
    @Test
    public void testDeleteCountry()
    {
        Integer id = 5;
        repo.deleteById(id);

        Optional<Country> findById = repo.findById(id);
        assertThat(findById.isEmpty());
    }
}
