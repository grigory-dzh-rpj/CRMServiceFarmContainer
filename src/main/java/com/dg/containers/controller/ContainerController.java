package com.dg.containers.controller;


import com.dg.containers.dto.container.ContainerCreateRequest;
import com.dg.containers.dto.container.PrintLabelsDTO;
import com.dg.containers.entity.container.Container;
import com.dg.containers.service.ContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping("/api/containers")
public class ContainerController {


    @Autowired
    private ContainerService containerService;

    @PostMapping
    @Operation(summary = "Просто создаем контейнер и получаем его данные после создания и JSON с наклейками",
            description = "Отправляем данные, создаем контейнер в базе и получаем наклейки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<?> createContainer(@RequestBody ContainerCreateRequest request) {
        Container container = containerService.createContainer(
                request.getName(),
                request.getLocation(),
                request.getHostIP(),
                request.getNumberOfRacks(),
                request.getNumberOfCellsPerRack()
        );

        PrintLabelsDTO labels = containerService.generateContainerLabels(container);

        return ResponseEntity.ok(new HashMap<String, Object>() {{
            put("container", container);
            put("labels", labels);
        }});
    }


    @PostMapping("/createContainerExcel")
    @Operation(summary = "Создать контейнер и получить Excel -file с QR кодами для расклейки",
            description = "Отправляем данные, создаем контейнер в базе и получаем наклейки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<?> createContainerPDF(@RequestBody ContainerCreateRequest request) {
        Container container = containerService.createContainer(
                request.getName(),
                request.getLocation(),
                request.getHostIP(),
                request.getNumberOfRacks(),
                request.getNumberOfCellsPerRack()
        );

        PrintLabelsDTO labels = containerService.generateContainerLabels(container);

        byte[] excelBytes = containerService.generateExcelWithLabels(labels);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qr_codes.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);

    }



    @GetMapping("/{name}/labels")
    @Operation(summary = "Получаем наклейки созданного контейнера по имени контейнера",
            description = "Отправляем данные, получаем наклейки.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<?> getContainerLabels(@PathVariable String name) {
        try {
            Container container = containerService.findByName(name);
            PrintLabelsDTO labels = containerService.generateContainerLabels(container);
            return ResponseEntity.ok(labels);
        } catch (EntityNotFoundException e) {
            // Возвращаем ResponseEntity с ErrorResponse
            ErrorResponse errorResponse = new ErrorResponse() {
                @Override
                public HttpStatusCode getStatusCode() {
                    return HttpStatus.NOT_FOUND;
                }

                @Override
                public ProblemDetail getBody() {
                    return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Container not found");
                }
            };
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }




}
