package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByUserId(long userId);
}
