package com.dg.containers.repository.container;

import com.dg.containers.entity.container.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CellRepository extends JpaRepository<Cell, Long> {


    Optional<Cell> findBySerialNumber(String serialNumber);
}

