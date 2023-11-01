package com.shop.admin.setting.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.common.entity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryRepository repo;
    @Test
    @WithMockUser(username = "cat@codejava.net", password = "ycat2023", roles = "ADMIN") //fake data
    public void testListCountries() throws Exception {
        String url = "/countries/list";
        MvcResult result = mockMvc.perform(get(url)).andExpect(status().isOk()).andDo(print()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
//        System.out.println(jsonResponse);

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
//        for (Country country : countries)
//        {
//            System.out.println(country);
//        }
        assertThat(countries).hasSizeGreaterThan(0);
    }
    @Test
    @WithMockUser(username = "cat@codejava.net", password = "ycat2023", roles = "ADMIN") //fake data
    public void testCreateCountry() throws Exception {
        String url = "/countries/save";
        String countryName = "Germany";
        String countryCode = "DE";
        Country country = new Country(countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        Integer countryId = Integer.parseInt(response);
        Optional<Country> findById = repo.findById(countryId);

        assertThat(findById.isPresent());

        Country savedCountry = findById.get();

//        System.out.println("Country ID: " + response);

        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }
    @Test
    @WithMockUser(username = "cat@codejava.net", password = "ycat2023", roles = "ADMIN") //fake data
    public void testUpdateCountry() throws Exception
    {
        String url = "/countries/save";
        Integer countryId = 6;
        String countryName = "China";
        String countryCode = "CN";
        Country country = new Country(countryId,countryName, countryCode);

        MvcResult result = mockMvc.perform(post(url).contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(countryId)))
                .andReturn();

        Optional<Country> findById = repo.findById(countryId);

        assertThat(findById.isPresent());

        Country savedCountry = findById.get();

//        System.out.println("Country ID: " + response);

        assertThat(savedCountry.getName()).isEqualTo(countryName);
    }

    @Test
    @WithMockUser(username = "cat@codejava.net", password = "ycat2023", roles = "ADMIN") //fake data
    public void testDeleteCountry() throws Exception {
        Integer countryId = 6;
        String url = "/countries/delete/"+countryId;
        mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        Optional<Country> findById = repo.findById(countryId);
        assertThat(findById).isNotPresent();

    }
}
