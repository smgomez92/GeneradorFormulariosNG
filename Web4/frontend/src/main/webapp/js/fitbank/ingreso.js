include("lib.prototype");
include("lib.onload");

include("fitbank.proc.parametros");
include("fitbank.util");

/**
 * Namespace Ingreso - Contiene las funciones para la página de ingreso.
 */
var Ingreso = {
    /**
     * Inicializa el formulario de ingeso.
     * 
     * @private
     */
    init: function () {
        if (Parametros['fitbank.ingreso.WEBKIT'] == "true" && !Prototype.Browser.WebKit) {
            window.location.href = "navegador.html";
            return;
        }

        Ingreso.webencrypt = Parametros['fitbank.webencrypt'] || false;
        Ingreso.phrase = Math.random() * 1000000;
        if (Ingreso.webencrypt) {
            Ingreso.phrase = CryptoJS.enc.Base64.parse(Parametros['fitbank.phrase']).toString(CryptoJS.enc.Utf8);
        }

        document.disableContextMenu();

        Ingreso.formulario = $("ingreso");

        if (!Ingreso.formulario) {
            return;
        }

        Ingreso.activa = $("activa");

        Ingreso.usuario = Ingreso.formulario.select("input[type=text]")[0];
        Ingreso.clave = Ingreso.formulario.select("input[type=password]")[0];
        Ingreso.cierre = $("cierre");

        // Obtener datos de ingreso de formulario de la p�gina padre
        if (window.opener && window.opener.Ingreso) {
            Ingreso.datos = window.opener.Ingreso.datos;
            window.opener.Ingreso.datos = null;
        }

        Ingreso.formulario.action = "proc/sig";
        Ingreso.formulario.method = "POST";
        Ingreso.formulario.onsubmit = Ingreso.ingresar.curry(false, false);

        Ingreso.progreso = new Element("img", {
            className: "ingreso-progreso",
            src: "img/progreso.gif"
        });
        Ingreso.progreso.hide();
        Ingreso.formulario.insert({
            after: Ingreso.progreso
        });

        Ingreso.formulario.insert(new Element("input", {
            type: "hidden",
            name: "_contexto",
            value: "sig"
        }));

        var ipElement = new Element("input", {
            type: "hidden",
            name: "_localip"
        });

        Util.getLocalIPsByRTC(function (ip) {
            if (ip && "127.0.0.1" !== ip) {
                ipElement.changeValue(ip);
                Ingreso.formulario.insert(ipElement);
            }
        }, ipElement);

        if ($('mas_opciones')) {
            $('mas_opciones').on("click", Element.toggle.curry($('opciones')));
        }

        // Forzar cierre
        $('forzar').on("click", function () {
            new Ajax.Request("proc/cad", {
                onComplete: function () {
                    location.reload();
                }
            });
        });

        Ingreso.initNames(true);
    },
    /**
     * Función que inicializa los names de los elementos.
     */
    initNames: function (init) {
        var json = {
            error: true
        };

        new Ajax.Request("proc/names", {
            asynchronous: false,
            parameters: {
                _contexto: "sig",
                rand: Math.random(),
                init: (init && !Ingreso.datos) ? "true" : ""
            },
            onSuccess: function (transport) {
                json = transport.responseJSON;
            }
        });

        if (json.error) {
            document.location.href = "error.html#" + json.id;
            Ingreso.formulario.hide();
            return false;
        }

        if (json.activa) {
            Ingreso.activa.show();
            Ingreso.formulario.hide();
            return false;
        }

        Ingreso.usuario.name = json.nameUsuario || Math.random();
        Ingreso.usuario.previous("label").setAttribute("for", Ingreso.usuario.name);

        Ingreso.clave.name = json.nameClave || Math.random();
        Ingreso.clave.previous("label").setAttribute("for", Ingreso.clave.name);

        if (Ingreso.datos) {
            Ingreso.usuario.value = Ingreso.datos.usuario.trim();
            Ingreso.clave.value = Ingreso.datos.clave.trim();
            Ingreso.ingresar(true, true);
        } else {
            Ingreso.formulario.show();
            Ingreso.formulario.focusFirstElement();
        }

        return true;
    },
    /**
     * Encriptar un texto, usando una frase secreta
     * 
     * @param String message Mensaje a encriptar
     * @param String pass Frase Secreta
     * @returns String Texto encriptado
     */
    encrypt: function (message, pass) {
        var iv = CryptoJS.lib.WordArray.random(128 / 8).toString(CryptoJS.enc.Hex);
        var salt = CryptoJS.lib.WordArray.random(128 / 8).toString(CryptoJS.enc.Hex);
        var iterations = 1000;
        var key = CryptoJS.PBKDF2(pass, CryptoJS.enc.Hex.parse(salt), {
            keySize: 128 / 32,
            iterations: iterations
        });

        var encrypted = CryptoJS.AES.encrypt(message, key, {
            iv: CryptoJS.enc.Hex.parse(iv)
        });
        return salt.toString() + iv.toString() + encrypted.ciphertext.toString(CryptoJS.enc.Base64);
    },
    /**
     * Desencriptar un texto, usando una frase secreta
     * 
     * @param String message Mensaje Encriptado
     * @param String pass Frase Secreta
     * @returns String Password desencriptado
     */
    decrypt: function (message, pass) {
        var salt = CryptoJS.enc.Hex.parse(message.substr(0, 32));
        var iv = CryptoJS.enc.Hex.parse(message.substr(32, 32))
        var encrypted = message.substring(64);
        var keySize = 256;
        var iterations = 100;


        var key = CryptoJS.PBKDF2(pass, salt, {
            keySize: keySize / 32,
            iterations: iterations
        });

        var decrypted = CryptoJS.AES.decrypt(encrypted, key, {
            iv: iv,
            padding: CryptoJS.pad.Pkcs7,
            mode: CryptoJS.mode.CBC

        });

        return decrypted.toString(CryptoJS.enc.Utf8);
    },
    /**
     * Abre una nueva ventana.
     *
     * @param usuario Usuario (opcional)
     * @param clave Clave (opcional)
     * 
     * @private
     */
    abrir: function (usuario, clave) {
        if (screen.width < 1024 || screen.height < 768) {
            if (!confirm("Su pantalla no cumple con los requerimientos mínimos"
                    + " de resolución de 1024x768."
                    + " ¿Desea continuar abriendo la aplicación con esta resolución?")) {
                return false;
            }
        }

        if (usuario && clave) {
            Ingreso.datos = {
                usuario: usuario,
                clave: clave
            };
        }

        var nombre = ("ventana_" + Math.random() * 10000000000).substring(0, 15);

        var url = modularjs.basePath + "../ingreso.html";
        if (Prototype.Browser.IE) {
            var width = screen.availWidth;
            var height = screen.availHeight - 10;

            window.open(url, nombre, "width=" + width + "px,height=" + height
                    + "px,left=0,top=0,resizable=yes");
        } else {
            window.open(url, nombre, "fullscreen=yes");
        }

        return nombre;
    },
    /**
     * Ejecuta el proceso de ingreso.
     * 
     * @private
     */
    ingresar: function (submit, noInit) {
        if (!noInit && !Ingreso.initNames(false)) {
            return false;
        }

        //Ignorar espacios en blanco antes y después del nombre enviado
        Ingreso.usuario.value = Ingreso.usuario.value.trim();

        //En caso de requerirlo, encriptar el password desde la peticion
        if (Ingreso.webencrypt && Ingreso.clave.value != "") {
            Ingreso.clave.value = Ingreso.encrypt(Ingreso.clave.value, Ingreso.phrase);
        }

        (function () {
            Ingreso.formulario.hide();
            Ingreso.progreso.show();
            if (submit) {
                Ingreso.formulario.submit();
            }
            Ingreso.usuario.value = "";
            Ingreso.clave.value = "";
            Ingreso.cierre.checked = false;
        }).defer();

        return true;
    },
    /**
     * Sale de la pagina actual y va a una pagina con un mensaje de nueva ventana.
     * 
     * @private
     */
    cerrar: function () {
        document.location.href = "postingreso.html";
    }
};

addOnLoad(Ingreso.init);
