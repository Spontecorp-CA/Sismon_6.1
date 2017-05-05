package com.sismon.vista.utilities;

import java.io.File;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {

    public final static String doc = "doc";
    public final static String docx = "docx";
    
    public final static String xls = "xls";
    public final static String xlsx = "xlsx";

    /*
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
    
    public static double parseDouble(String numero) throws ParseException {
        String convertido = numero.replaceAll("[^\\d,\\.]++", "");
        if (convertido.matches(".+\\.\\d+,\\d+$")) {
            return Double.parseDouble(convertido.replaceAll("\\.", "").replaceAll(",", "."));
        }

        if (convertido.matches(".+,\\d+\\.\\d+$")) {
            return Double.parseDouble(convertido.replaceAll(",", ""));
        }
        return Double.parseDouble(convertido.replaceAll(",", "."));
    }
    
    public static LocalDate parseToLocalDate(Date date){
        return LocalDateTime
                .ofInstant(date.toInstant(), ZoneId.systemDefault())
                .toLocalDate();
    }
    
}
