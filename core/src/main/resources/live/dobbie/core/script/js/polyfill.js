const print = function () {
    java.lang.System.out.println(Array.prototype.slice.call(arguments).join(' '));
};

const console = {
    log: function () {
        print.apply(undefined, arguments);
    },
    debug: function () {
        this.log.apply(undefined, arguments);
    }
};

/**
 * Parts of Polyfill project
 * @version 0.1.42
 * @see https://github.com/inexorabletash/polyfill
 * @license MIT
 * @license Unlicense
 */
(function () {
    // ES5 15.3.4.5 Function.prototype.bind ( thisArg [, arg1 [, arg2, ... ]] )
    // https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Function/bind
    if (!Function.prototype.bind) {
        Function.prototype.bind = function (o) {
            if (typeof this !== 'function') {
                throw TypeError("Bind must be called on a function");
            }

            var args = Array.prototype.slice.call(arguments, 1),
                self = this,
                nop = function () {
                },
                bound = function () {
                    return self.apply(this instanceof nop ? this : o,
                        args.concat(Array.prototype.slice.call(arguments)));
                };

            if (this.prototype)
                nop.prototype = this.prototype;
            bound.prototype = new nop();
            return bound;
        };
    }
})();