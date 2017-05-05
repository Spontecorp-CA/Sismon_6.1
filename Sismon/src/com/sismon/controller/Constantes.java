package com.sismon.controller;

import java.awt.Color;


/**
 * Clase de soporte donde se encuentran todas las constantes que usa el sistema
 * 
 */
public class Constantes {
    
    // referente al status de la Paridad cambiaria
    public static final int PARIDAD_ACTIVA = 1;
    public static final int PARIDAD_VENCIDA = 0;
    
    // referente a las fase de perforacion
    public static final String FASE_MUDANZA_ENTRE_MACOLLAS = "Mudanza entre macollas";
    public static final String FASE_MUDANZA_ENTRE_POZOS = "Mudanza entre pozos";
    public static final String FASE_SUPERFICIAL = "Superficial";
    public static final String FASE_SLANT = "Slant";
    public static final String FASE_PILOTO = "Piloto";
    public static final String FASE_VERTICAL = "Vertical";
    public static final String FASE_INTERMEDIO = "Intermedio";
    public static final String FASE_PRODUCTOR = "Productor";
    public static final String FASE_COMPLETACION = "Completación";
    public static final String FASE_CONEXION = "Conexión";
    public static final String FASE_EVALUACION = "Evaluación";
    
    // referente al color de los mensajes
    public static final Color MENSAJE_ERROR = Color.red;
    public static final Color MENSAJE_INFO = Color.blue;
    public static final Color MENSAJE_WARNING = Color.orange;
    public static final Color MENSAJE_CLEAR = Color.white;
    
    // referentes al tipo de pozo
    public static final String TIPO_PRODUCTOR = "Productor";
    public static final String TIPO_INYECTOR = "Inyector";
    public static final String TIPO_ESTRATIGRAFICO = "Estratigráfico";
    public static final String TIPO_OBSERVADOR = "Observador";
    
    // referentes al status de los taladros
    public static final String TALADRO_STATUS_DISPONIBLE = "Disponible";
    public static final String TALADRO_STATUS_OCUPADO = "Ocupado";
    public static final String TALADRO_STATUS_MANTENIMIENTO = "Mantenimiento";
    public static final String TALADRO_STATUS_NO_DISPONIBLE = "No Disponible";
    public static final String TALADRO_STATUS_DESCONTINUADO = "Descontinuado";
    public static final String TALADRO_STATUS_MUDADO = "Mudado";

    public static final int TALADRO_STATUS_ACTIVO = 1;
    public static final int TALADRO_STATUS_INACTIVO = 0;
    
    // referentes al escenario
    public static final int ESCENARIO_STATUS_ABIERTO = 0;
    public static final int ESCENARIO_STATUS_CIERRE = 1;
    public static final int ESCENARIO_BASE = 0;
    public static final int ESCENARIO_PRUEBA = 1;
    public static final int ESCENARIO_MEJOR_VISION = 2;
    
    // referente a los mantenimientos
    public static final int MANTENIMIENTO_EJECUTADO = 1;
    
    // referente a los reportes
    public static final int REPORTE_INVERSION = 0;
    public static final int REPORTE_PERFORACION = 1;
    public static final int REPORTE_EXPLOTACION = 2;
    public static final int REPORTE_PERFORACION_TOTAL = 3;
    
    public static final int REPORTE_SIN_FILTRO = 0;
    public static final int REPORTE_POR_TALADRO = 1;
    public static final int REPORTE_POR_MACOLLA = 2;
    public static final int REPORTE_POR_POZO = 3;
    public static final int REPORTE_POR_FECHAS = 4;
    
    public final static int HOJAOPERACIONAL_OK = 0;
    public final static int HOJAOPERACIONAL_ERROR = 1;
    public final static int HOJAOPERACIONAL_EN_USO = 2;
    
    // referente a las monedad
    public static final String BOLIVARES = "bs";
    public static final String DOLARES = "usd";
    
    // referente a las pestañas de la forma GestionTaladro2IF
    public static final int TAB_MANTENIMIENTO = 0;
    public static final int TAB_DESCONTINUAR = 1;
    public static final int TAB_MOVER = 2;
    public static final int TAB_AGREGAR = 3;
    public static final int TAB_QUITAR = 4;
    public static final int TAB_ASIGNACION_NUEVOS = 5;
    
    // referente a la modificación de los valores de perforacion
    public static final double PERF_ORIGINAL = 0.0; // como viene del taladro
    public static final double PERF_MODIFICADA = 1.0; // modificada después de perforara
}
