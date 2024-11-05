package com.dg.containers.controller;

import com.dg.containers.entity.work.DetailWork;
import com.dg.containers.entity.work.DeviceWorkHistory;
import com.dg.containers.service.DeviceWorkHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/devicesWork")
@Tag(name = "DeviceWorkController", description = "API для работы c историей работ над аппаратом")
public class DeviceWorkController {

    @Autowired
    private DeviceWorkHistoryService deviceWorkService;

    // Начало работы устройства
    @PostMapping("/{snDevice}/start")
    @Operation(summary = "Сканируем серийник аппарата и начинаем работу",
            description = "Создает историю работы, записывает время начала работы над аппаратом, имя сотрудника, и дату")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Работа успешна начата"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<String> startWork(
            @PathVariable String snDevice,
            @RequestParam String authorName) {
        try {
            deviceWorkService.startWork(snDevice, authorName);
            return ResponseEntity.ok("Работа устройства начата успешно.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Устройство не найдено.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при запуске работы.");
        }
    }

    @PutMapping("/{snDevice}/finish")
    @Operation(summary = "Завершает последню начатаю работу над аппаратом",
            description = "Принимает snDevice и лист деталей работы -> записывает время конца работы и расчитывает длительность")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Работа успешна начата"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<String> finishWork(
            @PathVariable String snDevice,
            @RequestBody List<DetailWork> detailWorkList) {

        try {
            deviceWorkService.finishWork(snDevice, detailWorkList);
            return ResponseEntity.ok("Работа устройства завершена успешно.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Устройство не найдено.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при завершении работы.");
        }
    }



}

