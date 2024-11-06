package org.ppg.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Date {

    private final int day, month, year;
    private final LocalDate localDate;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
        localDate = LocalDate.of(year, month, day);
    }

    @Override
    public String toString() {
        return "" + day + "/" + month + "/" + year;
    }

    /**
     * @param ComparedDate Fecha a ser comparada
     * 
     * @return Valor de la diferencia en dias
     */
    public int DateDayDifference(Date ComparedDate) {

        return (int) ChronoUnit.DAYS.between(localDate, ComparedDate.getLocalDate());
    }

    /**
     * 
     * @param days Número de dias a sumar o restar
     * 
     * @return Un nuevo objeto {@link Date} con el valor de los días añadidos
     *         o restados
     */
    public Date AddOrSubstractDays(int days) {

        LocalDate newLocalDate;
        Date newDate;

        if (days >= 0) {
            newLocalDate = this.getLocalDate().plusDays(days);
            newDate = new Date(newLocalDate.getDayOfMonth(), newLocalDate.getMonthValue(), newLocalDate.getYear());
        } else {
            newLocalDate = this.getLocalDate().minusDays(days);
            newDate = new Date(newLocalDate.getDayOfMonth(), newLocalDate.getMonthValue(), newLocalDate.getYear());
        }

        return newDate;
    }

    public LocalDate getLocalDate() {

        return localDate;
    }

    public int getDay() {

        return day;
    }

    public boolean isBefore(Date comparedDate) {

        return localDate.isBefore(comparedDate.getLocalDate());
    }


}