include("lib.prototype");

/**
 * Namespace Reporte - Define funciones para manejar los reportes.
 */
var Reporte = {

    /**
     * Funcion que genera un reporte.
     *
     * @parameter (String) name Nombre de reporte a ser bajado, la extension denota el tipo de reporte
     */
    mostrar: function(name, directDownload, downloadName) {
        var randomId = 'reporte' + Math.floor(Math.random() * 10001);
        window.open('about:blank', randomId, '');

        var fileName;
        var fileExtension;
        if (name.indexOf(".") < 0) {
            fileName = name;
            fileExtension = "";
        } else {
            fileName = name.split(".")[0];
            fileExtension = name.split(".")[1];
        }

        var rParams = $H({
            name: fileName,
            extension: fileExtension,
            directDownload: directDownload,
            downloadName: downloadName
        });

        Enlace.submit(c, {
            target: randomId,
            action: "proc/rep/",
            params: rParams,
            skipFiles: true
        });
    }

};
