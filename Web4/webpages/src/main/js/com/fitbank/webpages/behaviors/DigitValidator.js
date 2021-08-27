include("lib.prototype");

DigitValidator.cedulaEcuador = function(elemento) {
    var message = "";
    if (elemento.value.length == 0) {
        Validar.ok(elemento, this.constructor.simpleClassName);
        return  true;
    } else if (elemento.value.length == 10) {
        var rut = elemento.value.substring(0, 9);
        var verificador = elemento.value.substring(9, 10);
        var count = 0;
        var count2 = 0;
        var factor = 2;
        var suma = 0;
        var sum = 0;
        var digito = 0;
        count2 = rut.length - 1;

        while (count < rut.length) {
            sum = factor * (parseInt(rut.substr(count2, 1), 10));
            if (sum > 9) {
                sum -= 9;
            }

            suma += sum;
            count++;
            count2--;
            if (factor == 1) {
                factor = 2;
            } else {
                factor = 1;
            }
        }

        digito = 10 - (suma % 10);
        digito = digito == 10 ? 0 : digito;

        Validar.ok(elemento, this.constructor.simpleClassName);
        if (digito != verificador) {
            message = Mensajes["com.fitbank.webpages.behaviors.DigitValidator.ERROR_CEDULA"];
            Validar.error(elemento, message, this.constructor.simpleClassName);
        }
    } else {
        message = Mensajes["com.fitbank.webpages.behaviors.DigitValidator.LONGITUD_CEDULA"];
        Validar.ok(elemento, this.constructor.simpleClassName);
        Validar.error(elemento, message, this.constructor.simpleClassName);
    }
};

DigitValidator.rucEcuador = function(elemento) {
    var message = "";
    if (elemento.value.length == 0) {
        Validar.ok(elemento, this.constructor.simpleClassName);
    } else if (elemento.value.length < 11)	{
        message = Mensajes["com.fitbank.webpages.behaviors.DigitValidator.LONGITUD_RUC"];
        Validar.error(elemento, message, this.constructor.simpleClassName);
    } else {
        var	modulo11 = new Array(9);
        var	valorRetorno = true;
        var	verif =	parseFloat("0");
        if (parseInt(elemento.value.substring(0,2),10) < parseInt(1,10) ||
            parseInt(elemento.value.substring(0,2),10)	> parseInt(22,10)) {
            valorRetorno = false;
        } else if (parseInt(elemento.value.substring(2,3),10) < parseInt(0,10) 
            || (parseInt(elemento.value.substring(2,3),10) > parseInt(6,10) 
            && parseInt(elemento.value.substring(2,3),10) != parseInt(9,10)))	{
            valorRetorno = false;
        } else {
            if (parseInt(elemento.value.substring(2, 3),10) ==	parseInt(9,10))	{
                //sociedad privada o extranjeros
                if (elemento.value.substring(10, 13) != "001")
                    valorRetorno = false;
                else {
                    modulo11 = [4,3,2,7,6,5,4,3,2];
                    for (var i = 0 ; i < 9 ; i++) {
                        verif = verif + (parseFloat(elemento.value.substring(i, (i + 1)))
                            * (parseFloat(modulo11[i])));
                    }

                    if (verif % 11 == 0) {
                        valorRetorno = parseInt(elemento.value.substring(9, 10),10) == 0;
                    } else {
                        valorRetorno = (11 - (verif % 11)) == parseInt(elemento.value.substring(9, 10),10);
                    }
                }
            } else if (parseInt(elemento.value.substring(2,3),10) == 6) {
                //sociedad públicas
                if(elemento.value.substring(10, 13) != "001") {
                    valorRetorno = false;
                } else {
                    modulo11 = [3,2,7,6,5,4,3,2];
                    for (var i = 0 ; i < 8 ; i++) {
                        verif = verif + (parseFloat(elemento.value.substring(i, (i + 1))) 
                            * (parseFloat(modulo11[i])));
                    }

                    if (verif % 11 == 0) {
                        valorRetorno = parseInt(elemento.value.substring(8, 9),10) == 0;
                    } else {
                        valorRetorno = (11	- (verif % 11))	== parseInt(elemento.value.substring(8, 9),10);
                    }
                }
            } else if (parseInt(elemento.value.substring(2,3),10) < 6 &&
                parseInt(elemento.value.substring(2,3),10) >= 0) {
                //personas naturales
                if (elemento.value.substring(10,13) != "001") {
                    valorRetorno = false;
                } else {
                    modulo11 = [2,1,2,1,2,1,2,1,2];
                    for (var i = 0 ; i < 9 ; i++) {
                        var temp = parseInt(elemento.value.substring(i,(i + 1)), 10) * parseInt((modulo11[i]), 10);
                        if (temp > 9) {
                            temp -= 9;
                        }
                        verif += temp;
                    }

                    if (verif % 10 == 0) {
                        valorRetorno = parseInt(elemento.value.substring(9, 10),10) == 0;
                    } else {
                        valorRetorno = (10 - (verif % 10)) == parseInt(elemento.value.substring(9, 10), 10);
                    }
                }
            }
        }

        Validar.ok(elemento, this.constructor.simpleClassName);
        if(!valorRetorno) {
            message = Mensajes["com.fitbank.webpages.behaviors.DigitValidator.ERROR_RUC"];
            Validar.error(elemento, message, this.constructor.simpleClassName);
        }
    }
};

/**
 * @remote
 */
DigitValidator.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {

        elemento.on("blur", (function(elemento) {
            switch(this.validationType) {
                case DigitValidator$ValidationTypes.ID_ECUADOR:
                    this._cedulaEcuador(elemento);
                    break;
                case DigitValidator$ValidationTypes.RUC_ECUADOR:
                    this._rucEcuador(elemento);
                    break;
                default:
                    Estatus.mensaje("Validacion no definida", null, "error");
                    break;
            }
        }).bind(this, elemento));
    },
    
    _cedulaEcuador: DigitValidator.cedulaEcuador,
    
    _rucEcuador: DigitValidator.rucEcuador

});