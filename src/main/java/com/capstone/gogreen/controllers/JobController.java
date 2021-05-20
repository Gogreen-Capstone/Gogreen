package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.Location;
import com.capstone.gogreen.models.Service;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.LocationRepository;
import com.capstone.gogreen.repositories.ServiceRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class JobController {

    private final JobRepository jobsDao;
    private final UserRepository usersDao;
    private final LocationRepository locationsDao;
    private final ServiceRepository servicesDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao, LocationRepository locationsDao, ServiceRepository servicesDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
        this.locationsDao = locationsDao;
        this.servicesDao = servicesDao;
    }

    @GetMapping("/jobs/create")
    public String showCreateForm(Model model){
        model.addAttribute("job", new Job());
        model.addAttribute("location", new Location());
        model.addAttribute("services", servicesDao.findAll());
        return "jobs/create";
    }

    @PostMapping("/jobs/create")
    public String saveJob(@ModelAttribute Job job, @ModelAttribute Location location,
                          @RequestParam(name = "houseNumber") int houseNumber,
                          @RequestParam(name = "street") String street,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "state") String state,
                          @RequestParam(name = "zip") int zip,
                          @RequestParam(name = "services")List<Service> services){
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
        job.setUser(user); // assigning currently sing user to newly created post
        job.setJobServices(services);
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
