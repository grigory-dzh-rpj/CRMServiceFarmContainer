package com.dg.containers.dto.container;

import lombok.Data;

@Data
public class ContainerCreateRequest {

    private String name;
    private String location;
    private String hostIP;
    private int numberOfRacks;
    private int numberOfCellsPerRack;

}
