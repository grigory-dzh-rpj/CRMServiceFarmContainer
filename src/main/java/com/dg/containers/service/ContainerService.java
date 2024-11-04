package com.dg.containers.service;

import com.dg.containers.dto.container.CellLabelDTO;
import com.dg.containers.dto.container.PrintLabelsDTO;
import com.dg.containers.dto.container.RackLabelDTO;
import com.dg.containers.entity.container.Cell;
import com.dg.containers.entity.container.Container;
import com.dg.containers.entity.container.Rack;
import com.dg.containers.repository.container.ContainerRepository;
import com.dg.containers.repository.container.RackRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ContainerService {

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private RackRepository rackRepository;

    @Autowired
    private BarcodeService barcodeService;


    public Container createContainer(String name, String location, String hostIP,
                                     int numberOfRacks, int numberOfCellsPerRack) {
        Container container = new Container();
        container.setName(name);
        container.setLocation(location);
        container.setHostIP(hostIP);

        for (int i = 0; i < numberOfRacks; i++) {
            Rack rack = new Rack();
            rack.setContainer(container);
            rack.setRackNumber(i + 1);
            // Генерируем серийный номер для стойки
            String rackSerialNumber = String.format("%s-R%02d",
                    name.replaceAll("\\s+", ""),
                    (i + 1));
            rack.setSerialNumber(rackSerialNumber);

            List<Cell> cells = new ArrayList<>();

            for (int j = 0; j < numberOfCellsPerRack; j++) {
                Cell cell = new Cell();
                cell.setCellNumber(j + 1);
                cell.setRack(rack);

                String cellSerialNumber = String.format("%s-C%02d",
                        rackSerialNumber,
                        (j + 1));
                cell.setSerialNumber(cellSerialNumber);

                cells.add(cell);
            }

            rack.setCells(cells);
            container.getRacks().add(rack);
        }

        return containerRepository.save(container);
    }

    // Метод для поиска стойки по серийному номеру
    public Rack findRackBySerialNumber(String serialNumber) {
        return rackRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Rack not found with SN: " + serialNumber));
    }


    public String printContainerStructure(String containerName) {
        Container container = containerRepository.findByName(containerName)
                .orElseThrow(() -> new EntityNotFoundException("Container not found: " + containerName));

        StringBuilder result = new StringBuilder();
        result.append(String.format("Container: %s (Location: %s)%n",
                container.getName(), container.getLocation()));

        for (Rack rack : container.getRacks()) {
            result.append(String.format("  Rack %d [SN: %s]:%n",
                    rack.getRackNumber(),
                    rack.getSerialNumber()));

            for (Cell cell : rack.getCells()) {
                result.append(String.format("    Cell: %s%n",
                        cell.getSerialNumber()));
            }
        }

        return result.toString();
    }



    public PrintLabelsDTO generateContainerLabels(Container container) {
        PrintLabelsDTO labelsDTO = new PrintLabelsDTO();
        labelsDTO.setContainerName(container.getName());
        List<RackLabelDTO> rackLabels = new ArrayList<>();

        for (Rack rack : container.getRacks()) {
            RackLabelDTO rackLabel = new RackLabelDTO();
            rackLabel.setRackNumber(rack.getRackNumber());
            rackLabel.setRackSerialNumber(rack.getSerialNumber());
            rackLabel.setRackBarcode(barcodeService.generateQRCode(rack.getSerialNumber()));

            List<CellLabelDTO> cellLabels = new ArrayList<>();

            for (Cell cell : rack.getCells()) {
                CellLabelDTO cellLabel = new CellLabelDTO();
                cellLabel.setCellNumber(cell.getCellNumber());
                cellLabel.setCellSerialNumber(cell.getSerialNumber());
                cellLabel.setCellBarcode(barcodeService.generateQRCodeWithLabel(cell.getSerialNumber(), cell.getCellNumber()));
                cellLabels.add(cellLabel);
            }

            rackLabel.setCellLabels(cellLabels);
            rackLabels.add(rackLabel);
        }

        labelsDTO.setRackLabels(rackLabels);
        return labelsDTO;
    }





    public byte[] generateExcelWithLabels(PrintLabelsDTO labels) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Workbook workbook = new XSSFWorkbook();

            Font font = workbook.createFont();
            font.setFontName("Arial"); // Arial поддерживает кириллицу
            font.setFontHeightInPoints((short) 12);


            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(font);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);

            // Перебираем все стойки и добавляем QR-коды в Excel
            for (RackLabelDTO rackLabel : labels.getRackLabels()) {
                Sheet sheet = workbook.createSheet("Стойка " + rackLabel.getRackNumber());


                sheet.setColumnWidth(0, 6000); // Для QR-кода
                sheet.setColumnWidth(1, 4000); // Для серийного номера


                byte[] rackQrCodeBytes = Base64.getDecoder().decode(rackLabel.getRackBarcode());
                int pictureIdx = workbook.addPicture(rackQrCodeBytes, Workbook.PICTURE_TYPE_PNG);
                Drawing<?> drawing = sheet.createDrawingPatriarch();
                CreationHelper helper = workbook.getCreationHelper();
                ClientAnchor anchor = helper.createClientAnchor();

                anchor.setCol1(0);
                anchor.setRow1(0);
                anchor.setCol2(1);
                anchor.setRow2(5);

                drawing.createPicture(anchor, pictureIdx);

                Row rackRow = sheet.createRow(0);
                org.apache.poi.ss.usermodel.Cell rackNumberCell = rackRow.createCell(1);
                rackNumberCell.setCellValue("Номер стойки: " + rackLabel.getRackNumber());
                rackNumberCell.setCellStyle(cellStyle);

                int rowNum = 5;


                for (CellLabelDTO cellLabel : rackLabel.getCellLabels()) {

                    byte[] cellQrCodeBytes = Base64.getDecoder().decode(cellLabel.getCellBarcode());
                    pictureIdx = workbook.addPicture(cellQrCodeBytes, Workbook.PICTURE_TYPE_PNG);


                    anchor = helper.createClientAnchor();
                    anchor.setCol1(0);
                    anchor.setRow1(rowNum);
                    anchor.setCol2(1);
                    anchor.setRow2(rowNum + 5);


                    drawing.createPicture(anchor, pictureIdx);


                    Row cellRow = sheet.createRow(rowNum);
                    org.apache.poi.ss.usermodel.Cell serialNumberCell = cellRow.createCell(1);
                    serialNumberCell.setCellValue(cellLabel.getCellSerialNumber());
                    serialNumberCell.setCellStyle(cellStyle);


                    CellStyle cellBorderStyle = workbook.createCellStyle();
                    cellBorderStyle.cloneStyleFrom(cellStyle);
                    cellBorderStyle.setBorderTop(BorderStyle.THIN);
                    cellBorderStyle.setBorderBottom(BorderStyle.THIN);
                    cellBorderStyle.setBorderLeft(BorderStyle.THIN);
                    cellBorderStyle.setBorderRight(BorderStyle.THIN);

                    serialNumberCell.setCellStyle(cellBorderStyle);

                    rowNum += 6;
                }
            }

            workbook.write(byteArrayOutputStream);
            workbook.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while generating Excel", e);
        }
    }





    public Container findByName(String name) {
        Container container = containerRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Container not found"));
        return container;
    }
}

