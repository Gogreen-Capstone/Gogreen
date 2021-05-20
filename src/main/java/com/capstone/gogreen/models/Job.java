package com.capstone.gogreen.models;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String jobDetails;

    @Column
    private boolean isCompleted;

    @Column
    private String reviewTitle;

    @Column
    private String reviewBody;

    @Column(nullable = false)
//    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private String scheduledDate;

    @Column(nullable = false)
    private int scheduledTime;

    @ManyToOne()
    private User user;

    @OneToOne
    private Location location;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "job_services",
            joinColumns = {@JoinColumn(name = "job_id")},
            inverseJoinColumns = {@JoinColumn(name = "service_id")}
    )

    private List<Service> jobServices;

    public Job() {
    }

    public Job(String jobTitle, String jobDetails, String scheduledDate, int scheduledTime, User user) {
        this.jobTitle = jobTitle;
        this.jobDetails = jobDetails;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.user = user;
    }

    public Job(long id, String jobTitle, String jobDetails, boolean isCompleted, String reviewTitle, String reviewBody, String scheduledDate, int scheduledTime, User user, Location location, List<Service> jobServices) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobDetails = jobDetails;
        this.isCompleted = isCompleted;
        this.reviewTitle = reviewTitle;
        this.reviewBody = reviewBody;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.user = user;
        this.location = location;
        this.jobServices = jobServices;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    public boolean getIsCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public int getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(int scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Service> getJobServices() {
        return jobServices;
    }

    public void setJobServices(List<Service> jobServices) {
        this.jobServices = jobServices;
    }
}
