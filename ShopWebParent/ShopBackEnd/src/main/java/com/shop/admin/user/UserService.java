package com.shop.admin.user;

import com.shop.admin.paging.PagingAndSortingHelper;
import com.shop.common.entity.Role;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    public static final int USER_PER_PAGE = 4;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getByEmail(String email)
    {
        return userRepo.getUserByEmail(email);
    }
    public List<User> listAll() {
        return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
    }
    public void listByPage(int pageNum, PagingAndSortingHelper helper)
    {
        helper.listEntities(pageNum, USER_PER_PAGE, userRepo);
    }

    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        if(isUpdatingUser)
        {
            User existingUser = userRepo.findById(user.getId()).get();
            if (user.getPassword().isEmpty())
            {
                user.setPassword(existingUser.getPassword());
            }
            else
            {
                encodePassword(user);
            }
        }
        return userRepo.save(user);
    }
    public User updateAccount(User userInForm)
    {
        User userInDB = userRepo.findById(userInForm.getId()).get();
        if(!userInForm.getPassword().isEmpty())
        {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }
        if(userInForm.getPhotos() != null)
        {
            userInDB.setPhotos(userInForm.getPhotos());
        }
        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());
        return userRepo.save(userInDB);
    }

    private void encodePassword(User user)
    {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
    public boolean isEmailUnique(Integer id,String email)
    {
        User userByEmail = userRepo.getUserByEmail(email);
        if (userByEmail == null) return true;
        boolean isCreatingNew = (id == null);
        if (isCreatingNew)
        {
            if (userByEmail != null) return false;
        }else {
            if (userByEmail.getId() != id) return false;
        }
        return true;
    }

    public User get(Integer id) throws UserNotFoundException {

        try
        {
            return userRepo.findById(id).get();
        }
        catch (NoSuchElementException ex)
        {
            throw new UserNotFoundException("Could not find any user with ID"+ id);
        }
    }
    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if(countById == null || countById ==0)
        {
            throw new UserNotFoundException("Could not find any user with ID"+ id);
        }
        userRepo.deleteById(id);
    }
    public void updateUserEnabledStatus(Integer id, boolean enabled)
    {
        userRepo.updateEnabledStatus(id, enabled);
    }

}