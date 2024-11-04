package com.dg.containers.entity.container;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    private String serialNumber;

    private int rackNumber;

    // Каждая стойка связана с одним контейнером
    @ManyToOne
    @JoinColumn(name = "container_id")
    @JsonBackReference
    private Container container;

    // Одна стойка может содержать много ячеек
    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Cell> cells = new ArrayList<>();
}
