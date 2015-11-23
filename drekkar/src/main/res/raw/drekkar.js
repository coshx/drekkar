var Drekkar;

Drekkar = (function() {
  Drekkar["default"] = null;

  Drekkar.buses = [];

  Drekkar.hashCode = DrekkarWebViewJSEndpoint.getHashCode();

  function Drekkar(name) {
    this.name = name;
    this.subscribers = [];
  }

  Drekkar.prototype._post = function(eventName, data) {
    return setTimeout(((function(_this) {
      return function() {
        var dataToSend;
        dataToSend = null;
        if (data != null) {
          if (data instanceof Array || data instanceof Object) {
            dataToSend = JSON.stringify(data);
          } else {
            dataToSend = data;
          }
        }
        return DrekkarWebViewJSEndpoint.send(Drekkar.hashCode, _this.name, eventName, dataToSend);
      };
    })(this)), 0);
  };

  Drekkar.prototype.getName = function() {
    return this.name;
  };

  Drekkar.prototype.post = function(name, data) {
    return this._post(name, data);
  };

  Drekkar.prototype.register = function(name, callback) {
    return this.subscribers.push({
      name: name,
      callback: callback
    });
  };

  Drekkar.prototype.raise = function(name, data) {
    var e, i, len, parsedData, ref, results;
    if (data instanceof Array || data instanceof Object || (typeof data === "string" || data instanceof String)) {
      parsedData = data;
    } else {
      parsedData = JSON.parse(data);
    }
    ref = this.subscribers;
    results = [];
    for (i = 0, len = ref.length; i < len; i++) {
      e = ref[i];
      if (e.name === name) {
        results.push(e.callback(name, parsedData));
      } else {
        results.push(void 0);
      }
    }
    return results;
  };

  Drekkar.getDefault = function() {
    if (Drekkar["default"] == null) {
      Drekkar["default"] = new Drekkar("default");
      Drekkar["default"].post("DrekkarInit");
    }
    return Drekkar["default"];
  };

  Drekkar.get = function(name) {
    var b, i, len, ref;
    ref = Drekkar.buses;
    for (i = 0, len = ref.length; i < len; i++) {
      b = ref[i];
      if (b.getName() === name) {
        return b;
      }
    }
    b = new Drekkar(name);
    Drekkar.buses.push(b);
    b.post("DrekkarInit");
    return b;
  };

  return Drekkar;

})();

//# sourceMappingURL=drekkar.js.map
