package com.sismon.test;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author jgcastillo
 */
public class PruebaMessageAutoClose {

    public static void main(String[] args) {
        new PruebaMessageAutoClose();
    }

    public PruebaMessageAutoClose() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                new BackgroundWorker().execute();

            }

        });
    }

    public class BackgroundWorker extends SwingWorker<Void, Void> {

        private JProgressBar pb;
        private JDialog dialog;

        public BackgroundWorker() {
            addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("progress".equalsIgnoreCase(evt.getPropertyName())) {
                        if (dialog == null) {
                            dialog = new JDialog();
                            dialog.setTitle("Processing");
                            dialog.setLayout(new GridBagLayout());
                            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                            GridBagConstraints gbc = new GridBagConstraints();
                            gbc.insets = new Insets(2, 2, 2, 2);
                            gbc.weightx = 1;
                            gbc.gridy = 0;
                            dialog.add(new JLabel("Processing..."), gbc);
                            pb = new JProgressBar();
                            gbc.gridy = 1;
                            dialog.add(pb, gbc);
                            dialog.pack();
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                        }
                        pb.setValue(getProgress());
                    }
                }

            });
        }

        @Override
        protected void done() {
            if (dialog != null) {
                dialog.dispose();
            }
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (int index = 0; index < 100; index++) {
                setProgress(index);
                Thread.sleep(125);
            }
            return null;
        }
    }
}
