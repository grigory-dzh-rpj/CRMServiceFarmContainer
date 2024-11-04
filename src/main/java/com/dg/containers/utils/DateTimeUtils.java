package com.dg.containers.utils;

import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    // Форматтер для времени: часы:минуты:секунды (например, 12:10:10)
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Форматтер для даты: год-месяц-день (например, 2024-10-10)
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Метод для форматирования времени
    public static String formatTime(java.time.LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    // Метод для форматирования даты
    public static String formatDate(java.time.LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    // Метод для форматирования даты и времени
    public static String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER) + " " + dateTime.format(TIME_FORMATTER);
    }
}

