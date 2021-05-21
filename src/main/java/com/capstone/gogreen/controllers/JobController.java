package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobController {
    private final JobRepository jobsDao;
    private final UserRepository usersDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
    }

    @GetMapping("/reviews/{id}/create")
    public String showUserDashboard(Model model, @PathVariable long id) {
        model.addAttribute("job", jobsDao.getOne(id));
        return "reviews/create";
    }

    //Create a job reviews for completed jobs in users dashboard
    @PostMapping("/reviews/{id}/create")
    public String createJobReview(@ModelAttribute Job job) {
        jobsDao.save(job);
        return "redirect:/dashboard";
    }


}
