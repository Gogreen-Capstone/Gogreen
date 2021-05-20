package com.capstone.gogreen.repositories;

import com.capstone.gogreen.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
