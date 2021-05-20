package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
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

    public JobController(JobRepository jobsDao, UserRepository usersDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
    }

//    @GetMapping("jobs/create")
//    public String showCreateForm(Model model){
//        model.addAttribute("job", new Job());
//        return "jobs/create";
//    }

    @PostMapping("/dashboard")
    public String saveJob(@ModelAttribute Job job,
                          @RequestParam(name = "jobTitle") String title,
                          @RequestParam(name = "jobDetails") String details,
                          @RequestParam(name = "scheduledTime") int scheduledTime,
                          @RequestParam(name = "scheduledDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date scheduledDate){
        System.out.println("testing");
//        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = usersDao.getOne(principal.getId());  // getting currently signed in user; our Dao gets all info needed
//        job.setUser(user); // assigning currently sing user to newly created post
//        String str = "2015-03-31";
//        Date date = Date.valueOf(scheduledDate);
//        job.setJobTitle(title);
//        job.setJobDetails(details);
        job.setScheduledTime(scheduledTime);
//        job.setScheduledDate(scheduledDate);
        jobsDao.save(job);
        return "redirect:/dashboard";
    }
}
