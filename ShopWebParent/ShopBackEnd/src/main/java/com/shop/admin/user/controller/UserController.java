package com.shop.admin.user.controller;

import com.shop.admin.AmazonS3Util;
import com.shop.admin.FileUploadUtil;
import com.shop.admin.paging.PagingAndSortingHelper;
import com.shop.admin.paging.PagingAndSortingParam;
import com.shop.admin.user.UserNotFoundException;
import com.shop.admin.user.UserService;
import com.shop.admin.user.export.UserCsvExporter;
import com.shop.admin.user.export.UserExcelExporter;
import com.shop.admin.user.export.UserPdfExporter;
import com.shop.common.entity.Role;
import com.shop.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;
    @GetMapping("/users")
    public String listFirstPage()
    {
//        List<User> listUsers = service.listAll();
//        model.addAttribute("listUsers", listUsers);
//        return "users";
//        return listByPage(helper,1, model, "firstName", "asc", null);
        return "redirect:/users/page/1?sortField=firstName&sortDir=asc";
    }
    @GetMapping("users/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum)
    {
        service.listByPage(pageNum, helper);

        return "users/users";
    }
    @GetMapping("/users/new")
    public String newUser(Model model)
    {
        List<Role> listRoles = service.listRoles();
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");
        return "users/user_form";
    }
    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam("image") MultipartFile multipartFile) throws IOException {
        System.out.println(user);
        System.out.println(multipartFile.getOriginalFilename());
        if(!multipartFile.isEmpty())
        {
            System.out.println(user);
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = service.save(user);
            String uploadDir = "user-photos/"+savedUser.getId();
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
//            FileUploadUtil.cleanDir(uploadDir);
//            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        }
        else {
            if(user.getPhotos().isEmpty()) user.setPhotos(null);
            service.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        String firstPartOfEmail = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+firstPartOfEmail;
    }
    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id,Model model ,RedirectAttributes redirectAttributes )
    {
        try {
            User user = service.get(id);
            List<Role> listRoles = service.listRoles();
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID "+id+")");
            model.addAttribute("listRoles", listRoles);
            return "users/user_form";

        }catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }
    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes)
    {
        try
        {
            service.delete(id);
            String userPhotoDir = "user-photos/" + id;
            AmazonS3Util.removeFolder(userPhotoDir);
            redirectAttributes.addFlashAttribute("message", "The User ID "+id+" has been deleted successfully");
        }catch (UserNotFoundException ex)
        {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }
    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes)
    {
        service.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID "+ id + " has been "+ status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }
    @GetMapping("/users/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserCsvExporter exporter = new UserCsvExporter();
        exporter.export(listUsers, response);
    }
    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> listUsers = service.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(listUsers, response);
    }
    @GetMapping("/users/export/pdf")
    public void exportToPDf(HttpServletResponse response) throws  IOException
    {
        List<User> listUsers = service.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(listUsers, response);
    }
}
