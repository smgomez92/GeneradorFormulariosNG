Element.addMethods("INPUT", (function() {
    var changeHandler = function(e) {
        if (this.greyed || this.checked) {
            this.setGreyed(!this.greyed);
            if (!this.greyed) {
                this.checked = true;
            }
        }
    };

    function setGreyed(checkbox, greyed) {
        checkbox.greyed = typeof greyed == "undefined" ? true : !!greyed;
        checkbox.setOpacity(checkbox.greyed ? 0.33 : 1);
        if (checkbox.greyed) {
            checkbox.checked = checkbox.greyedState;
        }            
    }

    function setGreyable(element, enable) {
        if (!(element = $(element))) return;
        
        if (!(/checkbox|radiobutton/i.test(element.type))) {
            return;
        }

        if (typeof element.greyedState == "undefined" || enable) {
            element.observe("change", changeHandler);
            element.setGreyState(false);
            element.setGreyed(false);
        } else {
            element.stopObserving("change", changeHandler);
            element.setGreyed(false);
            element.checked = element.greyedState; 
            element.greyedState = undefined;
            element.greyed = undefined;
        }
    }

    function setGreyState(element, greyedState) {
        if (!(element = $(element))) return;
        
        if (!(/checkbox|radiobutton/i.test(element.type))) {
            return;
        }

        element.greyedState = typeof greyedState == "undefined" ? true :
            !!greyedState;
    }

    return {
        setGreyed: setGreyed,
        setGreyable: setGreyable,
        setGreyState: setGreyState
    }
})());
