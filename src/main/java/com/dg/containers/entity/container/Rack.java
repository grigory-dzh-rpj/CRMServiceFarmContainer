package com.dg.containers.entity.container;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rack")
@Data
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rackNumber;

    // Каждая стойка связана с одним контейнером
    @ManyToOne
    @JoinColumn(name = "container_id")
    private Container container;

    // Одна стойка может содержать много ячеек
    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cell> cells = new ArrayList<>();
}
