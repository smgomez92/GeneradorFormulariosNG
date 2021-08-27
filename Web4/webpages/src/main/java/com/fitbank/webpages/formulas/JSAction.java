package com.fitbank.webpages.formulas;

import org.parboiled.Action;
import org.parboiled.Context;

/**
 * Regla que puede convertirse en JS
 *
 * @author FitBank CI
 */
public abstract class JSAction implements Action {

    public abstract String toJS(Context context)  ;

    public boolean run(Context context) {
        context.setNodeValue(toJS(context));
        return true;
    }

}
