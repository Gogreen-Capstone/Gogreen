package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    Job findJobByUserId(long userId);

}
