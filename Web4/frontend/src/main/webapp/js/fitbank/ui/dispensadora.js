include("lib.prototype");

var Dispensadora = {
    element: null,
    ingresaNumerosNaturales: function(e, elemento, siguienteElemento,
            anteriorElemento, monto) {
        if (e.keyCode == 9) {
            if (e.shiftKey) {
                $(anteriorElemento).focus();
            } else {
                $(siguienteElemento).focus();
            }
            Event.stop(e);
        } else if (e.keyCode > 47 && e.keyCode < 58) {// números normales
            $("t" + elemento.name).value = Dispensadora.calculo(elemento,
                    String.fromCharCode(e.keyCode) * 1, monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 96) {// cero
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 0,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 97) {// uno
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 1,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 98) {// dos
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 2,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 99) {// tres
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 3,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 100) {// cuatro
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 4,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 101) {// cinco
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 5,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 102) {// seis
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 6,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 103) {// siete
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 7,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 104) {// ocho
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 8,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 105) {// nueve
            $("t" + elemento.name).value = Dispensadora.calculo(elemento, 9,
                    monto);
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 8) {// borrar
            if (elemento.value != "" && elemento.value.length > 0) {
                subtotal = (elemento.value.substring(0,
                        (elemento.value.length - 1)) * 1)
                        * monto;
            } else {
                subtotal = 0.00;
            }
            subtotal = Dispensadora.formatoNumero(subtotal);
            $("t" + elemento.name).value = subtotal;
            Dispensadora.sumaTotal();
        } else if (e.keyCode == 114) {// F3
            if (Prototype.Browser.IE) {
                window.event.keyCode = 0;
                window.event.returnValue = false;
            } else {
                e.preventDefault();
            }
            Event.stop(e);
            window.close();
        } else if (e.keyCode > 111 && e.keyCode < 124) {// desabilito funciones
            if (Prototype.Browser.IE) {
                window.event.keyCode = 0;
                window.event.returnValue = false;
            } else {
                e.preventDefault();
            }
            Event.stop(e);
        } else if (e.keyCode == 13) {// Enter
            Dispensadora.aceptar();
        } else if (e.keyCode == 27) {// Esc
            window.close();
        } else if (e.keyCode > 34 && e.keyCode < 41) {// fin-inicio-izquierda-arriba-derecha-abajo
        } else {
            Event.stop(e);
        }
    },
    /**
     * Función que permite el ingreso de numeros
     */
    ingresoNumeros: function(e, elemento, siguienteElemento, anteriorElemento,
            monto) {
        if (e.keyCode == 9) {
            if (e.shiftKey) {
                $(anteriorElemento).focus();
            } else {
                $(siguienteElemento).focus();
            }
            Event.stop(e);
        } else if (e.keyCode > 47 && e.keyCode < 58) {// números normales
        } else if (e.keyCode == 96) {// cero
        } else if (e.keyCode == 97) {// uno
        } else if (e.keyCode == 98) {// dos
        } else if (e.keyCode == 99) {// tres
        } else if (e.keyCode == 100) {// cuatro
        } else if (e.keyCode == 101) {// cinco
        } else if (e.keyCode == 102) {// seis
        } else if (e.keyCode == 103) {// siete
        } else if (e.keyCode == 104) {// ocho
        } else if (e.keyCode == 105) {// nueve
        } else if (e.keyCode == 8) {// borrar
        } else if (e.keyCode == 114) {// F3
            if (Prototype.Browser.IE) {
                window.event.keyCode = 0;
                window.event.returnValue = false;
            } else {
                e.preventDefault();
            }
            Event.stop(e);
            window.close();
        } else if (e.keyCode > 111 && e.keyCode < 124) {// desabilito funciones
            if (Prototype.Browser.IE) {
                window.event.keyCode = 0;
                window.event.returnValue = false;
            } else {
                e.preventDefault();
            }
            Event.stop(e);
        } else if (e.keyCode == 110 || e.keyCode == 190) {// punto
        } else if (e.keyCode == 188) {// coma
        } else if (e.keyCode == 13) {// Enter
            Dispensadora.aceptar();
        } else if (e.keyCode == 27) {// Esc
            window.close();
        } else if (e.keyCode > 34 && e.keyCode < 41) {// fin-inicio-izquierda-arriba-derecha-abajo
        } else {
            Event.stop(e);
        }
    },
    /**
     * Función que se encarga de quitar las comas y comilla simple de un n�mero
     * para que puedan operar sin problemas
     */
    elimarComas: function(monto) {
        nuevoNumeroMiles = "";
        separador = monto.split(",");
        if (separador.length > 1) {
            for (i = 0; i < separador.length; i++) {
                nuevoNumeroMiles += separador[i];
            }
        } else {
            nuevoNumeroMiles = monto;
        }
        nuevoNumero = "";
        separador = nuevoNumeroMiles.split("'");
        if (separador.length > 1) {
            for (i = 0; i < separador.length; i++) {
                nuevoNumero += separador[i];
            }
        } else {
            nuevoNumero = nuevoNumeroMiles;
        }
        return nuevoNumero;
    },
    /**
     * Función que realiza la suma total
     */
    sumaTotal: function() {
        valorBilletes = 0.00;
        valorMonedas = 0.00;
        for (posIni = 1; posIni < 8; posIni++) {
            valorCampoBillete = Dispensadora
                    .elimarComas($("tb" + posIni).value) * 1;
            if (valorCampoBillete > 0) {
                valorBilletes += valorCampoBillete;
            }
        }
        valorTotal = valorBilletes * 1;
        valorBilletes = Dispensadora.formatoNumero(valorBilletes);
        $("stb").value = valorBilletes;
        for (posIni = 1; posIni < 7; posIni++) {
            valorCampoMoneda = Dispensadora.elimarComas($("tm" + posIni).value) * 1;
            if (valorCampoMoneda > 0) {
                valorMonedas += valorCampoMoneda;
            }
        }
        valorTotal += valorMonedas * 1;
        valorMonedas = Dispensadora.formatoNumero(valorMonedas);
        $("stm").value = valorMonedas;
        if ($("tusuario").value != '') {
            valorSobrante = valorTotal
                    - (Dispensadora.elimarComas($("tusuario").value) * 1)
            valorSobrante = Dispensadora.formatoNumero(valorSobrante);
            $('sobrante').value = valorSobrante;
        }
        valorTotal = Dispensadora.formatoNumero(valorTotal);
        $("total").value = valorTotal;
    },
    /**
     * Función que se encarga de realizar la multiplicaci�n de la cantidad por
     * la denominaci�n
     */
    calculo: function(elemento, tecla, monto) {
        if (elemento.value != "") {
            subtotal = ((elemento.value + tecla) * 1) * monto;
        } else {
            subtotal = (tecla * 1) * monto;
        }
        subtotal = Dispensadora.formatoNumero(subtotal);
        return subtotal;
    },
    /**
     * Función que se encarga de dar el formato a cualquier n�mero
     */
    formatoNumero: function(monto) {
        monto = Dispensadora.formatoMillar(monto);
        monto = Dispensadora.formatoDecimal(monto);
        return monto;
    },
    /**
     * Función que se encarga de dar el formato a cifras con miles y millones
     */
    formatoMillar: function(monto) {
        monto = Dispensadora.formatoDecimal(monto);
        nuevoMonto = '' + monto;
        miles = nuevoMonto.indexOf(",");
        if (miles == -1 && nuevoMonto.length > 3) {
            if (nuevoMonto.split(".").length == 2) {
                separador = nuevoMonto.split(".");
                numeromiles = '' + separador[0];
                if (numeromiles.length > 3) {
                    numeroNegativo = false;
                    coma = ",";
                    posicionUno = numeromiles.substring(0,
                            numeromiles.length - 3);
                    if (posicionUno.indexOf("-") == 0) {
                        posicionUno = posicionUno.substring(1,
                                posicionUno.length);
                        if (posicionUno == "-") {
                            return posicionUno
                                    + numeromiles.substring(
                                            numeromiles.length - 3,
                                            numeromiles.length);
                        } else if (posicionUno == "") {
                            return "-"
                                    + numeromiles.substring(
                                            numeromiles.length - 3,
                                            numeromiles.length);
                        }
                        numeroNegativo = true;
                    }
                    posicionDos = ","
                            + numeromiles.substring(numeromiles.length - 3,
                                    numeromiles.length);
                    posicionTres = "";
                    posicionCuatro = "";
                    if (posicionUno.length > 3) {
                        posicionTres = posicionUno.substring(0,
                                posicionUno.length - 3);
                        posicionCuatro = "'"
                                + posicionUno.substring(posicionUno.length - 3,
                                        posicionUno.length);
                    }
                    if (posicionTres == "" && posicionCuatro == "") {
                        if (numeroNegativo) {
                            nuevoMonto = "-" + posicionUno + posicionDos;
                        } else {
                            nuevoMonto = posicionUno + posicionDos;
                        }
                    } else if (posicionTres != "" && posicionCuatro != "") {
                        if (numeroNegativo) {
                            nuevoMonto = "-" + posicionTres + posicionCuatro
                                    + posicionDos;
                        } else {
                            nuevoMonto = posicionTres + posicionCuatro
                                    + posicionDos;
                        }
                    }
                }
            }
        }
        return nuevoMonto;
    },
    /**
     * Función que se encarga de dar el formato a cifras decimales
     */
    formatoDecimal: function(monto) {
        numeroDecimales = 2;
        nuevoMonto = '' + monto;
        decimales = nuevoMonto.indexOf(".");
        if (decimales == -1) {
            punto = ".";
            for (i = 0; i < numeroDecimales; i++) {
                punto += 0;
            }
            nuevoMonto += punto;
        } else {
            separador = nuevoMonto.split(".");
            if (separador.length == 2) {
                numeromiles = '' + separador[0];
                numeroDecimal = '' + separador[1];
                if (numeroDecimal.length > numeroDecimales) {
                    numeroDecimal = numeroDecimal.substring(0, numeroDecimales);
                } else if (numeroDecimal.length < numeroDecimales) {
                    for (i = numeroDecimal.length; i < numeroDecimales; i++) {
                        numeroDecimal += "0";
                    }
                }
                nuevoMonto = numeromiles + "." + numeroDecimal
            }
        }
        return nuevoMonto;
    },
    /**
     * Función que se ejecuta al dar enter
     */
    aceptar: function() {
        if (opener && opener.Dispensadora.element) {
            opener.Dispensadora.element.value = $("total").value;
        }
        window.close();
    },
    /**
     * Función que despliega la dispensadora de efectivo
     */
    mostrar: function(elemento) {
        if (elemento != null) {
            Dispensadora.element = elemento;
        }
        if (Prototype.Browser.IE) {
            alto = 456;
            largo = 618;
        } else {
            alto = 431;// 426
    largo = 616;// 616
}
posX = (screen.width / 2) - (largo / 2);// para centrar a lo largo de la
                                        // pantalla
posY = (screen.height / 2) - (alto / 2);// para centrar a lo ancho de la
                                        // pantalla
window.open("dispensadora.html", "Dispensadora", "width=" + largo
        + "px,height=" + alto
        + "px,help=no,status=no,scrollbars=no,resizable=no,top=" + posY
        + ",left=" + posX + ",dependent=yes,alwaysRaised=yes", true);
}
};