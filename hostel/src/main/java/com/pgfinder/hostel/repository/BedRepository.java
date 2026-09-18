package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Bed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed,Long> {
    List<Bed> findByRoomId(Long roomId);

    boolean existsByBedNumberAndRoomId(String bedNumber, Long roomId);
    Optional<Bed> findByIdAndRoomHostelId(Long bedId, Long hostelId);
}
