package com.dg.containers.repository.work;

import com.dg.containers.entity.work.DeviceWorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceWorkHistoryRepository extends JpaRepository<DeviceWorkHistory, Long> {
}

