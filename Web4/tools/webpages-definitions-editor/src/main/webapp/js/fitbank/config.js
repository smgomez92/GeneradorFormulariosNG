includeCSS("editor");
include("fitbank.entorno");
include("fitbank.editor.editor");

window.onbeforeunload = Entorno.caducar;

document.disableContextMenu();