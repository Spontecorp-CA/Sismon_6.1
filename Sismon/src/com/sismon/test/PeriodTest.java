package com.sismon.test;

import java.time.LocalDate;
import java.time.Period;

public class PeriodTest {
    public static void main(String[] args) {
        LocalDate ldInicial = LocalDate.of(2017, 2, 1);
        LocalDate ldFin = LocalDate.of(2017, 2, 28);
        
        Period period = Period.between(ldFin, ldInicial);
        
        System.out.println("Hay " + period.getDays() + " dias");
    }
}
