package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.fitbank.ifg.swing.Boton;
import com.fitbank.ifg.swing.tables.ValidationMessagesTable;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationUtils;
import java.awt.Window;

public class ValidationDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public ValidationDialog(Window parent, WebElement<?> webElement) {
        super(parent, "Validaciones", ModalityType.APPLICATION_MODAL);

        final Collection<ValidationMessage> resultados = ValidationUtils
                .validate(webElement, new HardDiskWebPageProvider());

        this.setSize(new Dimension(1000, 400));
        setResizable(true);
        setLocationRelativeTo(null);

        final ValidationMessagesTable table = new ValidationMessagesTable(
                parent, new LinkedList<ValidationMessage>(resultados));

        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.getViewport().add(table);

        JButton arreglarTodo = new Boton("gtk-edit", "actions/edit-select-all.png", false);
        arreglarTodo.setText("Arreglar todo");

        arreglarTodo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (ValidationMessage message : resultados) {
                    if (message.isFixable()) {
                        message.fix();
                    }
                }
                setVisible(false);
                dispose();
            }
        });

        JButton arreglarSeleccionado = new Boton("gtk-edit", "actions/edit-find-replace.png", false);
        arreglarSeleccionado.setText("Arreglar seleccionado");

        arreglarSeleccionado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (ValidationMessage message : table.getSelected()) {
                    if (message.isFixable()) {
                        message.fix();
                    }
                }
                setVisible(false);
                dispose();
            }
        });

        arreglarTodo.setEnabled(false);
        arreglarSeleccionado.setEnabled(false);
        for (ValidationMessage message : resultados) {
            if (message.isFixable()) {
                arreglarTodo.setEnabled(true);
                arreglarSeleccionado.setEnabled(true);
                break;
            }
        }

        JPanel jPanel = new JPanel();
        jPanel.add(arreglarTodo);
        jPanel.add(arreglarSeleccionado);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jScrollPane, BorderLayout.CENTER);
        getContentPane().add(jPanel, BorderLayout.SOUTH);
    }

}
