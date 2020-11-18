package com.gryszko.eventFinder.utils;


import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EventDateFormatter {

    public Date formatStringDateToSQLDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String text = localDate.format(formatters);
        return Date.valueOf(LocalDate.parse(text, formatters));
    }
}
