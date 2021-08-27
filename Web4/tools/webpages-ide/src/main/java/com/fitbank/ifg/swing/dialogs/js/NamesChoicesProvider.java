package com.fitbank.ifg.swing.dialogs.js;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.fife.ui.autocomplete.*;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;

/**
 * Provee una lista con los nombres de los widgets en el webPage actual.
 * @author Fitbank RB
 */
public class NamesChoicesProvider implements ParameterChoicesProvider {
    
    private List widgetNames = new ArrayList(50);
    
    public NamesChoicesProvider() {
        WebPage webPage = UtilsEditorJavascript.getFullWebPage();
        
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            if (StringUtils.isNotBlank(formElement.getName())) {
                widgetNames.add(new BasicCompletion(null, "'"
                        + formElement.getName() + "'"));
            }
        }
    }

    @Override
    public List getParameterChoices(JTextComponent jtc, Parameter prmtr) {
        if (prmtr instanceof FitbankParameter) {
            FitbankParameter parameter = (FitbankParameter) prmtr;
            if (parameter.getTag().equals(FitbankParameter.WIDGET_NAMES)) {
                return Collections.unmodifiableList(widgetNames);
            }
        }
        
        return Collections.EMPTY_LIST;
    }
    
}
