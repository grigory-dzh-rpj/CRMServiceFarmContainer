package com.dg.containers.dto.container;

import lombok.Data;

import java.util.List;

@Data
public class RackLabelDTO {
    private int rackNumber;
    private String rackSerialNumber;
    private String rackBarcode;
    private List<CellLabelDTO> cellLabels;
}
