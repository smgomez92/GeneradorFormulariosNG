package com.fitbank.ifg.swing;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
public class Boton extends JButton {
   private static final long serialVersionUID = 1L;
   public Boton(String stockName, String imageFile, boolean toolbar) {
       super(load(stockName, imageFile, toolbar));
   }
   public Boton(String stockName, String imageFile, String text, String accion,
           boolean toolbar) {
       super(load(stockName, imageFile, toolbar));
       putClientProperty("JButton.buttonType", "toolbar");
       setText(text);
       setToolTipText(text);
       setActionCommand(accion);
   }
   public static ImageIcon load(String stockName, String imageFile,
           boolean toolbar) {
       URL resource = Thread.currentThread().getContextClassLoader().getResource(
               "com/fitbank/ifg/iconos/" + imageFile);
       if (resource == null) {
           resource = Thread.currentThread().getContextClassLoader().getResource(
                   String.format("org/freedesktop/tango/16x16/%s", imageFile));
       }
       return new ImageIcon(resource);
   }
}