package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByUserId(long userId);

//    @Query("DELETE FROM Job j WHERE j.reviewTitle = : review_title AND  j.reviewBody = : review_body")
//    void deleteReview(@Param("review_title") String review_title,@Param("review_body") String review_body,@Param("id") long id);
}
