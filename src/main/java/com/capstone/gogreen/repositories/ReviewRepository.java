package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Job;
import com.capstone.gogreen.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Job, Long> {
    Job displayAllReviews(String string);
}
