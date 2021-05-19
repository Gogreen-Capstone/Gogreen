package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobController {

    private JobRepository jobsDao;
    private UserRepository usersDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
    }

    @GetMapping("jobs/create")
    public String showCreateForm(Model model){
        model.addAttribute("job", new Job());
        return "jobs/create";
    }

    @PostMapping("jobs/create")
    public String saveJob(@ModelAttribute Job job){
        System.out.println(job.getScheduledDate());
        System.out.println(job.getScheduledTime());
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
        job.setUser(user); // assigning currently sing user to newly created post
        jobsDao.save(job);
        return "redirect:/dashboard";
    }
}
