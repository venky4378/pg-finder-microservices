package com.pgfinder.hostel.repository;

import com.pgfinder.hostel.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address,Long> {
}
