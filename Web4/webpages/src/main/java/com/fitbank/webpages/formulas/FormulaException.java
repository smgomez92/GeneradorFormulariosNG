package com.fitbank.webpages.formulas;


public class FormulaException extends Exception{

    public FormulaException(){
        super();
    }

    public FormulaException(String message){
        super(message);
    }

    public FormulaException(Throwable cause){
        super(cause);
    }
}