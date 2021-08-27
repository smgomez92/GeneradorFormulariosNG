package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.fitbank.ifg.Mensajes;

/**
 * Dialogo de Acerca De.
 * 
 * @author FitBank CI
 */
public class AcercaDe extends JDialog {

    private static final long serialVersionUID = 1L;

    Image image = new ImageIcon(Thread.currentThread().getContextClassLoader()
            .getResource("splash.png")).getImage();

    public AcercaDe(Frame parent) {
        this(parent, "Acerca de", true);
    }

    private AcercaDe(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        setResizable(false);
        setLayout(new BorderLayout());

        JLabel label = new JLabel(Mensajes.format("iFG.AcercaDe") + "    ") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, this);
                super.paintComponent(g);
            }
        };
        label.setPreferredSize(new Dimension(480, 320));
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        add(label, BorderLayout.CENTER);

        pack();

        setLocationRelativeTo(null);
    }
}