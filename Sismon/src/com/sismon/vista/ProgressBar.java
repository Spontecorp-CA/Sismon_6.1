package com.sismon.vista;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

public class ProgressBar {

    private final Frame component;
    private final JDialog dialog;
    private final JProgressBar progressBar;
    
    public ProgressBar(Frame component) {
        this.component = component;
        this.dialog = new JDialog(component, true);
        progressBar = new JProgressBar(0, 100);
        init();
    }

    private void init(){
        dialog.add(BorderLayout.CENTER, progressBar);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setSize(300, 75);
        dialog.setLocationRelativeTo(component);
    }

    public JDialog getDialog() {
        return dialog;
    }

    public JProgressBar getProgressBar(){
        return progressBar;
    }
}
