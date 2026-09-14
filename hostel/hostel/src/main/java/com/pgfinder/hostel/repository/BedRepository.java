package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed,Long> {
    List<Bed> findByRoomId(Long roomId);

    boolean existsByBedNumberAndRoomId(String bedNumber, Long roomId);
}
