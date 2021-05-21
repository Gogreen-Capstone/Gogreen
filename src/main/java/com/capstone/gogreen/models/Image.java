package com.capstone.gogreen.models;

import javax.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 1000)
    private String url;

    @Column(nullable = false)
    private boolean isReview;

    @ManyToOne()
    private Job job;

    public Image() {
    }

    public Image(long id, String url, boolean isReview) {
        this.id = id;
        this.url = url;
        this.isReview = isReview;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean getIsReview() {
        return isReview;
    }

    public void setReview(boolean review) {
        isReview = review;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
}
