package com.capstone.gogreen.models;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "services")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @ManyToMany(mappedBy = "jobServices")
    private List<Job> jobs;

    public Service() {
    }

    public Service(long id, String title, List<Job> jobs) {
        this.id = id;
        this.title = title;
        this.jobs = jobs;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }
}
