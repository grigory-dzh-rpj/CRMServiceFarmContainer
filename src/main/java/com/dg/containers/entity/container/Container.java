package com.dg.containers.entity.container;


import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "container")
@Data
public class Container {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;


    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rack> racks = new ArrayList<>();



}
