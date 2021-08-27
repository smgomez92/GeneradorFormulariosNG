var toggle = function (what) {
    var current = what.nextElementSibling.className;
    what.nextElementSibling.className = current ? "" : "visible";
}
