package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class AuthenticationController {

    private UserRepository usersDao;
    private PasswordEncoder passwordEncoder;

    public AuthenticationController(UserRepository usersDao, PasswordEncoder passwordEncoder) {
        this.usersDao = usersDao;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "users/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model){
        model.addAttribute("user", new User());
        return "users/register";
    }

    @PostMapping("/register")
    public String registerUser(Model model, @Valid @ModelAttribute User user, Errors validation, @RequestParam(name = "confirm") String confirm){

        String hash = passwordEncoder.encode(user.getPassword());

        // checking to make sure password and confirm password match
        if (!user.getPassword().equals(confirm)){
            validation.rejectValue(
                    "password",
                    "user.password",
                    "Passwords do not match"
            );
        }

        if (validation.hasErrors()){
            model.addAttribute("errors", validation);
            model.addAttribute("user", user);
            return "users/register";
        } else if (usersDao.findByUsername(user.getUsername()) != null && usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            return "users/register";
        } else if (usersDao.findByUsername(user.getUsername()) != null) {
            model.addAttribute("username", user.getUsername());
            return "users/register";
        } else if (usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("email", user.getEmail());
            return "users/register";
        }

        user.setPassword(hash);
        usersDao.save(user);
        return "redirect:/login";
    }
}

