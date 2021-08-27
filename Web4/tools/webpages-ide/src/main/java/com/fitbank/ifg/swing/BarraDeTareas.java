package com.fitbank.ifg.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.commons.lang.StringUtils;

import com.fitbank.ifg.DatosPortaPapeles;
import com.fitbank.ifg.iFG;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.widgets.Input;

/**
 * Clase BarraDeTareas.
 *
 * @author FitBank
 */
public class BarraDeTareas extends JToolBar {

    private static final long serialVersionUID = 1L;

    private Boton nuevo = new Boton("gtk-new", "nuevo.png", "Nuevo WebPage",
            "nuevo", true);

    private Boton abrir = new Boton("gtk-open", "abrir.png", "Abrir WebPage",
            "abrir", true);

    private Boton guardar = new Boton("gtk-save", "guardar.png",
            "Guardar WebPage", "guardar", true);

    private Boton undo = new Boton("gtk-undo", "undo.png", "Undo", "undo", true);

    private Boton redo = new Boton("gtk-redo", "redo.png", "Redo", "redo", true);

    private Boton validar = new Boton("gtk-index", "validar.png", "Validar",
            "validar", true);

    private Boton abrirNav = new Boton("gtk-execute", "abrirnav.png",
            "Abrir Navegador", "abrirNavegador", true);

    private Boton copiarFila = new Boton("gtk-copy", "cf.png",
            "Copiar Container", "copiarFila", true);

    private Boton cortarFila = new Boton("gtk-cut", "xf.png", "Cortar Container",
            "cortarFila", true);

    private Boton pegarFila = new Boton("gtk-paste", "vf.png", "Pegar Container",
            "pegarFila", true);

    private Boton copiarElemento = new Boton("gtk-copy", "ce.png",
            "Copiar Widget", "copiarElemento", true);

    private Boton cortarElemento = new Boton("gtk-cut", "xe.png",
            "Cortar Widget", "cortarElemento", true);

    private Boton pegarElemento = new Boton("gtk-paste", "ve.png",
            "Pegar Widget", "pegarElemento", true);

    private Boton cerrar = new Boton("gtk-close", "cerrar.png", "Cerrar WebPage",
            "cerrar", true);

    private Boton cerrarTodo = new Boton("", "cerrarTodo.png",
            "Cerrar Todos los Formularios", "cerrarTodo", true);

    private Boton limpiarLog = new Boton("gtk-clear", "clearLog.png",
            "Limpiar Registro de Mensajes", "limpiarLog", true);

    private JLabel labelRapido = new JLabel("Abrir rápido:");

    private JTextField abrirRapido = new JTextField();

    private JTextField buscarWidget = new JTextField();

    private JCheckBox abrirAdjuntos = new JCheckBox();
    private Boton tranformarFormulario = new Boton("gtk-save", "compilar.png",
            "Tranformar Documento", "transDocumento", true);

    /**
     * Crea un nuevo objeto BarraDeTareas.
     *
     * @param ventanaMadre Ventana madre
     */
    public BarraDeTareas(final iFG ifg) {
        putClientProperty("apple.awt.brushMetalLook", Boolean.TRUE);

        abrirRapido.setMaximumSize(new Dimension(100, 25));
        abrirRapido.setMinimumSize(new Dimension(100, 25));
        abrirRapido.setPreferredSize(new Dimension(100, 25));
        abrirRapido.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                String text = abrirRapido.getText().replaceAll("[^\\d]", "");

                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (text.length() < 3) {
                        return;
                    }

                    String subsystem = text.substring(0, 2);
                    String transaction = text.substring(2);
                    open(subsystem, transaction);
                    abrirRapido.setText("");
                    return;
                }

                if (text.length() > 6) {
                    text = text.substring(0, 6);
                }

                if (text.length() > 2) {
                    text = text.substring(0, 2) + "-" + text.substring(2);
                }

                abrirRapido.setText(text);
            }

        });

        buscarWidget.setMinimumSize(new Dimension(10, 25));
        buscarWidget.setPreferredSize(buscarWidget.getMinimumSize());
        buscarWidget.setMaximumSize(buscarWidget.getMaximumSize());
        buscarWidget.setToolTipText("Ingrese el nombre del widget a encontrar.");
        buscarWidget.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (ifg.getPanelFilasElementos() == null
                        || StringUtils.isBlank(buscarWidget.getText())) {
                    return;
                }

                WebPage webPage = ifg.getWebPageActual();
                FormElement element = webPage.findFormElement(buscarWidget.getText());

                if (element instanceof Input) {
                    Input input = (Input) element;
                    ifg.getPanelFilasElementos().seleccionarFila(input.getParent().getPosicion());
                    ifg.getPanelFilasElementos().seleccionarElemento(input.getPosicion());
                    buscarWidget.setText("");
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se encontró un widget con el nombre especificado.",
                            "Widget no encontrado", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        });

        abrirAdjuntos.setToolTipText("Abrir adjuntos");

        JComponent[] elementos = {nuevo, abrir, guardar,
            new JSeparator(), labelRapido, abrirRapido, abrirAdjuntos,
            new JSeparator(), undo, redo,
            new JSeparator(), validar, abrirNav,
            new JSeparator(), new JLabel("Container: "), copiarFila,
            cortarFila, pegarFila,
            new JSeparator(), new JLabel("Widget: "), copiarElemento,
            cortarElemento, pegarElemento,
            new JSeparator(), cerrar, cerrarTodo, limpiarLog,
            new JSeparator(), new JLabel("Transformar: "), tranformarFormulario, new JSeparator(), new JLabel("Buscar widget: "), buscarWidget,};

        for (JComponent elemento : elementos) {
            if (elemento instanceof AbstractButton) {
                this.add(elemento);
                ((AbstractButton) elemento).addActionListener(ifg);
            } else if (elemento instanceof JSeparator) {
                this.addSeparator();
            } else {
                this.add(elemento);
            }
        }

        setRollover(true);
        setFloatable(false);
        mostrarLabels(false);
    }

    private void open(String subsystem, String transaction) throws
            HeadlessException {
        while (transaction.length() < 4) {
            transaction = "0" + transaction;
        }

        File file = new File(HardDiskWebPageProvider.getPath(
                subsystem, transaction));

        if (!file.exists()) {
            JOptionPane.showMessageDialog(iFG.getSingleton(),
                    "Archivo no encontrado: "
                    + file.getAbsolutePath());
        } else {
            iFG.getSingleton().abrirFile(file);

            if (abrirAdjuntos.isSelected()) {
                for (AttachedWebPage attachedWebPage : iFG.getSingleton().
                        getWebPageActual().getAttached()) {
                    open(attachedWebPage.getSubsystem(), attachedWebPage.
                            getTransaction());
                }
            }
        }
    }

    /**
     * Activa o desactiva los labels.
     *
     * @param activos Define si estan activos
     */
    public final void mostrarLabels(boolean activos) {
        for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponentAtIndex(i);
            if (c != null && c instanceof AbstractButton) {
                ((AbstractButton) c).setText(activos ? ((AbstractButton) c).
                        getToolTipText() : null);
            }
        }
    }

    /**
     * Actualizar los activos.
     *
     * @param ifg
     */
    public void actualizarActivos(iFG ifg) {
        boolean abierto = ifg.isAbierto();
        PanelFilasElementos pfe = ifg.getPanelFilasElementos();
        boolean conCambios = abierto && pfe.hasUnsavedChanges();

        guardar.setEnabled(conCambios);

        validar.setEnabled(abierto);
        abrirNav.setEnabled(false);

        undo.setEnabled(abierto && pfe.canUndo());
        redo.setEnabled(abierto && pfe.canRedo());

        copiarFila.setEnabled(abierto && pfe.getContainerActual() != null);
        cortarFila.setEnabled(abierto && pfe.getContainerActual() != null);
        copiarElemento.setEnabled(abierto && pfe.getWidgetActual() != null);
        cortarElemento.setEnabled(abierto && pfe.getWidgetActual() != null);

        pegarFila.setEnabled(abierto
                && DatosPortaPapeles.isAvailable(Container.class));
        pegarElemento.setEnabled(abierto
                && DatosPortaPapeles.isAvailable(Widget.class)
                && pfe.getContainerActual() != null);

        limpiarLog.setEnabled(abierto);
        cerrar.setEnabled(abierto);
        cerrarTodo.setEnabled(abierto);
        tranformarFormulario.setEnabled(!conCambios && abierto);
    }

}
