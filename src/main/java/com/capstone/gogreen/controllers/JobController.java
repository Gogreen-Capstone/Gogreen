package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.*;
import com.capstone.gogreen.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

    @Value("${file-upload-path}")
    private String uploadPath;

    @GetMapping("/jobs/create")
    public String showCreateForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("location", new Location());
        model.addAttribute("services", servicesDao.findAll());
        model.addAttribute("image", new Image());
        return "jobs/create";
    }
    // creates new job
    @PostMapping("/jobs/create")
    public String saveJob(@ModelAttribute Job job, @ModelAttribute Location location, @ModelAttribute Image image,
                          @RequestParam(name = "houseNumber") int houseNumber,
                          @RequestParam(name = "street") String street,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "state") String state,
                          @RequestParam(name = "zip") int zip,
                          @RequestParam(name = "services")List<Service> services,
                          @RequestParam(name = "file") MultipartFile uploadedFile){
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

        String filename = uploadedFile.getOriginalFilename();
        String filepath = Paths.get(uploadPath, filename).toString();
        File destinationFile = new File(filepath);
        try {
            uploadedFile.transferTo(destinationFile);
            image.setReview(false);
            image.setJob(job);
            image.setUrl("/uploads/" + filename);
            imagesDao.save(image);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "redirect:/dashboard";
    }
    // shows specific job
    @GetMapping("/jobs/{id}/show")
    public String showOneJob(Model model, @PathVariable long id) {
        Job jobToView = jobsDao.getOne(id);
        Image jobImage = imagesDao.findByJobId(id);
        model.addAttribute("job", jobToView);
        model.addAttribute("image", jobImage);
        return "jobs/show";
    }
    // shows edit form
    @GetMapping("/jobs/{id}/edit")
    public String showEditForm(Model model, @PathVariable long id) {
        Job jobToEdit = jobsDao.getOne(id);
        model.addAttribute("job", jobToEdit);
        return "jobs/edit";
    }
    // edit job
    @PostMapping("/jobs/{id}/edit")
    public String saveEditJob(@PathVariable long id, @ModelAttribute Job jobToEdit) {

        return "";
    }

    @GetMapping("/reviews/{id}/create")
    public String showCreatePage(Model model, @PathVariable long id) {
        model.addAttribute("job", jobsDao.getOne(id));
        return "reviews/create";
    }

    // creating a job review for completed job
    @PostMapping("/reviews/{id}/create")
    public String createJobReview(@ModelAttribute Job job) {
        jobsDao.save(job);
        return "redirect:/dashboard";
    }

    @GetMapping("/reviews/{id}/edit")
    public String showEditPage(Model model, @PathVariable long id) {
        model.addAttribute("job", jobsDao.getOne(id));
        return "reviews/edit";
    }

    // editing existing job review
    @PostMapping("/reviews/{id}/edit")
    public String editJobReview(@ModelAttribute Job job) {
        jobsDao.save(job);
        return "redirect:/dashboard";
    }

    @PostMapping("/reviews/{id}/delete")
    public String delete(@PathVariable long id) {
        Job specificJob = jobsDao.getOne(id);
        specificJob.setReviewTitle(null);
        specificJob.setReviewBody(null);
        specificJob.setCompleted(true);
        jobsDao.save(specificJob);
        return "redirect:/dashboard";
    }


}
