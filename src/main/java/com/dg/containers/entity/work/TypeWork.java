package com.dg.containers.entity.work;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "type_work")
@Data
public class TypeWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameWork;

}
