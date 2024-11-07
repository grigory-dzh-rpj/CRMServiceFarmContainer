package com.dg.containers.repository.work;

import com.dg.containers.entity.work.TypeWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeWorkRepository extends JpaRepository<TypeWork, Long> {


    Optional<TypeWork> findByNameWork(String nameWork);
}
