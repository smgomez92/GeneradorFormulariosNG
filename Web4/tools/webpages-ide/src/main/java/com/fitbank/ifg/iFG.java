package com.fitbank.ifg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.fitbank.ifg.servidorhttp.ServidorHttp;
import com.fitbank.ifg.swing.BarraDeTareas;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.ifg.swing.Menu;
import com.fitbank.ifg.swing.PanelFilasElementos;
import com.fitbank.ifg.swing.dialogs.AcercaDe;
import com.fitbank.ifg.swing.dialogs.EditorJavascript;
import com.fitbank.ifg.swing.dialogs.EditorLibre;
import com.fitbank.ifg.swing.dialogs.EditorPropiedades;
import com.fitbank.ifg.swing.dialogs.PreferencesDialog;
import com.fitbank.ifg.swing.dialogs.ValidationDialog;
import com.fitbank.ifg.swing.tables.PropertiesTable;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializadorXml;
import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.util.SwingUtils;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.TransformarNG;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviromentNG;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationMessage.Severity;
import com.fitbank.webpages.util.ValidationUtils;
import java.io.IOException;

/**
 * Clase iFG.
 *
 * @author FitBank
 */
public class iFG extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private static final String EXTENSION = ".wpx"; //$NON-NLS-1$;

    public static final Preferences preferencias = Preferences.
            userNodeForPackage(iFG.class);

    private Menu barraDeMenu;

    private BarraDeTareas barraTareas;

    private JPanel panelCentral = new JPanel();

    private JPanel panelBotonesFila = new JPanel();

    private JPanel panelBotonesElemento = new JPanel();

    private JTabbedPane tabsWebPages = new JTabbedPane();

    private JButton nuevaFila = new Boton("gtk-add", "mas.png", false);

    private JButton borrarFila = new Boton("gtk-remove", "menos.png", false);

    private JButton subirFila = new Boton("gtk-go-up", "subir.png", false);

    private JButton bajarFila = new Boton("gtk-go-down", "bajar.png", false);

    private JButton nuevoElemento = new Boton("gtk-add", "mas.png", false);

    private JButton borrarElemento = new Boton("gtk-remove", "menos.png", false);

    private JButton subirElemento = new Boton("gtk-go-up", "subir.png", false);

    private JButton bajarElemento = new Boton("gtk-go-down", "bajar.png", false);

    private JScrollPane consoleScrollPane = new JScrollPane();

    private JScrollPane propertiesScrollPane;

    private JFileChooser fileDialog = new JFileChooser();

    private JPanel panelInferior = new JPanel();

    private JPanel barraEstado = new JPanel(new BorderLayout());

    private JLabel labelEstado = new JLabel();

    private JLabel labelValidaciones = new JLabel();

    private JTextPane console = new JTextPane();

    private JSplitPane propertiesSplit;

    private JSplitPane consoleSplit;

    private final iFGExtra iFGExtra;

    //private final ServidorHttp servidorHttp;

    private Thread threadValidaciones = null;

    private static iFG singleton;

    public static synchronized iFG getSingleton() {
        if (singleton == null) {
            singleton = new iFG();
        }
        return singleton;
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Locale.setDefault(new Locale(ResourceBundle.getBundle("config").
                getString("locale")));

        getSingleton().setVisible(true);

        Debug.info(Mensajes.format("iFG.Empezar")); //$NON-NLS-1$
    }

    private iFG() {
        SwingUtils.setupOutput(console, Color.WHITE, Color.RED);

        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            this.getRootPane().putClientProperty("apple.awt.brushMetalLook", //$NON-NLS-1$
                    Boolean.TRUE);
        } catch (Exception e) {
            // No hacer nada
        }

        this.jbInit();

        this.iFGExtra = new iFGExtra(this);

        new Thread() {

            @Override
            public void run() {
                try {
                    while (iFG.this.isVisible()) {
                        iFG.this.actualizarActivos();
                        Thread.sleep(250);
                    }
                } catch (InterruptedException e) {
                    Debug.error(e);
                }
            }

        }.start();

        String basePath = HardDiskWebPageProvider.getBasePath();
        String baseHtmlPathNg = HardDiskWebPageProvider.getHTMLPath();

        if (StringUtils.isBlank(basePath)) {
            JOptionPane.showMessageDialog(this, Mensajes.format(
                    "iFG.RutaBaseNoEncontrada"));
        }

        if (StringUtils.isBlank(baseHtmlPathNg)) {
            JOptionPane.showMessageDialog(this, Mensajes.format(
                    "iFG.RutaHtmlNoEncontrada"));
        }

        if (!new File(basePath).exists()) {
            JOptionPane.showMessageDialog(this, Mensajes.format(
                    "iFG.RutaBaseInvalida", basePath));
        }
        if (!new File(baseHtmlPathNg).exists()) {
            JOptionPane.showMessageDialog(this, Mensajes.format(
                    "iFG.RutaHtmlInvalida", baseHtmlPathNg));
        }

//        int port = iFG.preferencias.getInt("port", 8082);
//        this.servidorHttp = new ServidorHttp(this, port);
//        this.servidorHttp.start();
        actualizarActivos();
    }

    private void cargarEstado() {
        setLocation(preferencias.getInt("x", 25), preferencias.getInt("y", 25));
        setSize(preferencias.getInt("w", 1024), preferencias.getInt("h", 650));
        int csdl = preferencias.getInt("consoleSplit.dividerLocation", -1);
        consoleSplit.setDividerLocation(csdl);
        int psdl = preferencias.getInt("propertiesSplit.dividerLocation", -1);
        propertiesSplit.setDividerLocation(psdl);
    }

    private void guardarEstado() {
        preferencias.putInt("x", getLocation().x);
        preferencias.putInt("y", getLocation().y);
        preferencias.putInt("w", getSize().width);
        preferencias.putInt("h", getSize().height);
        int csdl = consoleSplit.getDividerLocation();
        preferencias.putInt("consoleSplit.dividerLocation", csdl);
        int psdl = propertiesSplit.getDividerLocation();
        preferencias.putInt("propertiesSplit.dividerLocation", psdl);
    }

    protected void resetearEstado() {
        preferencias.remove("x");
        preferencias.remove("y");
        preferencias.remove("w");
        preferencias.remove("h");
        preferencias.remove("consoleSplit.dividerLocation");
        preferencias.remove("propertiesSplit.dividerLocation");

        cargarEstado();
    }

    public PanelFilasElementos getPanelFilasElementos() {
        if (this.tabsWebPages.getTabCount() == 0) {
            return null;
        }

        return (PanelFilasElementos) this.tabsWebPages.getSelectedComponent();
    }

    public WebPage getWebPageActual() {
        if (getPanelFilasElementos() == null) {
            return null;
        }

        return getPanelFilasElementos().getWebPage();
    }

    public Collection<PanelFilasElementos> getPaneles() {
        return CollectionUtils.collect(
                Arrays.asList(tabsWebPages.getComponents()), new Transformer() {

            public Object transform(Object input) {
                return input;
            }

        });
    }

    public boolean isAbierto() {
        return this.getPanelFilasElementos() != null;
    }

    public iFGExtra getiFGExtra() {
        return this.iFGExtra;
    }

    private void jbInit() {
        // //////////////////////////////////////////////
        // VENTANA
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                iFG.this.salir(false);
            }

        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // //////////////////////////////////////////////
        // MENU Y BARRA DE TAREAS
        barraTareas = new BarraDeTareas(this);
        barraDeMenu = new Menu(this);
        setJMenuBar(barraDeMenu);
        barraDeMenu.actualizarRecientes(null);

        // //////////////////////////////////////////////
        // ACCIONES Y TOOLTIPS
        nuevaFila.addActionListener(this);
        nuevaFila.setActionCommand("nuevaFila"); //$NON-NLS-1$
        nuevaFila.setToolTipText(Mensajes.format("iFG.AgregarFila")); //$NON-NLS-1$

        borrarFila.addActionListener(this);
        borrarFila.setActionCommand("borrarFila"); //$NON-NLS-1$
        borrarFila.setToolTipText(Mensajes.format("iFG.EliminarFila")); //$NON-NLS-1$

        subirFila.addActionListener(this);
        subirFila.setActionCommand("subirFila"); //$NON-NLS-1$
        subirFila.setToolTipText(Mensajes.format("iFG.SubirFila")); //$NON-NLS-1$

        bajarFila.addActionListener(this);
        bajarFila.setActionCommand("bajarFila"); //$NON-NLS-1$
        bajarFila.setToolTipText(Mensajes.format("iFG.BajarFila")); //$NON-NLS-1$

        nuevoElemento.addActionListener(this);
        nuevoElemento.setActionCommand("nuevoElemento"); //$NON-NLS-1$
        nuevoElemento.setToolTipText(Mensajes.format("iFG.AgregarElemento")); //$NON-NLS-1$

        borrarElemento.addActionListener(this);
        borrarElemento.setActionCommand("borrarElemento"); //$NON-NLS-1$
        borrarElemento.setToolTipText(Mensajes.format("iFG.EliminarElemento")); //$NON-NLS-1$

        subirElemento.addActionListener(this);
        subirElemento.setActionCommand("subirElemento"); //$NON-NLS-1$
        subirElemento.setToolTipText(Mensajes.format("iFG.SubirElemento")); //$NON-NLS-1$

        bajarElemento.addActionListener(this);
        bajarElemento.setActionCommand("bajarElemento"); //$NON-NLS-1$
        bajarElemento.setToolTipText(Mensajes.format("iFG.BajarElemento")); //$NON-NLS-1$

        tabsWebPages.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                PanelFilasElementos pfe = iFG.this.getPanelFilasElementos();

                if (pfe != null && pfe.getArchivoActual() != null) {
                    fileDialog.setCurrentDirectory(pfe.getArchivoActual().getParentFile());
                    fileDialog.setSelectedFile(pfe.getArchivoActual());
                }

                actualizarActivos();
                actualizarCuentaErrores();
            }

        });

        // //////////////////////////////////////////////
        // CONTENIDO
        panelInferior.setLayout(new BorderLayout());
        panelInferior.add(panelBotonesFila, BorderLayout.WEST);
        panelInferior.add(panelBotonesElemento, BorderLayout.EAST);

        panelBotonesFila.setLayout(new FlowLayout());
        panelBotonesFila.add(nuevaFila);
        panelBotonesFila.add(borrarFila);
        panelBotonesFila.add(subirFila);
        panelBotonesFila.add(bajarFila);

        panelBotonesElemento.setLayout(new FlowLayout());
        panelBotonesElemento.add(nuevoElemento);
        panelBotonesElemento.add(borrarElemento);
        panelBotonesElemento.add(subirElemento);
        panelBotonesElemento.add(bajarElemento);

        panelCentral.setLayout(new BorderLayout());
        panelCentral.add(tabsWebPages, BorderLayout.CENTER);
        panelCentral.add(panelInferior, BorderLayout.SOUTH);

        propertiesScrollPane = new JScrollPane();
        propertiesScrollPane.setPreferredSize(new Dimension(300, 400));

        propertiesSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panelCentral, propertiesScrollPane);
        propertiesSplit.setBorder(BorderFactory.createEmptyBorder());
        propertiesSplit.setOneTouchExpandable(true);
        propertiesSplit.setResizeWeight(1);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(barraTareas, BorderLayout.NORTH);

        // //////////////////////////////////////////////
        // BARRA DE ESTADO
        barraEstado.setMinimumSize(new Dimension(20, 20));
        getContentPane().add(barraEstado, BorderLayout.SOUTH);

        labelEstado.setIcon(new ImageIcon(getClass().getResource("/org/freedesktop/tango/16x16/status/image-missing.png")));
        barraEstado.add(labelEstado, BorderLayout.WEST);

        labelValidaciones.setIcon(new ImageIcon(getClass().getResource("/org/freedesktop/tango/16x16/status/dialog-error.png")));
        labelValidaciones.setBorder(new EmptyBorder(5, 5, 5, 5));
        labelValidaciones.setVisible(false);
        labelValidaciones.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                validar();
            }

        });

        barraEstado.add(labelValidaciones, BorderLayout.EAST);
        barraEstado.setPreferredSize(new Dimension(800, 25));

        // //////////////////////////////////////////////
        // CONSOLA
        console.setBackground(new Color(0, 0, 0));
        console.setPreferredSize(new Dimension(100, 100));

        consoleScrollPane.getViewport().add(console);
        consoleScrollPane.setAutoscrolls(true);

        consoleSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                propertiesSplit, consoleScrollPane);
        consoleSplit.setBorder(BorderFactory.createEmptyBorder());
        consoleSplit.setOneTouchExpandable(true);
        consoleSplit.setResizeWeight(1);

        getContentPane().add(consoleSplit, BorderLayout.CENTER);

        cargarEstado();

        // //////////////////////////////////////////////
        // OTROS
        fileDialog.setFileFilter(new FileFilter() {

            public boolean accept(File pathname) {
                return pathname.getName().endsWith(EXTENSION);
            }

            @Override
            public String getDescription() {
                return "WebPage " + EXTENSION;
            }

        });
    }

    public void loadProperties(Object object) {
        propertiesScrollPane.getViewport().removeAll();
        if (object != null) {
            PropertiesTable propertiesTable
                    = new PropertiesTable(new JDialog(this), object);
            propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            propertiesScrollPane.getViewport().add(propertiesTable);
            actualizarActivos();
        }
    }

    private void actualizarActivos() {
        this.barraDeMenu.actualizarActivos(this);
        this.barraTareas.actualizarActivos(this);

        this.subirFila.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getContainerActual() != null
                && this.getPanelFilasElementos().getContainerActual().
                        getPosicion() > 0);
        this.bajarFila.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getContainerActual() != null
                && this.getPanelFilasElementos().getContainerActual().
                        getPosicion() < this.getPanelFilasElementos().getWebPage().size()
                - 1);
        this.nuevaFila.setEnabled(this.isAbierto());
        this.borrarFila.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getContainerActual() != null);

        this.subirElemento.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getWidgetActual() != null
                && this.getPanelFilasElementos().getWidgetActual().getPosicion()
                > 0);
        this.bajarElemento.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getWidgetActual() != null
                && this.getPanelFilasElementos().getWidgetActual().getPosicion()
                < this.getPanelFilasElementos().getContainerActual().size() - 1);
        this.nuevoElemento.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getContainerActual() != null);
        this.borrarElemento.setEnabled(this.isAbierto()
                && this.getPanelFilasElementos().getWidgetActual() != null);

        this.cambiarTitulo();
    }

    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        if (s == null) {
            return;
        }
        if (s.equals("nuevo")) { //$NON-NLS-1$
            Debug.info(Mensajes.format("iFG.Creando")); //$NON-NLS-1$
            new DelayThread() {

                @Override
                public void doAction() {
                    iFG.this.nuevoFile();
                }

            }.start();
        } else if (s.equals("abrir")) { //$NON-NLS-1$
            Debug.info(Mensajes.format("iFG.Abriendo")); //$NON-NLS-1$
            new DelayThread() {

                @Override
                public void doAction() {
                    iFG.this.abrirFile();
                }

            }.start();
        } else if (s.equals("editorLibre")) { //$NON-NLS-1$
            new EditorLibre(this, Mensajes.format("iFG.EditorLibre"), false).
                    setVisible(true); //$NON-NLS-1$
        } else if (s.equals("acercade")) { //$NON-NLS-1$
            new AcercaDe(this).setVisible(true);
        } else if (s.equals("limpiarLog")) { //$NON-NLS-1$
            this.console.setText(Mensajes.format("iFG.Listo")); //$NON-NLS-1$
        } else if (s.equals("preferencias")) { //$NON-NLS-1$
            new PreferencesDialog(new JDialog(this)).setVisible(true);
        } else if (this.isAbierto()) {
            if (s.equals("subirFila")) { //$NON-NLS-1$
                this.getPanelFilasElementos().moverFila(-1);
            } else if (s.equals("bajarFila")) { //$NON-NLS-1$
                this.getPanelFilasElementos().moverFila(1);
            } else if (s.equals("subirElemento")) { //$NON-NLS-1$
                this.getPanelFilasElementos().moverElemento(-1);
            } else if (s.equals("bajarElemento")) { //$NON-NLS-1$
                this.getPanelFilasElementos().moverElemento(1);
            } else if (s.equals("guardar")) { //$NON-NLS-1$
                Debug.info(Mensajes.format("iFG.Guardando")); //$NON-NLS-1$
                new DelayThread() {

                    @Override
                    public void doAction() {
                        iFG.this.guardarFile(false);
                    }

                }.start();
            } else if (s.equals("guardarComo")) { //$NON-NLS-1$
                Debug.info(Mensajes.format("iFG.Guardando")); //$NON-NLS-1$
                new DelayThread() {

                    @Override
                    public void doAction() {
                        iFG.this.guardarFile(true);
                    }

                }.start();
            } else if (s.equals("cerrar")) { //$NON-NLS-1$
                Debug.info(Mensajes.format("iFG.CerrandoFormulario")); //$NON-NLS-1$
                new DelayThread() {

                    @Override
                    public void doAction() {
                        iFG.this.cerrar(true);
                    }

                }.start();
            } else if (s.equals("transDocumento")) { //$NON-NLS-1$
                Debug.info(Mensajes.format("iFG.Transformando")); //$NON-NLS-1$

                new DelayThread() {

                    @Override
                    public void doAction() {
                        String path = iFG.this.getPanelFilasElementos().getArchivoActual().getPath();
                        iFG.this.transDocumento(path);
                    }

                }.start();
            } else if (s.equals("agregarImports")) { //$NON-NLS-1$
                //Debug.info(Mensajes.format("iFG.AbrirNavegador")); //$NON-NLS-1$

                new DelayThread() {

                    @Override
                    public void doAction() {
                        iFG.this.agregarImports();
                    }

                }.start();
            } else if (s.equals("cerrarTodo")) { //$NON-NLS-1$
                Debug.info(Mensajes.format("iFG.CerrandoFormulario")); //$NON-NLS-1$
                new DelayThread() {

                    @Override
                    public void doAction() {
                        iFG.this.cerrarTodo(true);
                    }

                }.start();
            } else {
                invoke(s);
            }
        } else {
            invoke(s);
        }
    }

    private void invoke(String s) throws Error {
        try {
            this.invoke(iFG.class, this, s);
        } catch (InvocationException e1) {
            try {
                this.invoke(PanelFilasElementos.class, this.
                        getPanelFilasElementos(), s);
            } catch (InvocationException e2) {
                throw new Error(e2);
            }
        }
    }

    private class InvocationException extends Exception {

        private static final long serialVersionUID = 1L;

        public InvocationException(Throwable t) {
            super(t);
        }

    }

    private void invoke(Class<?> c, Object object, String s)
            throws InvocationException {
        try {
            c.getDeclaredMethod(s).invoke(object);
        } catch (IllegalArgumentException e) {
            throw new InvocationException(e);
        } catch (SecurityException e) {
            throw new InvocationException(e);
        } catch (IllegalAccessException e) {
            throw new InvocationException(e);
        } catch (NoSuchMethodException e) {
            throw new InvocationException(e);
        } catch (InvocationTargetException e) {
            throw new Error(e.getTargetException());
        }
    }

    private boolean abrirFile() {
        this.fileDialog.showOpenDialog(this);
        if (this.fileDialog.getSelectedFile() != null) {
            this.abrirFile(this.fileDialog.getSelectedFile());
            return true;
        } else {
            Debug.info(Mensajes.format("iFG.AbriendoCancelado")); //$NON-NLS-1$
            return false;
        }
    }

    public void abrirFile(File file) {
        for (Component c : this.tabsWebPages.getComponents()) {
            PanelFilasElementos pfe = (PanelFilasElementos) c;
            if (file.equals(pfe.getArchivoActual())) {
                this.tabsWebPages.setSelectedComponent(c);
                return;
            }
        }
        this.fileDialog.setCurrentDirectory(file.getParentFile());
        this.fileDialog.setSelectedFile(file);
        this.crearBotonArchivo(file);
        this.cambiarTitulo();
        Debug.info(Mensajes.format("iFG.Abierto")); //$NON-NLS-1$
        this.barraDeMenu.actualizarRecientes(file);
        validar(false);
    }

    private void crearBotonArchivo(File archivo) {
        this.tabsWebPages.addTab("", new PanelFilasElementos(this, archivo));
        int index = this.tabsWebPages.getTabCount() - 1;
        this.tabsWebPages.setSelectedIndex(index);
        this.cambiarTitulo();
    }

    private boolean cerrar(boolean cancelable) {
        if (this.guardarAntes(cancelable)) {
            int index = this.tabsWebPages.getSelectedIndex();
            this.tabsWebPages.removeTabAt(index);
            Debug.info(Mensajes.format("iFG.Listo")); //$NON-NLS-1$

            return true;
        } else {
            Debug.info(Mensajes.format("iFG.CerrandoBotonCancelado")); //$NON-NLS-1$

            return false;
        }
    }

    private boolean cerrarTodo(boolean cancelable) {
        while (this.tabsWebPages.getTabCount() > 0) {
            if (!this.cerrar(cancelable)) {
                return false;
            }
        }

        return true;
    }

    private boolean transDocumento(String path) {
        WebPage wp = this.getWebPageActual();
        boolean isOnInit = wp.getNgOninit();
        TransformarNG tng = new TransformarNG();
        String path_ng = HardDiskWebPageProvider.getHTMLPath();
        try {
            String sOninit = wp.getInitialJS();
            if (isOnInit) {
                //sOninit = wp.getInitialJS();
                System.out.println(sOninit);
                if (!sOninit.contains("ngOnInit")) {
                    JOptionPane.showMessageDialog(this, Mensajes.format(
                            "iFG.OnInitNoimplementado"));
                    JOptionPane.showMessageDialog(this, "Documento no transformado. ");
                    return false;
                }

            } else {
             //   sOninit = wp.getInitialJS();
                if (sOninit.contains("ngOnInit")) {
                    JOptionPane.showMessageDialog(this, Mensajes.format(
                            "iFG.OnInitImplementado"));
                    JOptionPane.showMessageDialog(this, "Documento no transformado. ");
                    return false;
                }
            }
            WebPageEnviromentNG.setPath(HardDiskWebPageProvider.getHTMLPath());
            tng.process(path);
        } catch (ExcepcionParser e) {
            Debug.info("Algo salió mal al intentar parsear el documento");
        } catch (IOException e) {
            String component = "form" + this.getWebPageActual().getSubsystem().concat(this.getWebPageActual().getTransaction());
            int resp = JOptionPane.showConfirmDialog(this,
                    "No existe el componente " + component + " ¿Desea crearlo?",
                    "Crear Componente", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                tng.createComponent(path_ng, component);
                try {
                    tng.process(path);
                } catch (Exception ex) {
                    Debug.info("Algo salió mal al intentar guardar el archivo " + path);
                }

            }

        }

        return true;
    }

    private void cambiarTitulo() {
        if (!this.isAbierto()) {
            this.setTitle(Mensajes.format("iFG.Titulo")); //$NON-NLS-1$
        } else {
            String nombreArchivo = this.getPanelFilasElementos().
                    getArchivoActual() == null ? "Sin nombre" : this.
                                    getPanelFilasElementos().getArchivoActual().getName()
                            + " - " + this.getWebPageActual().getTitle();

            if (this.getPanelFilasElementos().hasUnsavedChanges()) {
                nombreArchivo += " *";
            }

            boolean hayCambios = false;
            for (Component pfe : this.tabsWebPages.getComponents()) {
                if (((PanelFilasElementos) pfe).hasUnsavedChanges()) {
                    hayCambios = true;
                    break;
                }
            }
            this.getRootPane().putClientProperty("Window.documentModified",
                    Boolean.valueOf(hayCambios));

            this.setTitle(Mensajes.format("iFG.TituloArchivo", nombreArchivo)); //$NON-NLS-1$
            this.tabsWebPages.setTitleAt(this.tabsWebPages.getSelectedIndex(),
                    nombreArchivo);
            this.tabsWebPages.invalidate();
        }
    }

    private final Set errores = new HashSet();

    private final Set warnings = new HashSet();

    public boolean validar(boolean esperar) {
        if (threadValidaciones == null) {
            final Collection<ValidationMessage> resultados
                    = new LinkedList<ValidationMessage>();

            threadValidaciones = new Thread() {

                @Override
                public void run() {
                    if (isAbierto()) {
                        PanelFilasElementos panelFilasElementos
                                = getPanelFilasElementos();
                        resultados.addAll(ValidationUtils.validate(
                                panelFilasElementos.getWebPage(),
                                new HardDiskWebPageProvider()));
                        warnings.clear();
                        errores.clear();
                        for (ValidationMessage message : resultados) {
                            Set donde;
                            if (message.getSeverity() == Severity.ERROR) {
                                donde = errores;
                            } else {
                                donde = warnings;
                            }
                            if (message.getValidatedObject() != null) {
                                donde.add(message.getValidatedObject());
                            } else {
                                donde.add(message.getWebElement());
                            }
                        }
                        panelFilasElementos.setWarningCount(warnings.size());
                        panelFilasElementos.setErrorCount(errores.size());
                        actualizarCuentaErrores();
                    }
                }

            };

            if (esperar) {
                threadValidaciones.run();
            } else {
                threadValidaciones.start();
            }

            return !errores.isEmpty();
        }

        return false;
    }

    public void buscar() {
    }

    public boolean hasError(Object value) {
        return errores.contains(value) || warnings.contains(value);
    }

    private void actualizarCuentaErrores() {
        if (isAbierto()) {
            int countWarnings = getPanelFilasElementos().getWarningCount();
            int countErrors = getPanelFilasElementos().getErrorCount();

            labelValidaciones.setText(
                    Mensajes.format("iFG.ErroresValidacion", countErrors, countWarnings));
            labelValidaciones.setVisible((countErrors + countWarnings) > 0);
        } else {
            labelValidaciones.setVisible(false);
        }
        threadValidaciones = null;
    }

    private boolean guardarFile(boolean pedirName) {
        File archivo = null;

        if (validar(true)) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Existen errores de validación, realmente desea continuar?",
                    "Errores de validacion", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                return false;
            }
        }

        if (pedirName
                || this.getPanelFilasElementos().getArchivoActual() == null) {
            this.fileDialog.showSaveDialog(this);

            if (this.fileDialog.getSelectedFile() != null) {
                archivo = this.fileDialog.getSelectedFile();
                if (!this.fileDialog.getSelectedFile().getName().endsWith(EXTENSION)) {
                    archivo = new File(archivo.getParent(), archivo.getName()
                            + EXTENSION);
                }
            } else {
                Debug.info(Mensajes.format("iFG.GuardandoCancelado")); //$NON-NLS-1$
                return false;
            }
        }

        if (archivo == null) {
            archivo = this.getPanelFilasElementos().getArchivoActual();
        }

        File nombreTecnico = new File(HardDiskWebPageProvider.getPath(this.
                getWebPageActual().getSubsystem(), this.getWebPageActual().
                        getTransaction()));
        Debug.info("Nombre tecnico " + nombreTecnico + " archivo " + archivo.getPath());
        if (StringUtils.isNotEmpty(this.getWebPageActual().getURI())
                && !nombreTecnico.equals(archivo)) {
            int res = JOptionPane.showConfirmDialog(this, Mensajes.format(
                    "iFG.GuardarNombreActual", archivo, nombreTecnico), //$NON-NLS-1$
                    Mensajes.format("iFG.CambiarNombreDialogo"), //$NON-NLS-1$
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (res == JOptionPane.NO_OPTION) {
                archivo = nombreTecnico;
            } else if (res == JOptionPane.CANCEL_OPTION) {
                Debug.info(Mensajes.format("iFG.GuardandoCancelado")); //$NON-NLS-1$
                return false;
            }
        }

        try {
            SerializadorXml s = new SerializadorXml();
            OutputStream os = new FileOutputStream(archivo);
            s.serializar(this.getWebPageActual(), os);
            os.close();

            this.getPanelFilasElementos().setArchivoActual(archivo);
            this.getPanelFilasElementos().setSaveState();
            this.actualizarActivos();
            this.barraDeMenu.actualizarRecientes(archivo);

            Debug.info(Mensajes.format(
                    "iFG.ArchivoGuardado", archivo.getAbsolutePath())); //$NON-NLS-1$

            this.fileDialog.setSelectedFile(this.getPanelFilasElementos().
                    getArchivoActual());

            this.cambiarTitulo();

        } catch (FileNotFoundException e) {
            Debug.error(Mensajes.format("iFG.ErrorSoloLectura", //$NON-NLS-1$
                    e.toString()), e);
            return false;
        } catch (Exception e) {
            Debug.error(Mensajes.format(
                    "iFG.ErrorFormularioConProblemas", e.toString()), e); //$NON-NLS-1$

            return false;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea tranformar el documento?",
                "Transformar Documento.", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            transDocumento(archivo.getPath());
        }

        return true;
    }

    private void nuevoFile() {
        this.crearBotonArchivo(null);

        this.editarFormulario();

        Debug.info(Mensajes.format("iFG.Nuevo")); //$NON-NLS-1$
    }

    private void editarFormulario() {
        Debug.info(Mensajes.format("iFG.EditandoFormulario")); //$NON-NLS-1$

        new EditorPropiedades(new JDialog(this), this.getWebPageActual()).
                setVisible(true);

        Debug.info(Mensajes.format("iFG.FormularioEditado")); //$NON-NLS-1$
    }

    protected void editarCalculos() {
        Debug.info(Mensajes.format("iFG.EditandoCalculos")); //$NON-NLS-1$
        EditorJavascript editorJavascript = new EditorJavascript(this, Tipo.SIMPLE);
        editorJavascript.setTitle("Editar Cálculos");
        this.getWebPageActual().setCalculos(editorJavascript.editar(this.getWebPageActual().getCalculos()));

        this.getPanelFilasElementos().saveUndoState();

        Debug.info(Mensajes.format("iFG.EditandoCalculosCompleto")); //$NON-NLS-1$
    }

    protected void agregarImports() {
        Debug.info(Mensajes.format("iFG.AddImports")); //$NON-NLS-1$
        EditorJavascript addImport = new EditorJavascript(this, Tipo.SIMPLE);
        addImport.setValidate(false);
        addImport.setTitle("Agregar Imports");
        this.getWebPageActual().setImports(addImport.editar(this.getWebPageActual().getImports()));
        this.getPanelFilasElementos().saveUndoState();
        Debug.info(Mensajes.format("iFG.AddImportsCompleto")); //$NON-NLS-1$
    }
    
    protected void agregarExports() {
        Debug.info(Mensajes.format("iFG.AddExports")); //$NON-NLS-1$
        EditorJavascript addExport = new EditorJavascript(this, Tipo.SIMPLE);
        addExport.setValidate(false);
        addExport.setTitle("Agregar Exports");
        this.getWebPageActual().setExports(addExport.editar(this.getWebPageActual().getExports()));
        this.getPanelFilasElementos().saveUndoState();
        Debug.info(Mensajes.format("iFG.AddExportsCompleto")); //$NON-NLS-1$
    }

    protected void validar() {
        Debug.info("Iniciando validación...");
        new ValidationDialog(this, this.getWebPageActual()).setVisible(true);
        Debug.info("Validación completa.");
        this.getPanelFilasElementos().saveUndoState();
    }

//    protected void abrirNavegador() {
//        Servicios.abrirNavegador(this.servidorHttp.getUrl().toString());
//    }

    public boolean salir(boolean cancelable) {
        boolean cambios = false;

        for (Component c : this.tabsWebPages.getComponents()) {
            PanelFilasElementos pfe = (PanelFilasElementos) c;

            if (pfe.hasUnsavedChanges()) {
                cambios = true;
                break;
            }
        }

        if (cambios) {
            int salir = cancelable ? JOptionPane.YES_NO_CANCEL_OPTION
                    : JOptionPane.YES_NO_OPTION;

            int res = JOptionPane.showConfirmDialog(this, Mensajes.format(
                    "iFG.PreguntaGuardarSalir"), //$NON-NLS-1$
                    Mensajes.format("iFG.GuardarSalirDialogo"), //$NON-NLS-1$
                    salir, JOptionPane.QUESTION_MESSAGE);

            if (res == JOptionPane.CANCEL_OPTION) {
                return false;
            } else if (res == JOptionPane.OK_OPTION) {
                if (!this.cerrarTodo(cancelable)) {
                    return false;
                }
            }
        }

        try {
          //  this.servidorHttp.stop();
            this.guardarEstado();
            System.exit(0);
        } catch (Exception e) {
            Debug.error(e);
            System.exit(1);
        }

        return true;
    }

    private boolean guardarAntes(boolean cancelable) {
        if (!this.getPanelFilasElementos().hasUnsavedChanges()) {
            return true;
        } else if (this.getPanelFilasElementos().getArchivoActual() != null) {
            return this.guardarFile(false);
        }

        int guardar = JOptionPane.showConfirmDialog(this, Mensajes.format(
                "iFG.PreguntaGuardarAntesDeContinuar"),
                Mensajes.format("iFG.GuardarDialogo"), //$NON-NLS-1$
                cancelable ? JOptionPane.YES_NO_CANCEL_OPTION
                        : JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        switch (guardar) {
            case JOptionPane.YES_OPTION:
                return this.guardarFile(false);
            case JOptionPane.NO_OPTION:
                return true;
            default:
                return !cancelable;
        }
    }

    private abstract class DelayThread extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(150);
                this.doAction();
            } catch (InterruptedException e) {
                Debug.error(e);
            }
        }

        public abstract void doAction();

    }

}
