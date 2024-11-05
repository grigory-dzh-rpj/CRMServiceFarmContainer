package com.dg.containers.repository.work;

import com.dg.containers.entity.container.Device;
import com.dg.containers.entity.work.DeviceWorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceWorkHistoryRepository extends JpaRepository<DeviceWorkHistory, Long> {

    Optional<DeviceWorkHistory> findTopByDeviceOrderByIdDesc(Device device);
    Optional<DeviceWorkHistory> findTopByDeviceAndEndTimeIsNullOrderByIdDesc(Device device);
}

