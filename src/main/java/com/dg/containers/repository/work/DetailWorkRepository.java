package com.dg.containers.repository.work;

import com.dg.containers.entity.work.DetailWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailWorkRepository extends JpaRepository<DetailWork, Long> {
}
