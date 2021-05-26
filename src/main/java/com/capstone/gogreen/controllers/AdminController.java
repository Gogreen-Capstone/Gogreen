package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Controller
public class AdminController {

    @Value("${mapbox.api.key}")
    private String mapBoxKey;

    private final UserRepository usersDao;
    private final JobRepository jobsDao;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationsDao;
    private final ImageRepository imagesDao;

    public AdminController(UserRepository usersDao, JobRepository jobsDao, PasswordEncoder passwordEncoder, LocationRepository locationsDao, ImageRepository imagesDao) {
        this.usersDao = usersDao;
        this.jobsDao = jobsDao;
        this.passwordEncoder = passwordEncoder;
        this.locationsDao = locationsDao;
        this.imagesDao = imagesDao;
    }

//    @Autowired
//    public AdminController(UserRepository usersDao, JobRepository jobsDao, PasswordEncoder passwordEncoder, LocationRepository locationsDao, ImageRepository imagesDao) {
//        this.usersDao = usersDao;
//        this.jobsDao = jobsDao;
//        this.passwordEncoder = passwordEncoder;
//        this.locationsDao = locationsDao;
//        this.imagesDao = imagesDao;
//    }

    @GetMapping("/admin/users")
    public String adminEditUser(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
        model.addAttribute("user", usersDao.getOne(loggedInUser.getId())); //adding that user object
        boolean isAdmin = usersDao.getOne(loggedInUser.getId()).getIsAdmin(); //Getting User according to logged in user and checking isAdmin row
        System.out.println(isAdmin); // Testing hibernate return in stack trace
        // Logic to redirect based off of isAdmin row from User table in db
        if (isAdmin) {
            return "admin/users";
//        } else {
//            return "error/permissions";
//        }
    }


    @GetMapping("/edit/{id}")
    @ResponseBody
    public User getUser(@PathVariable("id") Long id) {
        return usersDao.getOne(id);
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public User deleteUser(@PathVariable("id") Long id) {
        return usersDao.getOne(id);
    }
}
