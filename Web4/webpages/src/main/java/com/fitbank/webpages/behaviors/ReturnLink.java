package com.fitbank.webpages.behaviors;

import com.fitbank.js.JS;
import com.fitbank.util.Editable;

public class ReturnLink extends Link {

    @Override
    @Editable(ignore = true)
    @JS(ignore = true)
    public String getSubsystem() {
        return "";
    }

    @Override
    @Editable(ignore = true)
    @JS(ignore = true)
    public String getTransaction() {
        return "";
    }
}
