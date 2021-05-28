package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findJobByUserId(long userId);
    List<Job> findJobsByUserId(long userId);

    List<Job> findAllJobsByReviewTitleContains(String search);
}
