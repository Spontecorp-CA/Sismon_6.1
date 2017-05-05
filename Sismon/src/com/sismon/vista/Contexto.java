package com.sismon.vista;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

/**
 *
 * @author jgcastillo
 */
public class Contexto {

    private static JInternalFrame activeFrame;
    private static JFrame mainFrame;
    
    public static void showMessage(String message, Color color){
        MainFrame.setMessage(message, color);
    }

    public static JInternalFrame getActiveFrame() {
        return activeFrame;
    }

    public static void setActiveFrame(JInternalFrame activeFrame) {
        Contexto.activeFrame = activeFrame;
    }

    public static JFrame getMainFrame(){
        return MainFrame.getInstance();
    }
}
