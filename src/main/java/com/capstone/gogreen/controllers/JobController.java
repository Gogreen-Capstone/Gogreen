package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.*;
import com.capstone.gogreen.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.util.List;

@Controller
public class JobController {

    private final JobRepository jobsDao;
    private final UserRepository usersDao;
    private final LocationRepository locationsDao;
    private final ServiceRepository servicesDao;
    private final ImageRepository imagesDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao, LocationRepository locationsDao, ServiceRepository servicesDao, ImageRepository imagesDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
        this.locationsDao = locationsDao;
        this.servicesDao = servicesDao;
        this.imagesDao = imagesDao;
    }

    @GetMapping("/jobs/create")
    public String showCreateForm(Model model){
        model.addAttribute("job", new Job());
        model.addAttribute("location", new Location());
        model.addAttribute("services", servicesDao.findAll());
        model.addAttribute("image", new Image());
        return "jobs/create";
    }

    @PostMapping("/jobs/create")
    public String saveJob(@ModelAttribute Job job, @ModelAttribute Location location, @ModelAttribute Image image,
                          @RequestParam(name = "houseNumber") int houseNumber,
                          @RequestParam(name = "street") String street,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "state") String state,
                          @RequestParam(name = "zip") int zip,
                          @RequestParam(name = "services")List<Service> services,
                          @RequestParam(name = "image") String imageFile){
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
        image.setReview(false);
        image.setJob(job);
        image.setUrl(imageFile);
        imagesDao.save(image);
        return "redirect:/dashboard";
    }
}
