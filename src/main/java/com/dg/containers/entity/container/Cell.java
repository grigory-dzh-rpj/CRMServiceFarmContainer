package com.dg.containers.entity.container;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "cell")
@Data
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cellNumber;

    private String serialNumber;

    // Каждая ячейка принадлежит одной стойке
    @ManyToOne
    @JoinColumn(name = "rack_id")
    @JsonBackReference
    private Rack rack;


    @OneToOne(mappedBy = "cell", cascade = CascadeType.ALL)
    private Device currentDevice; // Текущий аппарат, находящийся в ячейке
}
