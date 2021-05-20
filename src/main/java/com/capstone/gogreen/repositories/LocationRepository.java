package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
