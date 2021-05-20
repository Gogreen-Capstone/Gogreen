package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import com.capstone.gogreen.repositories.JobRepository;
import com.capstone.gogreen.repositories.UserRepository;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;

@Controller
public class JobController {
    private final JobRepository jobsDao;
    private final UserRepository usersDao;

    public JobController(JobRepository jobsDao, UserRepository usersDao) {
        this.jobsDao = jobsDao;
        this.usersDao = usersDao;
    }


    @PostMapping("/modals")
    public String createJobReview(@ModelAttribute Job job,
                                  @RequestParam(name = "reviewTitle") String reviewTitle,
                                  @RequestParam(name = "reviewBody") String reviewBody,
                                  @RequestParam(name = "jobTitle") String jobTitle,
                                  @RequestParam(name = "jobDetails") String jobDetails,
                                  @RequestParam(name = "scheduledTime") int scheduledTime,
                                  @RequestParam(name = "scheduledDate") String scheduledDate,
                                  @RequestParam(name = "jobId") long jobId) {
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = usersDao.getOne(loggedInUser.getId());
        job.setUser(user);
        job.setId(jobId);
        job.setJobTitle(jobTitle);
        job.setJobDetails(jobDetails);
        job.setReviewTitle(reviewTitle);
        job.setReviewBody(reviewBody);
        job.setScheduledTime(scheduledTime);
        job.setScheduledDate(scheduledDate);
        jobsDao.save(job);
        return "redirect:/dashboard";
    }


}
