package com.fitbank.ifg.swing;

import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import com.fitbank.ifg.iFG;

/**
 * Menu del iFG.
 *
 * @author FitBank
 */
public class Menu extends JMenuBar {

    private static final int MENU_SHORTKEY = Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask();

    private static final long serialVersionUID = 1L;

    private final iFG ifg;

    private JMenu archivo = new JMenu("File");

    private JMenuItem nuevo = new JMenuItem("Nuevo");

    private JMenuItem abrir = new JMenuItem("Abrir");

    private JMenu recientes = new JMenu("Recientes");

    private JMenuItem cerrar = new JMenuItem("Cerrar");

    private JMenuItem cerrarTodo = new JMenuItem("Cerrar todo");

    private JMenuItem guardar = new JMenuItem("Guardar");

    private JMenuItem guardarComo = new JMenuItem("Guardar como...");

    private JMenuItem salir = new JMenuItem("Salir");

    private JMenu editar = new JMenu("Editar");

    private JMenuItem undo = new JMenuItem("Undo");

    private JMenuItem redo = new JMenuItem("Redo");

    private JMenuItem editarFormulario = new JMenuItem("WebPage");

    private JMenuItem editarCalculos = new JMenuItem("C\u00E1lculos");

    private JMenuItem editarFila = new JMenuItem("Container");

    private JMenuItem editarElemento = new JMenuItem("Widget");

    private JMenuItem resetearEstado = new JMenuItem("Resetear ventana");

    private JMenuItem preferencias = new JMenuItem("Preferencias");

    private JMenuItem addImports = new JMenuItem("Administrar Imports");
    private JMenuItem addExports = new JMenuItem("Administrar Exports");

    private JMenu herramientas = new JMenu("Herramientas");

    private JMenuItem validar = new JMenuItem("Validar");

    private JMenuItem buscar = new JMenuItem("Buscar");

    private JMenuItem fijarValor = new JMenuItem(
            "Fijar valor a columna:  X / Y / W / H");

    private JMenuItem agregarValor = new JMenuItem(
            "Agregar valor a columna:  X / Y / W / H");

    private JMenuItem ajustarX = new JMenuItem("Ajustar columna  X");

    private JMenuItem limpiarEstilo = new JMenuItem(
            "Limpiar Estilo y Valores de campos ocultos");

    private JMenu ayuda = new JMenu("Ayuda");

    private JMenuItem help = new JMenuItem("Ayuda");

    private JMenuItem acercade = new JMenuItem("Acerca de...");

    public Menu(final iFG ifg) {
        this.ifg = ifg;

        crearArchivo();
        this.add(archivo);

        crearEditar();
        this.add(editar);

        crearHerramientas();
        this.add(herramientas);

        crearAyuda();
        this.add(ayuda);

        fijarValor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ifg.getiFGExtra().agregarValor(false);
            }
        });
        agregarValor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ifg.getiFGExtra().agregarValor(true);
            }
        });
        ajustarX.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ifg.getiFGExtra().ajustarX();
            }
        });
        limpiarEstilo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ifg.getiFGExtra().limpiarEstilo();
            }
        });

    }

    private void crearArchivo() {
        nuevo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                MENU_SHORTKEY, true));
        nuevo.setActionCommand("nuevo");
        nuevo.addActionListener(ifg);

        abrir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                MENU_SHORTKEY, false));
        abrir.setActionCommand("abrir");
        abrir.addActionListener(ifg);

        cerrar.setActionCommand("cerrar");
        cerrar.addActionListener(ifg);

        cerrarTodo.setActionCommand("cerrarTodo");
        cerrarTodo.addActionListener(ifg);

        guardar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                MENU_SHORTKEY, true));
        guardar.setActionCommand("guardar");
        guardar.addActionListener(ifg);

        guardarComo.setActionCommand("guardarComo");
        guardarComo.addActionListener(ifg);

        salir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                Event.ALT_MASK, true));
        salir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ifg.salir(true);
            }
        });

        archivo.add(nuevo);
        archivo.add(abrir);
        archivo.add(recientes);
        archivo.addSeparator();
        archivo.add(cerrar);
        archivo.add(cerrarTodo);
        archivo.addSeparator();
        archivo.add(guardar);
        archivo.add(guardarComo);
        archivo.addSeparator();
        archivo.add(salir);
    }

    private void crearEditar() {
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                MENU_SHORTKEY, true));
        undo.setActionCommand("undo");
        undo.addActionListener(ifg);

        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                MENU_SHORTKEY, true));
        redo.setActionCommand("redo");
        redo.addActionListener(ifg);

        editarFormulario.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
                Event.ALT_MASK, true));
        editarFormulario.setActionCommand("editarFormulario");
        editarFormulario.addActionListener(ifg);

        editarCalculos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                Event.ALT_MASK, true));
        editarCalculos.setActionCommand("editarCalculos");
        editarCalculos.addActionListener(ifg);
        
        addImports.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                Event.CTRL_MASK, true));
        addImports.setActionCommand("agregarImports");
        addImports.addActionListener(ifg);
        
         addExports.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                Event.CTRL_MASK, true));
        addExports.setActionCommand("agregarExports");
        addExports.addActionListener(ifg);

        editarElemento.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                Event.ALT_MASK, true));
        editarElemento.setActionCommand("editarElemento");
        editarElemento.addActionListener(ifg);

        editarFila.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Event.ALT_MASK, true));
        editarFila.setActionCommand("editarFila");
        editarFila.addActionListener(ifg);

        resetearEstado.setActionCommand("resetearEstado");
        resetearEstado.addActionListener(ifg);

        preferencias.setActionCommand("preferencias");
        preferencias.addActionListener(ifg);

        
        editar.add(undo);
        editar.add(redo);
        editar.addSeparator();
        editar.add(editarElemento);
        editar.add(editarFila);
        editar.add(editarFormulario);
        editar.addSeparator();
        editar.add(editarCalculos);

        editar.addSeparator();
        editar.add(addImports);
        editar.add(addExports);
        editar.addSeparator();
        editar.add(resetearEstado);

        editar.addSeparator();
        editar.add(preferencias);
    }

    private void crearHerramientas() {
        validar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                Event.ALT_MASK, true));
        validar.setActionCommand("validar");
        validar.addActionListener(ifg);

        buscar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                Event.CTRL_MASK, true));
        buscar.setActionCommand("buscar");
        buscar.addActionListener(ifg);

        fijarValor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0,
                true));

        agregarValor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,
                Event.ALT_MASK, true));

        herramientas.add(validar);

        // FIXME
        herramientas.addSeparator();
        herramientas.add(fijarValor);
        herramientas.add(agregarValor);
        herramientas.add(ajustarX);
        herramientas.addSeparator();
        herramientas.add(limpiarEstilo);
    }

    private void crearAyuda() {
        acercade.setActionCommand("acercade");
        acercade.addActionListener(ifg);

        ayuda.add(acercade);
    }

    public void actualizarRecientes(File file) {
        String[] recientes2 = iFG.preferencias.get("recientes", "").split(";");
        List<String> rec = new LinkedList<String>(Arrays.asList(recientes2));

        if (rec.size() > 20) {
            rec = rec.subList(0, 20);
        }

        if (file != null) {
            String path = file.getAbsolutePath();

            rec.remove(path);
            rec.add(0, path);

            iFG.preferencias.put("recientes", StringUtils.join(rec, ";"));
        }

        recientes.removeAll();
        for (String filename : rec) {
            final File file2 = new File(filename);
            if (!file2.exists()) {
                continue;
            }
            JMenuItem archivo = new JMenuItem(filename);
            archivo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    ifg.abrirFile(file2);
                }
            });
            recientes.add(archivo);
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

        cerrar.setEnabled(abierto);
        cerrarTodo.setEnabled(abierto);
        guardar.setEnabled(abierto && pfe.getArchivoActual() != null);
        guardarComo.setEnabled(abierto);

        undo.setEnabled(abierto && pfe.canUndo());
        redo.setEnabled(abierto && pfe.canRedo());
        editarFormulario.setEnabled(abierto);
        editarFila.setEnabled(abierto && pfe.getContainerActual() != null);
        editarElemento.setEnabled(abierto && pfe.getWidgetActual() != null);
        editarCalculos.setEnabled(abierto);
        addImports.setEnabled(abierto);
        addExports.setEnabled(abierto);
        validar.setEnabled(abierto);
        limpiarEstilo.setEnabled(abierto);
        agregarValor.setEnabled(abierto);
        fijarValor.setEnabled(abierto);
        ajustarX.setEnabled(abierto);
    }
}
