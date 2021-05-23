package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByJobId(long id);
}
