package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.Location;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.ImageRepository;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.LocationRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository usersDao;
    private final JobRepository jobsDao;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationsDao;
    private final ImageRepository imagesDao;

    public UserController(UserRepository usersDao, JobRepository jobsDao, PasswordEncoder passwordEncoder, LocationRepository locationsDao, ImageRepository imagesDao) {
        this.usersDao = usersDao;
        this.jobsDao = jobsDao;
        this.passwordEncoder = passwordEncoder;
        this.locationsDao = locationsDao;
        this.imagesDao = imagesDao;
    }

    //API key for mapbox
    @Value("${mapbox_api_key}")
    private String mapBoxKey;

    // Adding json object to JS
    @RequestMapping(value = "/dashboard.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Job> userLocations() {
        return jobsDao.findAll();
    }



    @GetMapping("/dashboard")
    public String showUserDashboard(Model model, @ModelAttribute Job job) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Job> jobs = jobsDao.findJobsByUserId(loggedInUser.getId());
        model.addAttribute("mapBoxKey", mapBoxKey);
        model.addAttribute("user", loggedInUser);
        model.addAttribute("jobs", jobs); //Getting Job according to logged in user
        boolean isAdmin = usersDao.getOne(loggedInUser.getId()).getIsAdmin(); //Getting User according to logged in user and checking isAdmin row
        // Logic to redirect based off of isAdmin row from User table in db
        if (isAdmin) {
            return "admin/map";
        } else {
            return "users/dashboard";
        }
    }

    //Edit user information getMapping
    @GetMapping("/edit-user")
    public String showEditUser(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
        model.addAttribute("user", usersDao.getOne(loggedInUser.getId())); //adding that user object
        return "users/edit-user";
    }

    //Edit user information postMapping
    @PostMapping("/edit-user")
    public String editUser(@Valid @ModelAttribute User user, //Valid attribute is for validation
                           @RequestParam(name = "password") String password,
                           @RequestParam(name = "username") String username,
                           @RequestParam(name = "email") String email,
                           @RequestParam(name = "confirm") String confirm,
                           Errors validation,
                           Model model) { //errors validation also needed for validation
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        User userId = usersDao.getOne(loggedInUser.getId());
        String hash = passwordEncoder.encode(password);

        // checking to make sure password and confirm password match
        if (!user.getPassword().equals(confirm)) {
            validation.rejectValue(
                    "password",
                    "user.password",
                    "Passwords do not match"
            );
        }

        //Check if username and email is match with some other records from our database
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("user", user);
            return "users/edit-user";
        } else if (usersDao.findByUsername(user.getUsername()) != null && usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            return "users/dashboard";
        } else if (usersDao.findByUsername(user.getUsername()) != null) {
            model.addAttribute("username", user.getUsername());
            return "users/dashboard";
        } else if (usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("email", user.getEmail());
            return "users/dashboard";
        }

        //setting the username email and password
        userId.setPassword(hash);
        userId.setUsername(username);
        userId.setEmail(email);

        //saving those changes to the user
        usersDao.save(userId);
        return "redirect:/dashboard";
    }


}