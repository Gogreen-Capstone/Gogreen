package com.capstone.gogreen.controllers;

//import com.capstone.gogreen.repositories.ReviewRepository;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final UserRepository userDao;
    private final JobRepository jobsDao;

    public HomeController(UserRepository userDao, JobRepository jobsDao) {
        this.userDao = userDao;
        this.jobsDao = jobsDao;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "GoGreen.works We work for you!");
        model.addAttribute("jobs", jobsDao.findAll());
        return "home";
    }

    @GetMapping("/about-us")
    public String aboutUs(Model model) {
        model.addAttribute("title", "About Us");
        return "about_us";
    }

    @GetMapping("/how-it-works")
    public String howItWorks(Model model) {
        model.addAttribute("title", "How it Works");
        return "how_it_works";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("title", "Services");
        return "services";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("title", "Reviews");
        return "reviews";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute("title", "Gallery");
        return "gallery";
    }
}