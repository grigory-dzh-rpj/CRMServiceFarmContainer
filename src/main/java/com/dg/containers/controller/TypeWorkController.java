package com.dg.containers.controller;


import com.dg.containers.entity.container.Device;
import com.dg.containers.entity.work.TypeWork;
import com.dg.containers.service.TypeWorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/typeWork")
@Tag(name = "TypeWorkController", description = "API для работы c типами работ, которые используются для выбора работы на фронте")
public class TypeWorkController {

    @Autowired
    private TypeWorkService typeWorkService;

    @PostMapping("/create")
    @Operation(summary = "Создать новый тип работы",
            description = "Пополняет базу данных новым типом работ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Аппарат успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    public ResponseEntity<TypeWork> createTypeWork(@RequestBody TypeWork typeWork) {
        TypeWork createdTypeWork = typeWorkService.createTypeWork(typeWork);
        return new ResponseEntity<>(createdTypeWork, HttpStatus.CREATED);
    }



    @PostMapping("/delete")
    @Operation(summary = "Удалить тип работы",
            description = "Находит и удаляет тип работы по названиию.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Удалено"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
    })
    public ResponseEntity<String> deleteTypeWork(@RequestBody String typeWorkName){
        typeWorkService.deleteTypeWorkByNameWork(typeWorkName);
        return ResponseEntity.ok("Удалено!");
    }

    @GetMapping("/getAllTypeWorkNames")
    @Operation(summary = "Получить названия всех типов работ",
            description = "Преобразует объекты TypeWork, в список названий работ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно"),
            @ApiResponse(responseCode = "400", description = "Ошибка в предоставленных данных"),
    })
    public ResponseEntity<List<String>> getAllNames(){
        return ResponseEntity.ok(typeWorkService.findAllTypeWorkNames());
    }
}
