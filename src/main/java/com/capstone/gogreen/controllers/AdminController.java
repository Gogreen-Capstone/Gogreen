package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.*;
import com.capstone.gogreen.repositories.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminController {

    private final UserRepository usersDao;
    private final JobRepository jobsDao;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationsDao;
    private final ImageRepository imagesDao;

    public AdminController(UserRepository usersDao, JobRepository jobsDao, PasswordEncoder passwordEncoder, LocationRepository locationsDao, ImageRepository imagesDao) {
        this.usersDao = usersDao;
        this.jobsDao = jobsDao;
        this.passwordEncoder = passwordEncoder;
        this.locationsDao = locationsDao;
        this.imagesDao = imagesDao;
    }

    //Admin edit user information getMapping
    @GetMapping("/admin/users")
    public String adminEditUser(Model model) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
        usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
        model.addAttribute("user", usersDao.getOne(loggedInUser.getId())); //adding that user object
        boolean isAdmin = usersDao.getOne(loggedInUser.getId()).getIsAdmin(); //Getting User according to logged in user and checking isAdmin row
        if (isAdmin) {
            return "admin/users";
        }

        @PostMapping("/admin/users")
        public String adminEditUser (@Valid @ModelAttribute User user, //Valid attribute is for validation
                @RequestParam(name = "password") String password,
                @RequestParam(name = "username") String username,
                @RequestParam(name = "email") String email,
                @RequestParam(name = "confirm") String confirm,
                Errors validation,
                Model model){ //errors validation also needed for validation
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
            User userId = usersDao.getOne(loggedInUser.getId());
            String hash = passwordEncoder.encode(password);

            // checking to make sure password and confirm password match
            if (!user.getPassword().equals(confirm)) {
                validation.rejectValue(String()

                );
            }

            if (usersDao.getOne(loggedInUser.getId()).getIsAdmin()) {
//            validation.rejectValue(@Value("false"));
                boolean isAdmin = false;

            }
            //Check if username and email is match with some other records from our database
            if (validation.hasErrors()) {
                model.addAttribute("errors", validation);
                model.addAttribute("user", user);
                return "admin/users";
            } else if (usersDao.findByUsername(user.getUsername()) != null && usersDao.findByEmail(user.getEmail()) != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("email", user.getEmail());
                return "admin/dashboard";
            } else if (usersDao.findByUsername(user.getUsername()) != null) {
                model.addAttribute("username", user.getUsername());
                return "admin/dashboard";
            } else if (usersDao.findByEmail(user.getEmail()) != null) {
                model.addAttribute("email", user.getEmail());
                return "admin/dashboard";
            }

            //setting the username email and password
            userId.setPassword(hash);
            userId.setUsername(username);
            userId.setEmail(email);

            //saving those changes to the user
            usersDao.save(userId);
            return "redirect:/dashboard";
        }

        @GetMapping("/admin/jobs")
        public String adminEditJob (Model model){
            User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //Getting logged in user
            usersDao.getOne(loggedInUser.getId()); //getting that user by id of logged in user
            model.addAttribute("user", usersDao.getOne(loggedInUser.getId())); //adding that user object
            boolean isAdmin = usersDao.getOne(loggedInUser.getId()).getIsAdmin(); //Getting User according to logged in user and checking isAdmin row
            if (isAdmin) {
                return "admin/jobs";
            }

            @PostMapping("/jobs/edit/{id}")
            public String saveEditJob ( @PathVariable long id, @ModelAttribute Job jobToEdit,
            @ModelAttribute Location locationToEdit, @ModelAttribute Image imageToEdit,
            @RequestParam(name = "services") List<Service> services,
            @RequestParam(name = "file") MultipartFile uploadedFile,
            @RequestParam(name = "houseNumber") int houseNumber,
            @RequestParam(name = "street") String street,
            @RequestParam(name = "city") String city,
            @RequestParam(name = "state") String state,
            @RequestParam(name = "zip") int zip,
            @RequestParam(name = "locationId") long locationId){
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
            public String deleteJob ( @PathVariable long id){
                Job jobToDelete = jobsDao.getOne(id);
                List<Service> services = jobToDelete.getJobServices();
                List<Image> images = imagesDao.findAllByJobId(jobToDelete.getId());
                services.clear();
                imagesDao.deleteAll(images);
                jobsDao.deleteById(id);
                return "redirect:/dashboard";
            }


            @GetMapping("/reviews/{id}/edit")
            public String showEditPage (Model model,@PathVariable long id){
                model.addAttribute("job", jobsDao.getOne(id));
                return "reviews/edit";
            }

            // editing existing job review
            @PostMapping("/reviews/{id}/edit")
            public String editJobReview (@ModelAttribute Job job){
                jobsDao.save(job);
                return "redirect:/dashboard";
            }

            @PostMapping("/reviews/{id}/delete")
            public String delete ( @PathVariable long id){
                Job specificJob = jobsDao.getOne(id);
                specificJob.setReviewTitle(null);
                specificJob.setReviewBody(null);
                specificJob.setCompleted(true);
                jobsDao.save(specificJob);
                return "redirect:/dashboard";
            }


            @GetMapping("/admin/reviews")
            public String showEditPage (Model model,@PathVariable long id){
                model.addAttribute("job", jobsDao.getOne(id));
                return "admin/jobs";
            }
        }
    }
}