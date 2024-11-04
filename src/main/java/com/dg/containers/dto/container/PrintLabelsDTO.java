package com.dg.containers.dto.container;

import lombok.Data;

import java.util.List;

@Data
public class PrintLabelsDTO {
    private String containerName;
    private List<RackLabelDTO> rackLabels;
}
