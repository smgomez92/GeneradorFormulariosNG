__onload__ = false;

addOnLoad = function(f) {
    if (__onload__) {
        f();
    } else {
        document.observe("dom:loaded", f);
    }
}

addOnLoad(function() { __onload__ = true; });

