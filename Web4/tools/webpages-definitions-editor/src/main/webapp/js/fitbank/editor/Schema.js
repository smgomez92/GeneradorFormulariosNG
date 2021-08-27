include("lib.prototype");
include("lib.onload");

var Schema = $H();

Schema._get = Schema.get;

Object.extend(Schema, {
    init: function() {
        new Ajax.Request("proc/tables", {
            asynchronous: false,
            onComplete: function(transport) {
                transport.responseJSON.tables.each(function(tableName) {
                    Schema.set(tableName, "");
                });
            }
        });
    },
    
    get: function(tableName) {
        if (!Schema._get(tableName)) {
            new Ajax.Request("proc/table", {
                asynchronous: false,
                parameters: {
                    table: tableName
                },
                onComplete: function(transport) {
                    this.set(tableName, transport.responseJSON);
                }.bind(this)
            });
        }

        return this._get(tableName) || { fields: $H() };
    }
});

addOnLoad(Schema.init);
