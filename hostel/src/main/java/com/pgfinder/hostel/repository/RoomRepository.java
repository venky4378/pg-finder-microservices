package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {
    boolean existsByRoomNumberAndHostelId(
            String roomNumber,
            Long hostelId
    );}
