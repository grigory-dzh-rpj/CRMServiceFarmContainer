package com.dg.containers.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class BarcodeService {



    public String generateBarcode(String serialNumber) {
        try {
            // Создаем матрицу штрих-кода
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(
                    serialNumber,
                    BarcodeFormat.CODE_128,  // или BarcodeFormat.QR_CODE для QR-кода
                    200,   // ширина
                    100    // высота
            );

            // Создаем BufferedImage
            MatrixToImageWriter.toBufferedImage(bitMatrix);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Конвертируем в Base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating barcode", e);
        }
    }

    // Дополнительный метод для генерации QR-кода
    public String generateQRCode(String serialNumber) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(
                    serialNumber,
                    BarcodeFormat.QR_CODE,
                    400,   // ширина
                    400    // высота
            );

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }


    public String generateQRCodeWithLabel(String serialNumber, int cellNumber) {
        try {
            // Создание QR-кода
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(
                    serialNumber, // Используем только серийный номер в QR-коде
                    BarcodeFormat.QR_CODE,
                    200,   // ширина
                    200    // высота
            );

            // Преобразуем в изображение
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Создаем новое изображение для наклейки
            BufferedImage labelImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = labelImage.createGraphics();

            // Устанавливаем цвет фона и заполняем его
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, labelImage.getWidth(), labelImage.getHeight());

            // Рисуем QR-код на наклейке
            g.drawImage(qrImage, 50, 20, null); // Позиция QR-кода

            // Устанавливаем цвет и шрифт для текста
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 14));

            // Добавляем текстовую информацию
            g.drawString("SN: " + serialNumber, 50, 240);
            g.drawString("#: " + cellNumber, 50, 260);
            g.dispose(); // Завершаем рисование

            // Сохраняем изображение наклейки в байтовый массив
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(labelImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            // Возвращаем изображение в виде строки Base64
            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (WriterException | IOException e) {
            throw new RuntimeException("Error generating QR code with label", e);
        }
    }

    // Метод для настройки параметров штрих-кода
    public String generateCustomBarcode(String serialNumber,
                                        int width,
                                        int height,
                                        BarcodeFormat format) {
        try {
            // Настройки для штрих-кода
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1); // Отступы
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            // Создаем матрицу штрих-кода
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(
                    serialNumber,
                    format,
                    width,
                    height,
                    hints
            );

            // Создаем BufferedImage с настраиваемыми цветами
            MatrixToImageConfig config = new MatrixToImageConfig(
                    0xFF000000,     // Цвет штрих-кода (черный)
                    0xFFFFFFFF      // Цвет фона (белый)
            );

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix, config);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error generating custom barcode", e);
        }
    }

}
