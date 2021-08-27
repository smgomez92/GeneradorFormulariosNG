package com.fitbank.ifg.swing;

import java.awt.BorderLayout;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;

import com.fitbank.ifg.DatosPortaPapeles;
import com.fitbank.ifg.Mensajes;
import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.dialogs.EditorPropiedades;
import com.fitbank.ifg.swing.tables.TablaElementos;
import com.fitbank.ifg.swing.tables.TablaFilas;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import com.fitbank.util.Pair;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageXml;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.widgets.Label;

public class PanelFilasElementos extends JPanel implements ClipboardOwner,
        TableModelListener {

    private static final long serialVersionUID = 1L;

    private final TablaFilas filas;

    private final TablaElementos elementos;

    private final JDialog parent;

    private File archivoActual = null;

    private Widget widget = null;

    private int errorCount;

    private int warningCount;

    public PanelFilasElementos(JFrame parent, File file) {
        this.parent = new JDialog(parent);

        filas = new TablaFilas(this.parent, null);
        elementos = new TablaElementos(this.parent, null);

        setLayout(new BorderLayout());

        JScrollPane scrollPaneFilas = new JScrollPane();
        scrollPaneFilas.getViewport().add(filas);

        JScrollPane scrollPaneElementos = new JScrollPane();
        scrollPaneElementos.getViewport().add(elementos);

        filas.getModel().addTableModelListener(this);
        filas.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    return;
                }

                setContainerActual(getWebPage().get(
                        lsm.getMinSelectionIndex()));

                iFG.getSingleton().loadProperties(getContainerActual());
            }

        });
        filas.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                setContainerActual(getWebPage().get(filas.getSelectedRow()));
                iFG.getSingleton().loadProperties(getContainerActual());
            }

        });

        elementos.getModel().addTableModelListener(this);
        elementos.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    return;
                }

                setWidgetActual(getContainerActual().get(
                        lsm.getMinSelectionIndex()));

                iFG.getSingleton().loadProperties(getWidgetActual());
            }

        });
        elementos.addFocusListener(new FocusListener() {

            public void focusLost(FocusEvent e) {
            }

            public void focusGained(FocusEvent e) {
                iFG.getSingleton().loadProperties(getWidgetActual());
            }

        });

        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPaneFilas, scrollPaneElementos);
        jSplitPane.setDividerLocation(220);

        add(jSplitPane, BorderLayout.CENTER);

        loadFile(file);
    }

    public File getArchivoActual() {
        return archivoActual;
    }

    public void setArchivoActual(File archivoActual) {
        this.archivoActual = archivoActual;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public TablaElementos getTablaElementos() {
        return elementos;
    }

    private void loadFile(File archivoActual) {
        setArchivoActual(archivoActual);

        if (archivoActual != null) {
            try {
                setWebPage(WebPageXml.parse(new FileInputStream(
                        getArchivoActual())));
                saveUndoState();
                setSaveState();
                seleccionarFila(0);
                return;
            } catch (FileNotFoundException e) {
                Debug.error(e);
            } catch (ExcepcionParser e) {
                Debug.error(e);
            }
        }

        setWebPage(new WebPage());
        saveUndoState();
    }

    public WebPage getWebPage() {
        return filas.getWebPage();
    }

    public void setWebPage(WebPage webPage) {
        filas.setWebPage(webPage);
        if (webPage == null || !webPage.contains(getContainerActual())) {
            setContainerActual(null);
        }
    }

    public Container getContainerActual() {
        return elementos.getContainerActual();
    }

    public void setContainerActual(Container container) {
        if (container == null || container.contains(getWidgetActual())
                || !container.equals(getContainerActual())) {
            setWidgetActual(null);
        }
        TableCellEditor cellEditor = elementos.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
        elementos.setContainer(container);

        if (getContainerActual() != null) {
            int posicion = getContainerActual().getPosicion();
            filas.setRowSelectionInterval(posicion, posicion);
            filas.scrollRowToVisible(posicion);
        }
    }

    public Widget getWidgetActual() {
        return widget;
    }

    public void setWidgetActual(Widget widget) {
        this.widget = widget;

        if (getWidgetActual() != null) {
            int posicion = getWidgetActual().getPosicion();
            elementos.setRowSelectionInterval(posicion, posicion);
            elementos.scrollRowToVisible(posicion);
        }
    }

    public void refresh() {
        Pair<Integer, Integer> positions = savePositions();

        filas.refresh();
        elementos.refresh();

        restorePositions(positions);
    }

    private Pair<Integer, Integer> savePositions() {
        return new Pair<Integer, Integer>(
                getContainerActual() != null
                ? getContainerActual().getPosicion() : -1,
                getWidgetActual() != null ? getWidgetActual().getPosicion() : -1);
    }

    private void restorePositions(Pair<Integer, Integer> positions) {
        if (positions.getFirst() != -1 && !getWebPage().isEmpty()) {
            seleccionarFila(positions.getFirst());
            if (positions.getSecond() != -1 && !getContainerActual().isEmpty()) {
                seleccionarElemento(positions.getSecond());
            }
        }
    }

    // ////////////////////////////////////////////
    // MANEJO DE UNDO Y REDO
    // ////////////////////////////////////////////
    private final List<String> undoStack =
            Collections.synchronizedList(new LinkedList<String>());

    private int undoPosition = -1;

    private int savePosition = -1;

    private boolean doSave = true;

    public void saveUndoState() {
        if (!doSave) {
            return;
        }

        String stringXml = getWebPage().toStringXml();

        synchronized (undoStack) {
            if (!undoStack.isEmpty()
                    && stringXml.equals(undoStack.get(undoPosition))) {
                return;
            }

            Debug.debug("Guardando información de undo-redo");

            if (canRedo()) {
                undoStack.subList(undoPosition + 1, undoStack.size()).clear();
                if (savePosition > undoPosition) {
                    savePosition = -1;
                }
            }

            undoStack.add(stringXml);

            undoPosition = undoStack.size() - 1;

            refresh();
        }

        iFG.getSingleton().validar(false);
    }

    public void setSaveState() {
        saveUndoState();
        savePosition = undoPosition;
    }

    public boolean canUndo() {
        return undoPosition > 0;
    }

    public boolean canRedo() {
        return undoPosition + 1 < undoStack.size();
    }

    public boolean hasUnsavedChanges() {
        return undoPosition != savePosition;
    }

    public void undo() {
        if (canUndo()) {
            undoPosition--;
            realoadUndoState();
        }
    }

    public void redo() {
        if (canRedo()) {
            undoPosition++;
            realoadUndoState();
        }
    }

    private void realoadUndoState() {
        Pair<Integer, Integer> positions = savePositions();

        synchronized (undoStack) {
            doSave = false;
            try {
                setWebPage(WebPageXml.parseString(undoStack.get(undoPosition)));
            } catch (ExcepcionParser e) {
                Debug.error(e);
            }
            doSave = true;
        }

        restorePositions(positions);
    }

    // ////////////////////////////////////////////
    // MANEJO DE FILAS
    // ////////////////////////////////////////////
    public void seleccionarFila(int posicionFila) {
        if (getWebPage().isEmpty()) {
            setContainerActual(null);
        } else {
            setContainerActual(getWebPage().get(Servicios.inside(posicionFila, 0,
                    getWebPage().size() - 1)));
        }
    }

    public void editarFila() {
        filas.editarFila();
    }

    public void nuevaFila() {
        int posicionFila = getContainerActual() != null ? getContainerActual().
                getPosicion() + 1 : getWebPage().size();

        getWebPage().add(posicionFila, new Container());
        saveUndoState();
        refresh();

        seleccionarFila(posicionFila);
    }

    public boolean borrarFila() {
        int posicionFila = getContainerActual().getPosicion() + 1;

        getWebPage().remove(getContainerActual());
        saveUndoState();

        posicionFila = Math.min(getWebPage().size(), posicionFila) - 1;
        seleccionarFila(posicionFila);

        return true;
    }

    public void moverFila(int cuanto) {
        int posicionFila = getContainerActual().getPosicion();

        if (getWebPage().size() - 1 >= posicionFila + cuanto
                && posicionFila + cuanto >= 0) {
            getWebPage().moveChild(posicionFila, cuanto);
            saveUndoState();
            refresh();

            seleccionarFila(posicionFila + cuanto);
        }
    }

    // ////////////////////////////////////////////
    // MANEJO DE ELEMENTOS
    // ////////////////////////////////////////////
    public void seleccionarElemento(int posicionElemento) {
        if (getContainerActual().isEmpty()) {
            setWidgetActual(null);
        } else {
            setWidgetActual(getContainerActual().get(Servicios.inside(
                    posicionElemento, 0, getContainerActual().size() - 1)));
        }
    }

    public void editarElemento() {
        new EditorPropiedades(parent, getWidgetActual()).setVisible(true);
    }

    public void nuevoElemento() {
        int posicionElemento = getWidgetActual() != null ? getWidgetActual().
                getPosicion() + 1
                : getContainerActual().size();

        getContainerActual().add(posicionElemento, new Label());
        saveUndoState();

        seleccionarElemento(posicionElemento);
    }

    public boolean borrarElemento() {
        int posicionElemento = getWidgetActual().getPosicion();

        if (elementos.getSelectedRowCount() > 1) {
            int[] queF = elementos.getSelectedRows();

            for (int posicionElemento2 : queF) {
                getContainerActual().remove(posicionElemento2);
            }
        } else {
            getContainerActual().remove(getWidgetActual());
        }

        posicionElemento = Math.min(getContainerActual().size() - 1,
                posicionElemento);

        saveUndoState();

        seleccionarElemento(posicionElemento);

        return true;
    }

    public void moverElemento(int cuanto) {
        if (cuanto == 0) {
            return;
        }

        int posicionElemento = getWidgetActual().getPosicion();

        if (elementos.getSelectedRowCount() > 1) {
            int numF = elementos.getSelectedRowCount();
            int[] queF = elementos.getSelectedRows();

            if (cuanto > 0 && queF[numF - 1] != getContainerActual().size() - 1) {
                for (int i = numF - 1; i >= 0; i--) {
                    getContainerActual().moveChild(queF[i], cuanto);
                }
            } else if (cuanto < 0 && queF[0] != 0) {
                for (int i = 0; i < numF; i++) {
                    getContainerActual().moveChild(queF[i], cuanto);
                }
            } else {
                Debug.warn(Mensajes.format(
                        "iFG.ErrorGrupoElementosNoPermiteDesplazamiento")); //$NON-NLS-1$
                return;
            }

            saveUndoState();

            seleccionarElemento(queF[0] + cuanto);

            for (int i = 1; i < numF; i++) {
                posicionElemento = queF[i] + cuanto;
                elementos.addRowSelectionInterval(posicionElemento,
                        posicionElemento);
            }
        } else if (getContainerActual().size() - 1 >= posicionElemento + cuanto
                && posicionElemento + cuanto >= 0) {
            getContainerActual().moveChild(posicionElemento, cuanto);

            saveUndoState();

            seleccionarElemento(posicionElemento + cuanto);
        }
    }

    // ////////////////////////////////////////////
    // MANEJO DE CLIPBOARD FILAS
    // ////////////////////////////////////////////
    public void copiarFila() {
        DatosPortaPapeles.copy(getContainerActual(), this);
    }

    public void cortarFila() {
        copiarFila();
        borrarFila();
    }

    public void pegarFila() {
        int posicionFila = getContainerActual() != null ? getContainerActual().
                getPosicion() + 1 : getWebPage().size();

        Container container = DatosPortaPapeles.get(Container.class);

        if (container != null) {
            getWebPage().add(posicionFila, container);

            saveUndoState();

            seleccionarFila(posicionFila);
        }
    }

    // ////////////////////////////////////////////
    // MANEJO DE CLIPBOARD ELEMENTOS
    // ////////////////////////////////////////////
    public void copiarElemento() {
        DatosPortaPapeles.copy(getWidgetActual(), this);
    }

    public void cortarElemento() {
        copiarElemento();
        borrarElemento();
    }

    public void pegarElemento() {
        int posicionElemento = getWidgetActual() != null ? getWidgetActual().
                getPosicion() + 1 : getContainerActual().size();

        Widget widget = DatosPortaPapeles.get(Widget.class);

        if (widget != null) {
            getContainerActual().add(posicionElemento, widget);

            saveUndoState();

            seleccionarElemento(posicionElemento);
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    public void tableChanged(TableModelEvent e) {
        saveUndoState();
    }

}
