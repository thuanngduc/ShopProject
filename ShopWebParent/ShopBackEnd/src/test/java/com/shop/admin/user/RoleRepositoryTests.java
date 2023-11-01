package com.shop.admin.user;

import com.shop.common.entity.Role;
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
public class RoleRepositoryTests {
    @Autowired
    private RoleRepository repo;
    @Test
    public void testCreateFileRole()
    {
        Role roleAdmin = new Role("Admin",  "manage everything");
        Role savedRole = repo.save(roleAdmin);
        assertThat(savedRole.getId()).isNotNull();
    }
    @Test
    public void testCreateRestRoles()
    {
        Role roleSalesperson = new Role("Salesperson", "manage product" +
                " price, customers, shipping, orders and sales report");
        Role roleEditor = new Role("Editor", "manage categories, brands" +
                "products, articles, menus");
        Role roleShipper = new Role("Shipper", "view products, view orders" +
                "and update order status");
        Role roleAssistant = new Role("Assistant", "manage questions and reviews");

        repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
    }
}
