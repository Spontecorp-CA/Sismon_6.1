package com.sismon.vista.utilities;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SismonLog {
    public final Logger logger = Logger.getLogger(SismonLog.class.getName());
    private static SismonLog instance = null;

    private SismonLog() {
        try {
            Handler consoleHandler = new ConsoleHandler();
            Handler fileHandler = new FileHandler("sismonlog%u%g.log", 1024 * 1024, 3, true);
            Formatter formatter = new SimpleFormatter();

            consoleHandler.setLevel(Level.FINE);
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.FINE);

            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (IOException | SecurityException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public static SismonLog getInstance() {
        if (instance == null) {
            instance = new SismonLog();
        }
        return instance;
    }
}
