package com.fitbank.ifg.swing.dialogs.js;

import com.fitbank.util.Debug;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.autocomplete.VariableCompletion;

/**
 * Clase que se encarga de mostrar las opciones de autocompletado con un
 * estilo personalizado.
 * 
 * @author Fitbank RB
 */
public class CustomCellRenderer extends DefaultListCellRenderer {
    
    private ImageIcon propertyIcon = null;
    
    private ImageIcon methodIcon = null;
    
    private ImageIcon templateIcon = null;
    
    private ImageIcon completionIcon = null;
    
    public CustomCellRenderer() {
        setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        methodIcon = getImage("method.png");
        propertyIcon = getImage("property.png");
        templateIcon = getImage("template.png");
        completionIcon = getImage("fastCompletion.png");
    }
    
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean selected, boolean hasFocus) {
        
        super.getListCellRendererComponent(list, value, index, selected, hasFocus);
        
        if (value instanceof FunctionCompletion) {
            renderFunction((FunctionCompletion) value);
        } else if (value instanceof VariableCompletion) {
            renderProperty((VariableCompletion) value);
        } else if (value instanceof ShorthandCompletion) {
            ShorthandCompletion completion = (ShorthandCompletion) value;
            setText(completion.getInputText());
            setIcon(templateIcon);
        } else if (value instanceof Completion) {
            Completion completion = (Completion) value;
            setText(completion.getInputText());
            setIcon(completionIcon);
        } else {
            setText(String.valueOf(value));
            setIcon(null);
            
            if (value == null) {
                Debug.error("No se pudo renderizar valor nulo!");
                return this;
            }
            
            //Nunca debería caer aquí...
            Debug.warn("No se pudo renderizar completado para: "
                    + value.getClass().getName());
        }
        
        return this;
    }
    
    private void renderFunction(FunctionCompletion func) {
        String paramStart = "(";
        String paramSeparator = ", ";
        String paramEnd = ")";
        
        if (func.getProvider() != null) {
            paramStart = "" + func.getProvider().getParameterListStart();
            paramSeparator = func.getProvider().getParameterListSeparator();
            paramEnd = "" + func.getProvider().getParameterListEnd();
        }
        
        StringBuilder builder = new StringBuilder(256);
        builder.append(func.getName()).append(paramStart);
        
        for (int i = 0; i < func.getParamCount(); i++) {
            Parameter param = func.getParam(i);
            String type = StringUtils.defaultIfEmpty(param.getType(), "");
            String name = param.getName();
            builder.append(type).append(" ").append(name);
            
            if (i < func.getParamCount() - 1) {
                builder.append(paramSeparator);
            }
        }
        
        builder.append(paramEnd).append(" : ").append(func.getType());
        
        setText(builder.toString());
        setIcon(methodIcon);
    }
    
    private void renderProperty(VariableCompletion var) {
        StringBuilder builder = new StringBuilder(128);
        builder.append(var.getName());
        
        if (StringUtils.isNotBlank(var.getType())) {
            builder.append(" : ").append(var.getType());
        }
        
        setText(builder.toString());
        setIcon(propertyIcon);
    }
    
    private ImageIcon getImage(String name) {
        InputStream is = getClass().getResourceAsStream("/com/fitbank/ifg/iconos/" + name);
        
        if (is == null) {
            Debug.error("No se encontró imagen " + name);
            return null;
        }
        
        byte[] imageData = null;
        
        try {
            imageData = IOUtils.toByteArray(is);
            return new ImageIcon(imageData);
        } catch (IOException ioe) {
            Debug.error(ioe);
            return null;
        }
    }
}
