include("lib.prototype");
include("lib.onload");

var CSS3Hacks = {

    STYLESHEET_ID: "__css3hacks__",

    IMPORT_REGEX: /@import ["'](.*)["'];/i,

    VARIABLES_REGEX: /@variables/i,

    VARIABLE_REGEX: /^\s*(.*)\s*:\s*(.*)\s*;\s*/i,

    VAR_REGEX: /var\(([^\)]+)\)/i,

    BR_REGEX: /border-radius/i,

    variables: $H(),

    init: function() {
        for (var s = 0; s < document.styleSheets.length; s++) {
            CSS3Hacks.process(document.styleSheets[s].href);
        }
    },

    process: function(file) {
        file && new Ajax.Request(file, {
            method: "GET",
            onComplete: CSS3Hacks.parse,
            asynchronous: false  // Should not block, file is in cache, but
                                 // needed in order to parse files in order
        });
    },

    parse: function(response) {
        var css = response.responseText.split("\n");
        var selector = "";
        for (var l = 0; l < css.length; l++) {
            // Process imports
            var import_res = CSS3Hacks.IMPORT_REGEX.exec(css[l]);
            if (import_res && import_res.length > 1 && import_res[1].length > 0) {
                var url = response.request.url;
                url = url.substring(0, url.lastIndexOf("/") + 1) + import_res[1];
                CSS3Hacks.process(url);
                continue;
            }

            // Process variables definition
            if (CSS3Hacks.VARIABLES_REGEX.test(css[l])) {
                while (css[++l].indexOf("}") == -1) {
                    var variable_res = CSS3Hacks.VARIABLE_REGEX.exec(css[l]);
                    if (variable_res) {
                        CSS3Hacks.variables.set(variable_res[1], variable_res[2]);
                    }
                }
                continue;
            }

            // Process border radius
            var open = css[l].indexOf("{");
            if (open > 0) {
                selector = css[l].substring(0, open);
                selector = selector.replace(/^\s*/, '').replace(/\s*$/, '');
            }
            if (CSS3Hacks.BR_REGEX.test(css[l])) {
                CSS3Hacks.createCSS(selector,
                        css[l].replace(CSS3Hacks.BR_REGEX, "-moz-border-radius") +
                        css[l].replace(CSS3Hacks.BR_REGEX, "-webkit-border-radius") +
                        css[l].replace(CSS3Hacks.BR_REGEX, "-khtml-border-radius")
                        /*+ css[l].replace(CSS3Hacks.BR_REGEX, "-ms-border-radius")
                        + "behavior:url(css/border-radius.htc);"*/);
            }

            // Process variables
            var var_res = CSS3Hacks.VAR_REGEX.exec(css[l]);
            if (var_res) {
                CSS3Hacks.createCSS(selector, css[l].replace(CSS3Hacks.VAR_REGEX,
                        CSS3Hacks.variables.get(var_res[1])));
            }
        }
    },

    getRule: function(selectorText) {
        for ( var s = 0; s < document.styleSheets.length; s++) {
            var rules = document.styleSheets[s].rules
                    || document.styleSheets[s].cssRules;

            for ( var r = 0; r < rules.length; r++) {
                if (rules[r].selectorText == selectorText) {
                    return rules[r];
                }
            }
        }

        return null;
    },

    createCSS: function(selector, declaration) {
        var styleElement = $(CSS3Hacks.STYLESHEET_ID);

        if (!styleElement) {
            styleElement = document.createElement("style");
            styleElement.setAttribute("id", CSS3Hacks.STYLESHEET_ID);
            styleElement.setAttribute("type", "text/css");
            styleElement.setAttribute("media", "screen");
            document.getElementsByTagName("head")[0].appendChild(styleElement);
        }

        if (!Prototype.Browser.IE) {
            styleElement.appendChild(document.createTextNode(selector + " {"
                    + declaration + "}"));
        } else {
            var last = document.styleSheets[document.styleSheets.length - 1];
            last.addRule(selector, declaration);
        }
    }

};

addOnLoad(CSS3Hacks.init);
