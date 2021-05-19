package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.UserRepository;
import com.capstone.gogreen.services.UserDetailsLoader;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class UserController {

    private UserRepository usersDao;
    private PasswordEncoder passwordEncoder;

    public UserController(UserRepository usersDao, PasswordEncoder passwordEncoder) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String showUserDashboard(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", loggedInUser);
        return "users/dashboard";
    }

    @GetMapping("/edit-user")
    public String showEditUser(Model model){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
        model.addAttribute("user", usersDao.getOne(loggedInUser.getId())); //adding that user object
        return "users/edit-user";
    }

    @PostMapping("/edit-user")
    public String editUser(@Valid @ModelAttribute User user,
                           @RequestParam(name="password")String password,
                           @RequestParam(name = "username")String username,
                           @RequestParam(name = "email")String email,
                           Errors validation){
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        User userId = usersDao.getOne(loggedInUser.getId());
        String hash = passwordEncoder.encode(password);

        userId.setPassword(hash);
        userId.setUsername(username);
        userId.setEmail(email);
        usersDao.save(userId);
        return "users/edit-user";
    }




}

