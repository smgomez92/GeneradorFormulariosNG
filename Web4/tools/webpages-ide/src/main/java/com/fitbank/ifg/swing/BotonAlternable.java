package com.fitbank.ifg.swing;

import javax.swing.JToggleButton;

public class BotonAlternable extends JToggleButton {

    private static final long serialVersionUID = 1L;

    public BotonAlternable(String stockId, String imageFile, boolean toolbar) {
        super(Boton.load(stockId, imageFile, toolbar));
    }

    public BotonAlternable(String stockId, String imageFile, String text,
            boolean toolbar) {
        super(Boton.load(stockId, imageFile, toolbar));
        putClientProperty("JButton.buttonType", "toolbar");
        setText(text);
        setToolTipText(text);
    }

}