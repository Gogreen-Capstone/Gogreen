package com.capstone.gogreen.controllers;

//import com.capstone.gogreen.repositories.ReviewRepository;
import com.capstone.gogreen.repositories.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserRepository userDao;
//    private final ReviewRepository reviewDao;

    public HomeController(UserRepository userDao) {
        this.userDao = userDao;
//        this.reviewDao = reviewDao;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "GoGreen.works We work for you!");
        return "home";
    }

    @GetMapping("/about-us")
    public String aboutUs(Model model) {
        model.addAttribute("title", "About Us");
        return "about_us";
    }

}