package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.repositories.ImageRepository;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {
    private final UserRepository userDao;
    private final JobRepository jobsDao;
    private final ImageRepository imagesDao;

    public HomeController(UserRepository userDao, JobRepository jobsDao, ImageRepository imagesDao) {
        this.userDao = userDao;
        this.jobsDao = jobsDao;
        this.imagesDao = imagesDao;
    }

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "GoGreen.works We work for you!");
        model.addAttribute("jobs", jobsDao.findAll());
        model.addAttribute("images", imagesDao.findAll());
        return "home";
    }

    @GetMapping("/home")
    public String adminHome(Model model){
        model.addAttribute("title", "GoGreen.works We work for you!");
        model.addAttribute("jobs", jobsDao.findAll());
        model.addAttribute("images", imagesDao.findAll());
        return "admin/home";
    }

    @GetMapping("/about-us")
    public String aboutUs(Model model) {
        model.addAttribute("title", "About Us");
        return "about_us";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("title", "Services");
        return "services";
    }

}