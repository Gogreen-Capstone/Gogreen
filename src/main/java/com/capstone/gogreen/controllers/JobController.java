package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.Location;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.LocationRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import java.util.Date;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;

@Controller
public class JobController {

    private JobRepository jobsDao;
    private UserRepository usersDao;
    private LocationRepository locationsDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao, LocationRepository locationsDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
        this.locationsDao = locationsDao;
    }

//    @GetMapping("jobs/create")
//    public String showCreateForm(Model model){
//        model.addAttribute("job", new Job());
//        return "jobs/create";
//    }

    @PostMapping("/modals")
    public String saveJob(@ModelAttribute Job job, @ModelAttribute Location location,
                          @RequestParam(name = "houseNumber") int houseNumber,
                          @RequestParam(name = "street") String street,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "state") String state,
                          @RequestParam(name = "zip") int zip){
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
        job.setUser(user); // assigning currently sing user to newly created post
        location.setHouseNumber(houseNumber);
        location.setStreet(street);
        location.setCity(city);
        location.setState(state);
        location.setZipCode(zip);
        locationsDao.save(location);
        job.setLocation(location);
        jobsDao.save(job);
        return "redirect:/dashboard";
    }
}
