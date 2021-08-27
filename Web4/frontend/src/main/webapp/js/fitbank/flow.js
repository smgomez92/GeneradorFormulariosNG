include("lib.prototype");
include("lib.onload");

/**
 * Namespace Flow - Contiene las funciones para la página de flujos.
 */
var Flow = {

    show: function() {
        setTimeout(function() {
            Flow.showDiagram(data[0], 100);
        }, 100);
        setTimeout(Flow.showData, 500);
    },

    showDiagram: function(data, level) {
        var element = $('data');
        var processId = data.processId;
        var activities = data.activities;

        element.update(new Element("img", {
            id: "_iFlow" + level
        }));

        if (level == 100) {
            self['parent' + level] = data;
            self['level'+level] = level;
        }

        if (data.image) {
            $("_iFlow" + level).src = "data:image/png;base64," + data.image;
        } else {
            $("_iFlow" + level).src = "img/proc/img_disco/flow/" + processId + ".png"
        }

        self.parentProcess = data;

        var levelActual = level + 100;
        for (var i = 0; i < activities.length; i++) {
            levelActual += i;
            self['parent' + levelActual] = activities[i];
            self['level' + levelActual] = level;
            topy = activities[i].y;

            if (level > 100) {
                element.innerHTML += '<div class="back" onclick="Flow.showLevel('+level+', true)"></div>';
            }

            if(activities[i].processId){
                element.innerHTML += '<div class="show-level" onclick="Flow.showLevel('+levelActual+')" style="left:'+(activities[i].x)+'px;top:'+topy+'px;width:'+(activities[i].w - 2)+'px;height:'+(activities[i].h - 2)+'px;"></div>';
            }else{
                element.innerHTML += '<div class="level-actual" style="left:'+(activities[i].x)+'px;top:'+topy+'px;width:'+(activities[i].w - 2)+'px;height:'+(activities[i].h - 2)+'px;"></div>';
            }
        }
        top.properties = data.properties[0];
        top.pid = processId;
        self.clheight = element.offsetHeight+210;
        top.userinfo = data.userinfo[0];
    },

    showLevel: function(level, back1) {
        if (back1) {
            level = self['level' + level];
        }
        var data = self['parent' + level];
        Flow.showDiagram(data, level);
    },

    openDialog: function() {
        if (self.w) {
            self.w.close();
        }
        w = window.open("frames.html", "flowvars", "width=1024,height=768,location=0,status=0,scrollbars=1,resizable=0");
        w.properties = top.properties;
        w.pid = top.pid;

    },

    showData: function() {
        for (k in top.userinfo) {
            v = top.userinfo[k][0];
            Flow.showTable(v, k == "ar0" ? "varF1" : "varF");
        }
    },

    showTable: function(arr, divid) {
        $(divid).insert(Flow.showArray(arr));
    },

    prepareCell: function(value, type, colspan) {
        var td = new Element(type || "td").update(value.replace(/_/g, ' '));

        if (colspan) {
            td.colSpan = colspan;
            td.align = "center";
        }

        return td;
    },

    prepareImg: function(img, rowspan) {
        td = new Element("td", {
            align: "center",
            className: "img"
        });

        if (rowspan) {
            td.rowSpan = rowspan;
        }

        if (img) {
            td.insert(new Element("img", {
                src: img,
                border: 0
            }));
        }

        return td;
    },

    showArray: function(arr) {
        var div = new Element("div");

        var tab = new Element("table", {
            className: 'tab'
        });
        div.insert(tab);

        var tbody=new Element("tbody");
        tab.insert(tbody);

        for (k in arr) {
            var tr = new Element("tr", {
                className: "ti"
            });
            tbody.insert(tr);

            tr.insert(Flow.prepareCell(k, null, 3));

            var obj = arr[k];
            c = 0;
            for (i in obj) {
                if(obj[i] == ''){
                    continue;
                }

                c++;

                var trel = new Element("tr", {
                    className: c % 2 ? 'alt2' : 'alt'
                });

                if(c == 1 && k.indexOf('Transaccion_Actual')==0){
                    tdimg = Flow.prepareImg(Flow.getIconToShow(obj.Respuesta));
                    tdimg.rowSpan = '100';
                    trel.insert(tdimg);
                }

                trel.insert(Flow.prepareCell(i));
                trel.insert(Flow.prepareCell(obj[i]));

                if(c == 1 && k.indexOf('Transaccion_Realizada')==0){
                    tdimg = Flow.prepareImg(Flow.getIconToShow(obj.Respuesta));
                    tdimg.rowSpan = '100';
                    trel.insert(tdimg);
                }

                tbody.insert(trel);
            }
        }

        return div;
    },

    getIconToShow: function(res) {
        if (res == 'OK') {
            return 'img/bpm/ok.png';
        } else if (res == 'NO') {
            return 'img/bpm/deny.png';
        } else {
            return 'img/bpm/wait.png';
        }
    }

}

addOnLoad(Flow.show);
