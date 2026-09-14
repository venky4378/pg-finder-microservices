package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity,Long> {
}
