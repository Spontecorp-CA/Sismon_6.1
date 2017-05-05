package com.sismon.vista.utilities;

public enum StatusTaladroEnum {
    DISPONIBLE("Disponible", 0),
    OCUPADO("Ocupado", 1),
    MANTENIMIENTO("En Mantenimiento", 2),
    NO_DISPONIBLE("No Disponible", 3),
    DESCONTINUADO("Descontinuado", 4),
    REASIGNADO("Reasignado", 5);

    private final String statusSt;
    private final int valor;

    private StatusTaladroEnum(String statusSt, int valor) {
        this.statusSt = statusSt;
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return statusSt;
    }
}
