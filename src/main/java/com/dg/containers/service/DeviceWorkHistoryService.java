package com.dg.containers.service;

import com.dg.containers.entity.container.Device;
import com.dg.containers.entity.work.DetailWork;
import com.dg.containers.entity.work.DeviceWorkHistory;
import com.dg.containers.repository.work.DetailWorkRepository;
import com.dg.containers.repository.work.DeviceWorkHistoryRepository;
import com.dg.containers.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.dg.containers.utils.DateTimeUtils.DATE_FORMATTER;
import static com.dg.containers.utils.DateTimeUtils.TIME_FORMATTER;

@Service
public class DeviceWorkHistoryService {

    @Autowired
    private DeviceWorkHistoryRepository deviceWorkHistoryRepository;

    @Autowired
    private DetailWorkRepository detailWorkRepository;

    @Autowired
    private DeviceService deviceService;


    private Map<String, Device> cacheMapWorkDevice = new HashMap<>();

    @Transactional
    public void startWork(String snDevice, String authorName){
        Device device = deviceService.findBySerialNumber(snDevice);

        DeviceWorkHistory deviceWorkHistory = new DeviceWorkHistory();
        deviceWorkHistory.setDevice(device);
        deviceWorkHistory.setDate(LocalDate.now().format(DATE_FORMATTER));
        deviceWorkHistory.setStartTime(LocalTime.now().format(TIME_FORMATTER));
        deviceWorkHistory.setUser(authorName);
        deviceWorkHistoryRepository.save(deviceWorkHistory);

        cacheMapWorkDevice.put(snDevice, device);

    }

    @Transactional
    public void finishWork(String snDevice, List<DetailWork> detailWorkList) {
        Device device = cacheMapWorkDevice.get(snDevice);

        // Если устройства нет в кэше, ищем его в базе данных
        if (device == null) {
            device = deviceService.findBySerialNumber(snDevice);

            // Ищем последнюю запись работы устройства без времени завершения
            Optional<DeviceWorkHistory> unfinishedWork = deviceWorkHistoryRepository.findTopByDeviceAndEndTimeIsNullOrderByIdDesc(device);

            if (unfinishedWork.isPresent()) {
                DeviceWorkHistory workHistory = unfinishedWork.get();
                completeWorkHistory(workHistory, detailWorkList);
            } else {
                throw new IllegalStateException("Незавершенная работа для устройства не найдена!");
            }
        } else {
            // если найдено в кэше
            DeviceWorkHistory lastWorkHistory = deviceWorkHistoryRepository.findTopByDeviceOrderByIdDesc(device)
                    .orElseThrow(() -> new IllegalStateException("История работы устройства не найдена!"));

            completeWorkHistory(lastWorkHistory, detailWorkList);

            // чистим кэш
            cacheMapWorkDevice.remove(snDevice);
        }
    }

    @Transactional
    public void completeWorkHistory(DeviceWorkHistory workHistory, List<DetailWork> details) {
        workHistory.setEndTime(LocalTime.now().format(TIME_FORMATTER));

        for (DetailWork detail : details) {
            // Создаем новую запись детали работы
            DetailWork newDetailWork = new DetailWork();

            newDetailWork.setNameWork(detail.getNameWork());
            newDetailWork.setDescription(detail.getDescription() != null ? detail.getDescription() : "-");
            newDetailWork.setDeviceWorkHistory(workHistory);

            // Сохраняем деталь работы в базе данных
            detailWorkRepository.save(newDetailWork);
        }

        LocalTime startTime = LocalTime.parse(workHistory.getStartTime(), TIME_FORMATTER);
        LocalTime endTime = LocalTime.parse(workHistory.getEndTime(), TIME_FORMATTER);
        Duration duration = Duration.between(startTime, endTime);

        String formattedDuration = String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart()
        );
        workHistory.setDuration(formattedDuration);

        deviceWorkHistoryRepository.save(workHistory);
    }

}
