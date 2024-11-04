package com.dg.containers.entity.container;

import com.dg.containers.entity.container.Cell;
import com.dg.containers.entity.work.DeviceWorkHistory;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "device")
@Data
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serialNumber;

    private String modelPlat;

    private String ip;

    // Ячейка, в которой находится аппарат
    @OneToOne
    @JoinColumn(name = "cell_id")
    private Cell cell;

    //История работы
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeviceWorkHistory> workHistory = new ArrayList<>();
}
