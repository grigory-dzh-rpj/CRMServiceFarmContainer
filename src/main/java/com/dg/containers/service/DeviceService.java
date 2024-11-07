package com.dg.containers.service;

import com.dg.containers.entity.container.Cell;
import com.dg.containers.entity.container.Device;
import com.dg.containers.repository.container.CellRepository;
import com.dg.containers.repository.container.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CellRepository cellRepository;


    public Device createDevice(Device device) {
        return deviceRepository.save(device);
    }

    @Transactional
    public void attachDeviceToCell(String snDevice, String snCell) {
        Device device = deviceRepository.findBySerialNumber(snDevice)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        Cell cell = cellRepository.findBySerialNumber(snCell)
                .orElseThrow(() -> new EntityNotFoundException("Cell not found"));

        // Проверяем, если ячейка уже занята
        if (cell.getCurrentDevice() != null) {
            throw new IllegalStateException("Ячейка занята другим аппаратом! Освободите ячейку.");
        }

        // Присоединяем устройство к ячейке
        device.setCell(cell);
        cell.setCurrentDevice(device);

        deviceRepository.save(device);
        cellRepository.save(cell);
    }


    @Transactional
    public void detachDeviceFromCell(String snDevice) {
        Device device = deviceRepository.findBySerialNumber(snDevice)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        Cell cell = device.getCell();
        if (cell != null) {

            cell.setCurrentDevice(null);
            device.setCell(null);

            deviceRepository.save(device);
            cellRepository.save(cell);
        }
    }


    public Device findBySerialNumber(String snDevice){
        Device device = deviceRepository.findBySerialNumber(snDevice)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        return device;
    }


}

