package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.*;
import com.capstone.gogreen.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import javax.validation.Valid;

@Controller
public class AdminController {

    private final UserRepository usersDao;
    private final JobRepository jobsDao;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationsDao;
    private final ServiceRepository servicesDao;
    private final ImageRepository imagesDao;

    public AdminController(UserRepository usersDao, JobRepository jobsDao, PasswordEncoder passwordEncoder, LocationRepository locationsDao, ServiceRepository servicesDao, ImageRepository imagesDao) {
        this.usersDao = usersDao;
        this.jobsDao = jobsDao;
        this.passwordEncoder = passwordEncoder;
        this.locationsDao = locationsDao;
        this.servicesDao = servicesDao;
        this.imagesDao = imagesDao;
    }

//////////////// USERS ////////////////////////

    // shows specific user
    @GetMapping("/admin/users/show/{id}")
    public String showOneUser(Model model, @PathVariable long id) {
        User userToView = usersDao.getOne(id);
        model.addAttribute("user", userToView);
        model.addAttribute("admin", userToView.getIsAdmin());
        return "admin/users/show";
    }

    // delete user
//    @PostMapping("/admin/users/delete/{id}")
//    public String deleteUser(@PathVariable long id) {
//        User userToDelete = usersDao.getOne(id);
//        List<Job> jobs = jobsDao.findJobsByUserId(id);
//        List<Service> services = jobs.getJobServices();
//        List<Image> images = imagesDao.findAllByJobId(jobToDelete.getId());
//        services.clear();
//        imagesDao.deleteAll(images);
//        jobsDao.deleteAll(jobs);
//        usersDao.deleteById(id);
//        return "redirect:/admin/users";
//    }

    @GetMapping("/admin/users")
    public String adminShowUsers(Model model, @ModelAttribute User user) {
        model.addAttribute("users", usersDao.findAll()); //getting all users to admin view
        return "admin/users/index";
    }

    //Admin edit user information getMapping
    @GetMapping("/admin/users/edit/{id}")
    public String adminEditUser(Model model, @PathVariable long id) {
//        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
//        usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
        model.addAttribute("user", usersDao.getOne(id)); //adding that user object
//        boolean isAdmin = usersDao.getOne(loggedInUser.getId()).getIsAdmin(); //Getting User according to logged in user and checking isAdmin row
        return "admin/users/edit";
    }

    @PostMapping("/admin/users/edit/{id}")
    public String adminEditUser(@PathVariable long id, @Valid @ModelAttribute User user, //Valid attribute is for validation
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "username") String username,
                                @RequestParam(name = "email") String email,
                                @RequestParam(name = "confirm") String confirm,
                                Errors validation,
                                Model model) { //errors validation also needed for validation
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        User userId = usersDao.getOne(id);
        String hash = passwordEncoder.encode(password);

        // checking to make sure password and confirm password match
        if (!user.getPassword().equals(confirm) && user.getIsAdmin()) {
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
            return "admin/users/edit";
        } else if (usersDao.findByUsername(user.getUsername()) != null && usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            return "admin/users";
        } else if (usersDao.findByUsername(user.getUsername()) != null) {
            model.addAttribute("username", user.getUsername());
            return "admin/users";
        } else if (usersDao.findByEmail(user.getEmail()) != null) {
            model.addAttribute("email", user.getEmail());
            return "admin/users";
        }

        //setting the username email and password
        userId.setPassword(hash);
        userId.setUsername(username);
        userId.setEmail(email);

        //saving those changes to the user
        usersDao.save(userId);
        return "redirect:/admin/users";
    }

////////////////// JOBS ////////////////////////

    @Value("${file-upload-path}")
    private String uploadPath;

    // show all jobs
    @GetMapping("/admin/jobs")
    public String showJobsIndex(Model model) {
        model.addAttribute("jobs", jobsDao.findAll());
        return "admin/jobs/index";
    }

    // shows specific job
    @GetMapping("/admin/jobs/show/{id}")
    public String showOneJob(Model model, @PathVariable long id) {
        Job jobToView = jobsDao.getOne(id);
        List<Image> images = imagesDao.findAllByJobId(id);
        model.addAttribute("job", jobToView);
        model.addAttribute("images", images);
        model.addAttribute("services", jobToView.getJobServices());
        return "admin/jobs/show";
    }

    // shows edit form
    @GetMapping("/admin/jobs/edit/{id}")
    public String adminShowEditForm(Model model, @PathVariable long id) {
        Job jobToEdit = jobsDao.getOne(id);
        List<Image> imagesToEdit = imagesDao.findAllByJobId(id);
        Location locationToEdit = jobToEdit.getLocation();
        model.addAttribute("job", jobToEdit);
        model.addAttribute("images", imagesToEdit);
        model.addAttribute("services", servicesDao.findAll());
        model.addAttribute("location", locationToEdit.getId());
        return "admin/jobs/edit";
    }

    // Admin save edit job
    @PostMapping("/admin/jobs/edit/{id}")
    public String adminSaveEditJob(@PathVariable long id, @ModelAttribute Job jobToEdit, @ModelAttribute Location locationToEdit, @ModelAttribute Image imageToEdit,
                                   @RequestParam(name = "services") List<Service> services,
                                   @RequestParam(name = "file") MultipartFile uploadedFile,
                                   @RequestParam(name = "houseNumber") int houseNumber,
                                   @RequestParam(name = "street") String street,
                                   @RequestParam(name = "city") String city,
                                   @RequestParam(name = "state") String state,
                                   @RequestParam(name = "zip") int zip,
                                   @RequestParam(name = "locationId") long locationId,
                                   @RequestParam(name = "user") User user) {
//        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
//        jobToEdit.setUser(usersDao.getOne(jobToEdit.getUser().getId())); // assigning currently signed in user to newly created post
        jobToEdit.setJobServices(services);
        locationToEdit.setHouseNumber(houseNumber);
        locationToEdit.setStreet(street);
        locationToEdit.setCity(city);
        locationToEdit.setState(state);
        locationToEdit.setZipCode(zip);
        locationToEdit.setId(locationId);
        locationsDao.save(locationToEdit);
        jobToEdit.setLocation(locationToEdit);
        jobsDao.save(jobToEdit);

        if (!uploadedFile.isEmpty()) {                          // if user is not uploading a different image on the file type input, then this if will not delete their previous uploaded image
            imagesDao.deleteAllByJobId(jobToEdit.getId());      // this is a custom query in our imagesDao that deletes all images by job_id
        }

        String filename = uploadedFile.getOriginalFilename();
        String filepath = Paths.get(uploadPath, filename).toString();
        File destinationFile = new File(filepath);
        try {
            uploadedFile.transferTo(destinationFile);
            imageToEdit.setReview(false);
            imageToEdit.setJob(jobToEdit);
            imageToEdit.setUrl("/uploads/" + filename);
            imagesDao.save(imageToEdit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/admin/jobs/show/" + id;
    }

    // Admin delete job
    @PostMapping("/admin/jobs/delete/{id}")
    public String adminDeleteJob(@PathVariable long id) {
        Job jobToDelete = jobsDao.getOne(id);
        List<Service> services = jobToDelete.getJobServices();
        List<Image> images = imagesDao.findAllByJobId(jobToDelete.getId());
        services.clear();
        imagesDao.deleteAll(images);
        jobsDao.deleteById(id);
        return "redirect:/admin/jobs";
    }

    // Admin complete job
    @PostMapping("/admin/jobs/complete/{id}")
    public String adminCompleteJob(@PathVariable long id) {
        Job jobToComplete = jobsDao.getOne(id);
        jobToComplete.setIsCompleted(true);
        jobsDao.save(jobToComplete);
        return "redirect:/admin/jobs";

    }

    ////////////////// REVIEWS ////////////////////////

    // show admin index page
    @GetMapping("/admin/reviews")
    public String adminShowReviews(Model model, @ModelAttribute Job job) {
        model.addAttribute("jobs", jobsDao.findAll()); //getting all jobs to admin view
        return "admin/reviews/index";
    }

    // show edit review page
    @GetMapping("/admin/reviews/edit/{id}")
    public String adminShowEditPage(Model model, @PathVariable long id) {
        model.addAttribute("job", jobsDao.getOne(id));
        return "admin/reviews/edit";
    }

    // editing existing job review
    @PostMapping("/admin/reviews/edit/{id}")
    public String adminEditJobReview(@ModelAttribute Job job) {
        jobsDao.save(job);
        return "redirect:/admin/reviews";
    }
    // delete review by admin
    @PostMapping("/admin/reviews/delete/{id}")
    public String adminDeleteReview(@PathVariable long id) {
        Job specificJob = jobsDao.getOne(id);
        specificJob.setReviewTitle(null);
        specificJob.setReviewBody(null);
        specificJob.setIsCompleted(true);
        jobsDao.save(specificJob);
        return "redirect:/admin/reviews";
    }

}