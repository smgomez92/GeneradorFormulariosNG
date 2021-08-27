include("fitbank.entorno");

window.onbeforeunload = Entorno.caducar;

document.disableContextMenu();

if (Parametros['fitbank.config.DOMAIN']) {
    document.domain = Parametros['fitbank.config.DOMAIN'];
}
