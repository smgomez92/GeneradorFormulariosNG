package com.fitbank.serializador.xml;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface XML {

    boolean usarTag() default false;

    String nombre() default "";

    String nombreSubitems() default "item";

    boolean ignore() default false;

}
