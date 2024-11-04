package com.dg.containers.repository.container;

import com.dg.containers.entity.container.Rack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RackRepository extends JpaRepository<Rack, Long> {


    Optional<Rack> findBySerialNumber(String serialNumber);
}

