var BSharpTree = Class.create( {
    root: null,

    initialize: function(comparator) {
        this.root = $A();
        this.comparator = comparator || function(x) {
            return x
        };
    },

    add: function(text, object, node) {
        var node = node || this.root;

        node.objects = node.objects || $A();
        if (node != this.root) {
            node.objects.push(object);
        }

        if (text.length > 0) {
            var chr = text.charCodeAt(0) - 32;
            var next = node[chr] = chr == 0 ? this.root : node[chr] || $A();
            this.add(text.substring(1), object, next);
        }
    },

    search: function(text, objects) {
        var node = this.root;
        var result = null;

        while (text.length > 0) {
            var chr = text.charCodeAt(0) - 32;
            text = text.substring(1);

            if (chr == 0) {
                result = this.search(text, result);
                break;
            } else {
                node = node[chr];
                if (!node) {
                    return $A();
                }
                result = node.objects;
            }
        }

        if (objects) {
            if (node == this.root) {
                return objects;
            } else {
                return this.intersect(result, objects);
            }
        } else {
            if (node == this.root) {
                return $A();
            } else {
                return result;
            }
        }
    },

    intersect: function(a, b) {
        var i = 0;
        var j = 0
        var intersect = $A();

        a = a.sortBy(this.comparator);
        b = b.sortBy(this.comparator);

        while (i < a.length && j < b.length) {
            if (this.comparator(a[i]) == this.comparator(b[j])) {
                intersect.push(a[i]);
                i++;
                j++;
            } else if (this.comparator(a[i]) > this.comparator(b[j])) {
                j++;
            } else {
                i++;
            }
        }

        return intersect;
    }
});
