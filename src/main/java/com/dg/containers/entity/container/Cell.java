package com.dg.containers.entity.container;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "cell")
@Data
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cellNumber;

    // Каждая ячейка принадлежит одной стойке
    @ManyToOne
    @JoinColumn(name = "rack_id")
    private Rack rack;
}
