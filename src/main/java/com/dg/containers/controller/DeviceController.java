package com.dg.containers.controller;

import com.dg.containers.entity.container.Device;
import com.dg.containers.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@Tag(name = "DeviceController", description = "API для работы с аппаратами")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    // Метод для прикрепления аппарата к ячейке
    @PostMapping("/{snDevice}/attach/{snCell}")
    @Operation(summary = "Прикрепление аппарата к ячееке",
            description = "Сканируем ячейку прикрепляем аппарат, если ячейка не занята.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аппарат успешно прикреплен"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<String> attachDeviceToCell(@PathVariable String snDevice, @PathVariable String snCell) {
        try {
            deviceService.attachDeviceToCell(snDevice, snCell);
            return ResponseEntity.ok("Аппарат успешно прикреплен.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Метод для отвязывания аппарата от ячейки
    @PostMapping("/{snDevice}/detach")
    public ResponseEntity<String> detachDeviceFromCell(@PathVariable String snDevice) {
        try {
            deviceService.detachDeviceFromCell(snDevice);
            return ResponseEntity.ok("Device detached from cell successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




    @PostMapping("/create")
    @Operation(summary = "Создать новый аппарат",
            description = "Пополняет базу данных новым аппаратом.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Аппарат успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<Device> createCategory(@RequestBody Device device) {
        Device savedDevice = deviceService.createDevice(device);
        return new ResponseEntity<>(device, HttpStatus.CREATED);
    }
}

