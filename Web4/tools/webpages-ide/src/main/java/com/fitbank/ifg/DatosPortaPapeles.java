package com.fitbank.ifg;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.webpages.WebElement;

public class DatosPortaPapeles implements Transferable {

    public final static DataFlavor DATAFLAVOR_IFG = new DataFlavor(Object.class,
            "iFG Object");

    private static Clipboard systemClipboard =
            Toolkit.getDefaultToolkit().getSystemClipboard();

    private Object o;

    public static void copy(Object object, ClipboardOwner owner) {
        Transferable t = new DatosPortaPapeles(object);

        systemClipboard.setContents(t, owner);
    }

    public static boolean isAvailable(Class aClass) {
        return get(aClass) != null;
    }

    public static <T> T get(Class T) {
        if (!systemClipboard.isDataFlavorAvailable(DATAFLAVOR_IFG)) {
            return null;
        }

        try {
            Object o = Clonador.clonar(systemClipboard.getData(
                    DatosPortaPapeles.DATAFLAVOR_IFG));

            if (o instanceof WebElement) {
                ((WebElement) o).resetId();
            }

            if (T.isInstance(o)) {
                return (T) o;
            } else {
                return null;
            }

        } catch (UnsupportedFlavorException ex) {
            // Ignorar
        } catch (IllegalStateException ex) {
            // Ignorar
        } catch (IOException ex) {
            Debug.error(ex);
        } catch (Throwable t) {
            Debug.error(t);
        }

        return null;
    }

    public DatosPortaPapeles(Object o) {
        setObject(o);
    }

    public final void setObject(Object o) {
        this.o = Clonador.clonar(o);
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { DATAFLAVOR_IFG };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DATAFLAVOR_IFG);
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return o;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

}
