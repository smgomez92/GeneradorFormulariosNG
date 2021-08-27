package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * Clase que presenta un editor libre.
 * 
 * @author FitBank CI
 */
public class EditorLibre extends JDialog implements WindowListener {

    private static final long serialVersionUID = 1L;

    private JTextField unaLinea = new JTextField();
    private Preferences preferencias = Preferences
            .systemNodeForPackage(EditorLibre.class);
    private JScrollPane scroll = new JScrollPane();
    private JEditorPane muchasLineas = new JEditorPane();

    public EditorLibre(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        this.setSize(new Dimension(400, 300));
        getContentPane().setLayout(new BorderLayout());
        setResizable(true);
        muchasLineas.setBorder(javax.swing.BorderFactory
                .createBevelBorder(BevelBorder.LOWERED));
        getContentPane().add(unaLinea, BorderLayout.NORTH);
        scroll.getViewport().add(muchasLineas, null);
        getContentPane().add(scroll, BorderLayout.CENTER);
        addWindowListener(this);
        muchasLineas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    EditorLibre.this.setVisible(false);
                } else if (e.getModifiers() != InputEvent.SHIFT_MASK
                        && e.getKeyCode() == KeyEvent.VK_TAB
                        && muchasLineas.getSelectedText() == null) {
                    int pos = muchasLineas.getCaretPosition();
                    String cont = muchasLineas.getText().replaceAll("\r\n",
                            "\n");
                    muchasLineas.setText(cont.substring(0, pos) + "  "
                            + cont.substring(pos));
                    muchasLineas.setCaretPosition(pos + 2);
                    e.consume();
                }
            }
        });
        setLocationRelativeTo(null);
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
        preferencias.put("unaLinea", unaLinea.getText()
                .replaceAll("\r?\n", " "));
        preferencias.put("muchasLineas", muchasLineas.getText().replaceAll(
                "\r\n", "\n"));
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
        unaLinea.setText(preferencias.get("unaLinea", ""));
        muchasLineas.setText(preferencias.get("muchasLineas", ""));
    }
}