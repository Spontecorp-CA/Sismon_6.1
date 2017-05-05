package com.sismon.test;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author jgcastillo
 */
public class CalendarTest {

    public static void main(String[] args) {
        int year = 2016;
        LocalDate dayIn = LocalDate.of(year, Month.FEBRUARY, 26);
        LocalDate dayOut = LocalDate.of(year, Month.MARCH, 5);

        long days = ChronoUnit.DAYS.between(dayIn, dayOut);
        System.out.println("El numero de dias es " + days);
    }
}
