package com.shop.admin.user;


import com.shop.common.entity.Role;
import com.shop.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
    @Autowired
    private UserRepository repo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateNewUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userThuanND = new User("thuan@gmail.com", "thuanpassword"
                , "Thuan", "Nguyen Duc");
        userThuanND.addRole(roleAdmin);
        User savedUser = repo.save(userThuanND);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateNewUserWithTwoRoles() {
        User userSecond = new User("User2@gmail.com", "user2", "User", "VanlaUser");
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        userSecond.addRole(roleEditor);
        userSecond.addRole(roleAssistant);
        User savedUser = repo.save(userSecond);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUser() {
        Iterable<User> listUsers = repo.findAll();
        listUsers.forEach(user -> System.out.println(user));
    }

    @Test
    public void getUserById() {
        User userAdmin = repo.findById(2).get();
        System.out.println(userAdmin);
        assertThat(userAdmin).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User userUpdate = repo.findById(2).get();
        userUpdate.setEnabled(true);
        repo.save(userUpdate);
    }

    @Test
    public void testUpdateUserRoles() {
        User userUpdate = repo.findById(2).get();
        Role roleEditor = new Role(3);
        Role roleSaleperson = new Role(2);
        userUpdate.getRoles().remove(roleEditor);
        userUpdate.addRole(roleSaleperson);
        repo.save(userUpdate);
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 2;
        repo.deleteById(userId);
    }

    @Test
    public void testGetUserByEmail() {
        String email = "usertest5@usertest5.abc";
        User user = repo.getUserByEmail(email);
        assertThat(user).isNotNull();
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long countById = repo.countById(id);
        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser()
    {
        Integer id = 1;
        repo.updateEnabledStatus(id, false);
    }
    @Test
    public void testEnableUser()
    {
        Integer id = 2;
        repo.updateEnabledStatus(id, true);
    }
    @Test
    public void testListFirstPage()
    {
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));
        assertThat(listUsers.size()).isEqualTo(pageSize);
    }
    @Test
    public void testSerachUsers()
    {
        String keyword = "bruce";
        int pageNumber = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repo.findAll(keyword,pageable);
        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));
        assertThat(listUsers.size()).isGreaterThan(0);
    }
}
