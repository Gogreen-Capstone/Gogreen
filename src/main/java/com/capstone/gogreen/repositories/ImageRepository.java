package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByJobId(long id);

    // custom method to delete all images by job_id
    @Modifying
    @Query("delete from Image where job_id = ?1")
    @Transactional
    void deleteAllByJobId(long id);
}
