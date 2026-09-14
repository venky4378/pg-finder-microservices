package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository extends JpaRepository<Hostel,Long> {
    
}
