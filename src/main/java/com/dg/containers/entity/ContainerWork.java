package com.dg.containers.entity;


import com.dg.containers.entity.container.Container;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "container_work")
@Data
public class ContainerWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "container_id")
    private Container container;
}
