package com.sismon.vista;

import com.sismon.controller.Constantes;
import com.sismon.vista.configuracion.AsignaTaladrosaFilaIF;
import com.sismon.vista.configuracion.AsignarPozosAFilasIF;
import com.sismon.vista.configuracion.CargaInicialTaladrosIF;
import com.sismon.vista.configuracion.ConfiguraCampoIF;
import com.sismon.vista.configuracion.ConfiguraDistritoIF;
import com.sismon.vista.configuracion.ConfiguraParidadIF;
import com.sismon.vista.configuracion.ConfiguraSecuenciaMacollasIF;
import com.sismon.vista.configuracion.GenPerfBaseIF;
import com.sismon.vista.configuracion.SecPerfoPozoIF;
import com.sismon.vista.escenario.AgregarPozosIF;
import com.sismon.vista.escenario.ExplotacionEscenarioIF;
import com.sismon.vista.escenario.GestionEscenarioIF;

import com.sismon.vista.escenario.GestionFaseTaladrosIF;
import com.sismon.vista.escenario.GestionPozosIF;

import com.sismon.vista.escenario.GestionSecPerforacionIF;
import com.sismon.vista.escenario.GestionSecuenciaMacollasIF;
import com.sismon.vista.escenario.GestionTaladros2IF;
import com.sismon.vista.escenario.GestionTiempoCostoPerfIF;
import com.sismon.vista.escenario.PerforacionEscenariolIF;
import com.sismon.vista.mejorvision.GestionMejorVisionIF;
import com.sismon.vista.reporte.RepExplotacionIF;
import com.sismon.vista.reporte.RepHojaOperacionalIF;
import com.sismon.vista.reporte.RepInversionIF;
import com.sismon.vista.utilities.SismonLog;
import com.sismon.vista.utilities.VistaUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;


public class MainFrame extends javax.swing.JFrame {

    private static final SismonLog sismonlog = SismonLog.getInstance();
    private final ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
                            .getImage(getClass().getResource("/resources/LogoSismonInferior.png")));
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    private static MainFrame instance;
    
    public MainFrame() {
        super("Sismon");
        initComponents();
        init();
        sismonlog.logger.log(Level.INFO, "Arrancando la aplicaci\u00f3n {0}", dateFormat.format(new Date()));
    }
    
    private void init(){
        this.setLocationRelativeTo(null);
        this.setIconImage(icon.getImage());
        instance = this;
        
        messageLabel.setText(null);
    }
    
    public static MainFrame getInstance(){
        return instance;
    }
    
    private void openForm2(final JInternalFrame iframe){
        SwingUtilities.invokeLater(() ->{
            Dimension desktopSize = desktopPane.getSize();
            if (iframe.getParent() == null) {
                Dimension iframeSize = iframe.getSize();
                desktopPane.add(iframe);
                iframe.setLocation((desktopSize.width - iframeSize.width) / 2,
                        (desktopSize.height - iframeSize.height) / 2);
            }
            iframe.setVisible(true);
            desktopPane.moveToFront(iframe);
            try {
                iframe.setSelected(true);
            } catch (PropertyVetoException e) {
                sismonlog.logger.log(Level.SEVERE, "", e);
            }
        });
    }
    
    private void openForm(final JInternalFrame iframe) {
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Dimension desktopSize = desktopPane.getSize();
                if (iframe.getParent() == null) {
                    Dimension iframeSize = iframe.getSize();
                    desktopPane.add(iframe);
                    iframe.setLocation((desktopSize.width - iframeSize.width) / 2,
                            (desktopSize.height - iframeSize.height) / 2);
                }
                iframe.setVisible(true);
                desktopPane.moveToFront(iframe);
                try {
                    iframe.setSelected(true);
                } catch (PropertyVetoException e) {
                    sismonlog.logger.log(Level.SEVERE, "", e);
                }
                return null;
            }
        };
        worker.execute();
    }
    
    protected static void setMessage(String message, Color color){
        messageLabel.setText(message);
        messageLabel.setForeground(color);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane(){
            private BufferedImage image;
            {
                try{
                    image = ImageIO.read(getClass().getResource("/resources/Sismon-backgrounds.png"));
                } catch(IOException e){
                    sismonlog.logger.log(Level.WARNING, "No pudo cargar la imagen", e);
                }
            }

            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        statusPanel = new javax.swing.JPanel();
        messageLabel = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        archivoMenu = new javax.swing.JMenu();
        salirMenuItem = new javax.swing.JMenuItem();
        configuraCampoMenu = new javax.swing.JMenu();
        paridadMenuItem = new javax.swing.JMenuItem();
        distritoMenuItem = new javax.swing.JMenuItem();
        configuraCampoMenuItem = new javax.swing.JMenuItem();
        configTaladrosMenuItem = new javax.swing.JMenuItem();
        orderMacollasMenuItem = new javax.swing.JMenuItem();
        asignarPozosAFilasMenuItem = new javax.swing.JMenuItem();
        secPerfoPozosMenuItem = new javax.swing.JMenuItem();
        asignaTaladrosFilaMenuItem = new javax.swing.JMenuItem();
        genPerfBaseMenuItem = new javax.swing.JMenuItem();
        escenariosMenu = new javax.swing.JMenu();
        gestionEscenariosMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        gestionTaladrosMenuItem = new javax.swing.JMenuItem();
        editarFasesMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        ordenMacollasMenuItem = new javax.swing.JMenuItem();
        agregarPozoMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        variacionesPozoMenuItem = new javax.swing.JMenuItem();
        secuenciaPerfMenuItem = new javax.swing.JMenuItem();
        tiemposCostoMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        perforacionEscMenuItem = new javax.swing.JMenuItem();
        explotacionEscMenuItem = new javax.swing.JMenuItem();
        gestionMVMenuItem = new javax.swing.JMenuItem();
        reportesMenu = new javax.swing.JMenu();
        repInversionMenuItem = new javax.swing.JMenuItem();
        repPerforacionMenuItem = new javax.swing.JMenuItem();
        repProduccionMenuItem = new javax.swing.JMenuItem();
        hojaOperacionalMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                onWindowOpened(evt);
            }
        });

        javax.swing.GroupLayout desktopPaneLayout = new javax.swing.GroupLayout(desktopPane);
        desktopPane.setLayout(desktopPaneLayout);
        desktopPaneLayout.setHorizontalGroup(
            desktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        desktopPaneLayout.setVerticalGroup(
            desktopPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );

        statusPanel.setBackground(new java.awt.Color(255, 255, 255));

        messageLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        messageLabel.setText("jLabel1");

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(messageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 866, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(326, Short.MAX_VALUE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(messageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        archivoMenu.setText("Archivo");

        salirMenuItem.setText("Salir");
        salirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salirMenuItemActionPerformed(evt);
            }
        });
        archivoMenu.add(salirMenuItem);

        jMenuBar1.add(archivoMenu);

        configuraCampoMenu.setText("Configuración");

        paridadMenuItem.setText("Paridad cambiaria");
        paridadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paridadMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(paridadMenuItem);

        distritoMenuItem.setText("Distrito / Empresa Mixta");
        distritoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                distritoMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(distritoMenuItem);

        configuraCampoMenuItem.setText("Campo");
        configuraCampoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configuraCampoMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(configuraCampoMenuItem);

        configTaladrosMenuItem.setText("Taladros");
        configTaladrosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configTaladrosMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(configTaladrosMenuItem);

        orderMacollasMenuItem.setText("Orden Perforación Macollas");
        orderMacollasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderMacollasMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(orderMacollasMenuItem);

        asignarPozosAFilasMenuItem.setText("Asignar Pozos a Filas");
        asignarPozosAFilasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignarPozosAFilasMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(asignarPozosAFilasMenuItem);

        secPerfoPozosMenuItem.setText("Secuencia de Perforación de Pozos");
        secPerfoPozosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secPerfoPozosMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(secPerfoPozosMenuItem);

        asignaTaladrosFilaMenuItem.setText("Asignar Taladros a Filas");
        asignaTaladrosFilaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                asignaTaladrosFilaMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(asignaTaladrosFilaMenuItem);

        genPerfBaseMenuItem.setText("Generar Perforación Base");
        genPerfBaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genPerfBaseMenuItemActionPerformed(evt);
            }
        });
        configuraCampoMenu.add(genPerfBaseMenuItem);

        jMenuBar1.add(configuraCampoMenu);

        escenariosMenu.setText("Escenarios");

        gestionEscenariosMenuItem.setText("Gestión Escenarios");
        gestionEscenariosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestionEscenariosMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(gestionEscenariosMenuItem);
        escenariosMenu.add(jSeparator1);

        gestionTaladrosMenuItem.setText("Gestión de Taladros");
        gestionTaladrosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestionTaladrosMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(gestionTaladrosMenuItem);

        editarFasesMenuItem.setText("Gestión de Fases de Taladros");
        editarFasesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarFasesMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(editarFasesMenuItem);
        escenariosMenu.add(jSeparator2);

        ordenMacollasMenuItem.setText("Orden de Macollas");
        ordenMacollasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordenMacollasMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(ordenMacollasMenuItem);

        agregarPozoMenuItem.setText("Agregar / Eliminar Pozo en Macolla");
        agregarPozoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarPozoMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(agregarPozoMenuItem);
        escenariosMenu.add(jSeparator3);

        variacionesPozoMenuItem.setText("Editar Características de Pozos");
        variacionesPozoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variacionesPozoMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(variacionesPozoMenuItem);

        secuenciaPerfMenuItem.setText("Secuencia de Perforación de Pozos");
        secuenciaPerfMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secuenciaPerfMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(secuenciaPerfMenuItem);

        tiemposCostoMenuItem.setText("Tiempos y Costos de Perforación");
        tiemposCostoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tiemposCostoMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(tiemposCostoMenuItem);
        escenariosMenu.add(jSeparator4);

        perforacionEscMenuItem.setText("Perforación de Escenario");
        perforacionEscMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                perforacionEscMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(perforacionEscMenuItem);

        explotacionEscMenuItem.setText("Explotación de Escenario");
        explotacionEscMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                explotacionEscMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(explotacionEscMenuItem);

        gestionMVMenuItem.setText("Gestión Mejor Visión");
        gestionMVMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gestionMVMenuItemActionPerformed(evt);
            }
        });
        escenariosMenu.add(gestionMVMenuItem);

        jMenuBar1.add(escenariosMenu);

        reportesMenu.setText("Reportes");

        repInversionMenuItem.setText("Inversión");
        repInversionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repInversionMenuItemActionPerformed(evt);
            }
        });
        reportesMenu.add(repInversionMenuItem);

        repPerforacionMenuItem.setText("Perforación (Conteo)");
        repPerforacionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repPerforacionMenuItemActionPerformed(evt);
            }
        });
        reportesMenu.add(repPerforacionMenuItem);

        repProduccionMenuItem.setText("Producción");
        repProduccionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repProduccionMenuItemActionPerformed(evt);
            }
        });
        reportesMenu.add(repProduccionMenuItem);

        hojaOperacionalMenuItem.setText("Hoja Operacional");
        hojaOperacionalMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hojaOperacionalMenuItemActionPerformed(evt);
            }
        });
        reportesMenu.add(hojaOperacionalMenuItem);

        jMenuBar1.add(reportesMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(desktopPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktopPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void salirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salirMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_salirMenuItemActionPerformed

    private void distritoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_distritoMenuItemActionPerformed
        ConfiguraDistritoIF configuraDistritoIF = ConfiguraDistritoIF.getInstance();
        openForm2(configuraDistritoIF);
    }//GEN-LAST:event_distritoMenuItemActionPerformed

    private void configuraCampoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configuraCampoMenuItemActionPerformed
        ConfiguraCampoIF configuraCampoIF = ConfiguraCampoIF.getInstance();
        openForm2(configuraCampoIF);
    }//GEN-LAST:event_configuraCampoMenuItemActionPerformed

    private void paridadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paridadMenuItemActionPerformed
        ConfiguraParidadIF configParidadIF = ConfiguraParidadIF.getInstance();
        openForm2(configParidadIF);
    }//GEN-LAST:event_paridadMenuItemActionPerformed

    private void configTaladrosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configTaladrosMenuItemActionPerformed
        CargaInicialTaladrosIF cargaTaladrosIF = CargaInicialTaladrosIF.getInstance();
        openForm2(cargaTaladrosIF);
    }//GEN-LAST:event_configTaladrosMenuItemActionPerformed

    private void orderMacollasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderMacollasMenuItemActionPerformed
        ConfiguraSecuenciaMacollasIF ordenMacollasIF = ConfiguraSecuenciaMacollasIF.getInstance();
        openForm2(ordenMacollasIF);
    }//GEN-LAST:event_orderMacollasMenuItemActionPerformed

    private void asignarPozosAFilasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignarPozosAFilasMenuItemActionPerformed
        AsignarPozosAFilasIF pozosAFilasIF = AsignarPozosAFilasIF.getInstance();
        openForm2(pozosAFilasIF);
    }//GEN-LAST:event_asignarPozosAFilasMenuItemActionPerformed

    private void secPerfoPozosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secPerfoPozosMenuItemActionPerformed
        SecPerfoPozoIF secPerfoPozoIF = SecPerfoPozoIF.getInstance();
        openForm2(secPerfoPozoIF);
    }//GEN-LAST:event_secPerfoPozosMenuItemActionPerformed

    private void asignaTaladrosFilaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_asignaTaladrosFilaMenuItemActionPerformed
        AsignaTaladrosaFilaIF asignaTaladrosaFilaIF = AsignaTaladrosaFilaIF.getInstance();
        openForm2(asignaTaladrosaFilaIF);
    }//GEN-LAST:event_asignaTaladrosFilaMenuItemActionPerformed

    private void onWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_onWindowOpened
        if(!VistaUtilities.OnStart()){
            JOptionPane.showMessageDialog(null, 
                    "No hay acceso a la Base de Datos, \nverifique que el servicio está activo", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }//GEN-LAST:event_onWindowOpened

    private void genPerfBaseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genPerfBaseMenuItemActionPerformed
        GenPerfBaseIF genPerfBase = GenPerfBaseIF.getInstance();
        openForm2(genPerfBase);
    }//GEN-LAST:event_genPerfBaseMenuItemActionPerformed

    private void gestionEscenariosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestionEscenariosMenuItemActionPerformed
        GestionEscenarioIF escenarioIF = GestionEscenarioIF.getInstance();
        openForm2(escenarioIF);
    }//GEN-LAST:event_gestionEscenariosMenuItemActionPerformed

    private void perforacionEscMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_perforacionEscMenuItemActionPerformed
        PerforacionEscenariolIF perforacionRealIF = PerforacionEscenariolIF.getInstance();
        openForm2(perforacionRealIF);
    }//GEN-LAST:event_perforacionEscMenuItemActionPerformed

    private void gestionTaladrosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestionTaladrosMenuItemActionPerformed
        GestionTaladros2IF gestionTaladrosIF = GestionTaladros2IF.getInstance();
        openForm2(gestionTaladrosIF);
    }//GEN-LAST:event_gestionTaladrosMenuItemActionPerformed

    private void ordenMacollasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordenMacollasMenuItemActionPerformed
        GestionSecuenciaMacollasIF gestionSecMacoIF = GestionSecuenciaMacollasIF.getInstance();
        openForm2(gestionSecMacoIF);
    }//GEN-LAST:event_ordenMacollasMenuItemActionPerformed

    private void secuenciaPerfMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secuenciaPerfMenuItemActionPerformed
        GestionSecPerforacionIF gestionSecPerforacion = GestionSecPerforacionIF.getInstance();
        openForm2(gestionSecPerforacion);
    }//GEN-LAST:event_secuenciaPerfMenuItemActionPerformed

    private void editarFasesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarFasesMenuItemActionPerformed
        GestionFaseTaladrosIF faseTaladrosIF = GestionFaseTaladrosIF.getInstance();
        openForm2(faseTaladrosIF);
    }//GEN-LAST:event_editarFasesMenuItemActionPerformed

    private void variacionesPozoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variacionesPozoMenuItemActionPerformed
        GestionPozosIF gestionPozosIF = GestionPozosIF.getInstance();
        openForm2(gestionPozosIF);
    }//GEN-LAST:event_variacionesPozoMenuItemActionPerformed

    private void explotacionEscMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_explotacionEscMenuItemActionPerformed
        ExplotacionEscenarioIF explotacionEscenarioIF = ExplotacionEscenarioIF.getInstance();
        openForm2(explotacionEscenarioIF);
    }//GEN-LAST:event_explotacionEscMenuItemActionPerformed

    private void repPerforacionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repPerforacionMenuItemActionPerformed
        RepInversionIF repPerforacion = RepInversionIF.getInstance();
        repPerforacion.setTipoReporte(Constantes.REPORTE_PERFORACION);
        repPerforacion.setTitle("Perforación (Conteo)");
        openForm2(repPerforacion);
    }//GEN-LAST:event_repPerforacionMenuItemActionPerformed

    private void repProduccionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repProduccionMenuItemActionPerformed
        RepExplotacionIF repExplotacion = RepExplotacionIF.getInstance(Constantes.REPORTE_EXPLOTACION);
        openForm2(repExplotacion);
    }//GEN-LAST:event_repProduccionMenuItemActionPerformed

    private void repInversionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repInversionMenuItemActionPerformed
        RepInversionIF repInversion = RepInversionIF.getInstance();
        repInversion.setTipoReporte(Constantes.REPORTE_INVERSION);
        repInversion.setTitle("Reporte Inversión");
        openForm2(repInversion);
    }//GEN-LAST:event_repInversionMenuItemActionPerformed

    private void tiemposCostoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tiemposCostoMenuItemActionPerformed
        GestionTiempoCostoPerfIF gestionTiempoCostos = GestionTiempoCostoPerfIF.getInstance();
        gestionTiempoCostos.setTitle("Gestión de Tiempos y Costos de Perforación");
        openForm2(gestionTiempoCostos);
    }//GEN-LAST:event_tiemposCostoMenuItemActionPerformed

    private void gestionMVMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gestionMVMenuItemActionPerformed
        GestionMejorVisionIF gestionMVIF = GestionMejorVisionIF.getInstance();
        gestionMVIF.setTitle("Gestión de Mejor Visión");
        openForm2(gestionMVIF);
    }//GEN-LAST:event_gestionMVMenuItemActionPerformed

    private void agregarPozoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarPozoMenuItemActionPerformed
        AgregarPozosIF agregarPozosIF = AgregarPozosIF.getInstance();
        agregarPozosIF.setTitle("Agregar Pozo a Macolla");
        openForm2(agregarPozosIF);
    }//GEN-LAST:event_agregarPozoMenuItemActionPerformed

    private void hojaOperacionalMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hojaOperacionalMenuItemActionPerformed
        RepHojaOperacionalIF hojaOperacionalIF = RepHojaOperacionalIF.getInstance();
        hojaOperacionalIF.setTitle("Reporte Hoja Operacional");
        openForm2(hojaOperacionalIF);
    }//GEN-LAST:event_hojaOperacionalMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem agregarPozoMenuItem;
    private javax.swing.JMenu archivoMenu;
    private javax.swing.JMenuItem asignaTaladrosFilaMenuItem;
    private javax.swing.JMenuItem asignarPozosAFilasMenuItem;
    private javax.swing.JMenuItem configTaladrosMenuItem;
    private javax.swing.JMenu configuraCampoMenu;
    private javax.swing.JMenuItem configuraCampoMenuItem;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem distritoMenuItem;
    private javax.swing.JMenuItem editarFasesMenuItem;
    private javax.swing.JMenu escenariosMenu;
    private javax.swing.JMenuItem explotacionEscMenuItem;
    private javax.swing.JMenuItem genPerfBaseMenuItem;
    private javax.swing.JMenuItem gestionEscenariosMenuItem;
    private javax.swing.JMenuItem gestionMVMenuItem;
    private javax.swing.JMenuItem gestionTaladrosMenuItem;
    private javax.swing.JMenuItem hojaOperacionalMenuItem;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private static javax.swing.JLabel messageLabel;
    private javax.swing.JMenuItem ordenMacollasMenuItem;
    private javax.swing.JMenuItem orderMacollasMenuItem;
    private javax.swing.JMenuItem paridadMenuItem;
    private javax.swing.JMenuItem perforacionEscMenuItem;
    private javax.swing.JMenuItem repInversionMenuItem;
    private javax.swing.JMenuItem repPerforacionMenuItem;
    private javax.swing.JMenuItem repProduccionMenuItem;
    private javax.swing.JMenu reportesMenu;
    private javax.swing.JMenuItem salirMenuItem;
    private javax.swing.JMenuItem secPerfoPozosMenuItem;
    private javax.swing.JMenuItem secuenciaPerfMenuItem;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenuItem tiemposCostoMenuItem;
    private javax.swing.JMenuItem variacionesPozoMenuItem;
    // End of variables declaration//GEN-END:variables
}
