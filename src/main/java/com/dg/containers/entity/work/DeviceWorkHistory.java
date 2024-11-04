package com.dg.containers.entity.work;


import com.dg.containers.entity.container.Device;
import com.dg.containers.entity.container.Cell;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "container_work")
@Data
public class DeviceWorkHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cell_id")
    private Cell cell;

    private String date;

    private String status;

    private String startTime;

    private String endTime;

    private String duration;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;


    @OneToMany(mappedBy = "deviceWorkHistory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetailWork> details = new ArrayList<>();

    private String userId;



}