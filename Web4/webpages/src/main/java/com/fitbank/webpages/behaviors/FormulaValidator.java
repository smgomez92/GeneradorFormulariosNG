package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import org.apache.commons.lang.StringUtils;

import com.fitbank.js.LiteralJS;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.formulas.FormulaParser;

/**
 * Valida un campo dado una formula
 *
 * @author FitBank
 */
public class FormulaValidator extends AbstractJSBehaivor {

    @Editable
    private boolean validateEmpty = true;

    @Editable
    private String formula = "";

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean getValidateEmpty() {
        return validateEmpty;
    }

    public void setValidateEmpty(boolean validateEmpty) {
        this.validateEmpty = validateEmpty;
    }

    @XML(ignore = true)
    public LiteralJS getFormulaJS() {
        return new LiteralJS(StringUtils.isBlank(getFormula()) ? "null"
                : FormulaParser.parse(null, getFormula()).getJavaScript());
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getFormula() + ")";
    }

}
