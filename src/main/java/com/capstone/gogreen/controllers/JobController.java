package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.*;
import com.capstone.gogreen.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.Errors;

import javax.validation.Valid;
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

    @Value("${file_upload_path}")
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
    public String saveJob(@ModelAttribute Job job,@Valid @ModelAttribute Location location, @ModelAttribute Image image,
                          @RequestParam(name = "houseNumber") int houseNumber,
                          @RequestParam(name = "street") String street,
                          @RequestParam(name = "city") String city,
                          @RequestParam(name = "state") String state,
                          @RequestParam(name = "zip") int zip,
                          @RequestParam(name = "services")List<Service> services,
                          @RequestParam(name = "file") MultipartFile uploadedFile,
                          Errors validation,
                          Model model){
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
        job.setUser(user); // assigning currently signed in user to newly created post
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
        if (validation.hasErrors()) {
            model.addAttribute("errors", validation);
            model.addAttribute("location", location);
            return "jobs/create";
        }
        return "redirect:/dashboard";
    }

    // shows specific job
    @GetMapping("/jobs/show/{id}")
    public String showOneJob(Model model, @PathVariable long id) {
        Job jobToView = jobsDao.getOne(id);
        List<Image> images = imagesDao.findAllByJobId(id);
        model.addAttribute("job", jobToView);
        model.addAttribute("images", images);
        model.addAttribute("services", jobToView.getJobServices());
        return "jobs/show";
    }
    // shows edit form
    @GetMapping("/jobs/edit/{id}")
    public String showEditForm(Model model, @PathVariable long id) {
        Job jobToEdit = jobsDao.getOne(id);
        List<Image> imagesToEdit = imagesDao.findAllByJobId(id);
        Location locationToEdit = jobToEdit.getLocation();
        model.addAttribute("job", jobToEdit);
        model.addAttribute("images", imagesToEdit);
        model.addAttribute("services", servicesDao.findAll());
        model.addAttribute("location", locationToEdit.getId());
        return "jobs/edit";
    }
    // save edit job
    @PostMapping("/jobs/edit/{id}")
    public String saveEditJob(@PathVariable long id, @ModelAttribute Job jobToEdit,@ModelAttribute Location locationToEdit, @ModelAttribute Image imageToEdit,
                              @RequestParam(name = "services") List<Service> services,
                              @RequestParam(name = "file") MultipartFile uploadedFile,
                              @RequestParam(name = "houseNumber") int houseNumber,
                              @RequestParam(name = "street") String street,
                              @RequestParam(name = "city") String city,
                              @RequestParam(name = "state") String state,
                              @RequestParam(name = "zip") int zip,
                              @RequestParam(name = "locationId") long locationId) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
        jobToEdit.setUser(user); // assigning currently signed in user to newly created post
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
        return "redirect:/jobs/show/" + id;
    }

    // delete job
    @PostMapping("/jobs/delete/{id}")
    public String deleteJob(@PathVariable long id) {
        Job jobToDelete = jobsDao.getOne(id);
        List<Service> services = jobToDelete.getJobServices();
        List<Image> images = imagesDao.findAllByJobId(jobToDelete.getId());
        services.clear();
        imagesDao.deleteAll(images);
        jobsDao.deleteById(id);
        return "redirect:/dashboard";
    }

    @GetMapping("/reviews/{id}/create")
    public String showCreatePage(Model model, @PathVariable long id) {
        model.addAttribute("job", jobsDao.getOne(id));
        model.addAttribute("image", new Image());
        return "reviews/create";
    }

    // creating a job review for completed job
    @PostMapping("/reviews/{id}/create")
    public String createJobReview(@ModelAttribute Job job, @ModelAttribute Image image, @RequestParam(name = "isCompleted") boolean isCompleted, @RequestParam(name = "file") MultipartFile uploadedFile) {
        job.setIsCompleted(isCompleted);
        jobsDao.save(job);

        String filename = uploadedFile.getOriginalFilename();
        String filepath = Paths.get(uploadPath, filename).toString();
        File destinationFile = new File(filepath);
        try {
            uploadedFile.transferTo(destinationFile);
            image.setReview(true);
            image.setJob(job);
            image.setUrl("/uploads/" + filename);
            imagesDao.save(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/reviews/{id}/edit")
    public String showEditPage(Model model, @PathVariable long id) {
        List<Image> imagesToEdit = imagesDao.findAllByJobId(id);
        model.addAttribute("job", jobsDao.getOne(id));
        model.addAttribute("images", imagesToEdit);
        return "reviews/edit";
    }

    // editing existing job review
    @PostMapping("/reviews/{id}/edit")
    public String editJobReview(@ModelAttribute Job job, @ModelAttribute Image imageToEdit, @RequestParam(name = "file") MultipartFile uploadedFile) {
        jobsDao.save(job);

        if (!uploadedFile.isEmpty()) {                          // if user is not uploading a different image on the file type input, then this if will not delete their previous uploaded image
            imagesDao.deleteAllByJobId(job.getId());      // this is a custom query in our imagesDao that deletes all images by job_id
        }

        String filename = uploadedFile.getOriginalFilename();
        String filepath = Paths.get(uploadPath, filename).toString();
        File destinationFile = new File(filepath);
        try {
            uploadedFile.transferTo(destinationFile);
            imageToEdit.setReview(true);
            imageToEdit.setJob(job);
            imageToEdit.setUrl("/uploads/" + filename);
            imagesDao.save(imageToEdit);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/reviews/{id}/delete")
    public String delete(@PathVariable long id) {
        Job specificJob = jobsDao.getOne(id);
        specificJob.setReviewTitle(null);
        specificJob.setReviewBody(null);
        specificJob.setIsCompleted(true);
        jobsDao.save(specificJob);
        return "redirect:/dashboard";
    }



}
