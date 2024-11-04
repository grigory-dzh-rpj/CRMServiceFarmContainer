package com.dg.containers.entity.work;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "detail_work")
@Data
public class DetailWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private TypeWork typeWork;

    @ManyToOne
    @JoinColumn(name = "device_history_id")
    private DeviceWorkHistory deviceWorkHistory;





}
