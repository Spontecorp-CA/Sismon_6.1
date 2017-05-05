package com.sismon.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jgcastillo
 */
public class NumericChar {

    public static void main(String[] args) {
        NumberFormat numberFormat = new DecimalFormat("###,###,###,###,##0.00");
        String value = "ABC";
        
        char valor = 'a';
        System.out.println("valor de " + valor + " es: "+ Character.getNumericValue(valor));
        
        valor = '0';
        System.out.println("el valor de " + valor + " es: " + Character.getNumericValue(valor));
        
        System.out.println("valor es: " + value);
        boolean isOk = true;
        for(char ch : value.toCharArray()){
            if(ch == '\u002C' || ch == '\u002E'){
                continue;
            }
            if(Character.getNumericValue(ch) < 0 || Character.getNumericValue(ch) > 9 ){
                isOk = false;
                break;
            } 
        }
        
        if(isOk){
            try {
                System.out.println("valor válido");
                Number numero = numberFormat.parse(value);
                System.out.println("El valor es " + numero.doubleValue());
            } catch (ParseException ex) {
                Logger.getLogger(NumericChar.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("valor no válido");
        }
    }
}
