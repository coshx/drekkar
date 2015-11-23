# @class Drekkar
# @brief Drekkar JS bus
class Drekkar
  @default = null
  @buses = []
  @hashCode = DrekkarWebViewJSEndpoint.getHashCode()

  constructor: (name)  ->
    @name = name
    @subscribers = []

  # Internal method for posting
  _post: (eventName, data) ->
    # shouldLoadRequest is only triggered when a new content is required
    # Ajax requests are useless
    setTimeout (() =>
      dataToSend = null
      if data?
        if data instanceof Array or data instanceof Object
          dataToSend = JSON.stringify(data)
        else
          dataToSend = data
      DrekkarWebViewJSEndpoint.send Drekkar.hashCode, @name, eventName, dataToSend
    ), 0

  getName: () ->
    @name

  post: (name, data) ->
    @_post name, data

  register: (name, callback) ->
    @subscribers.push { name: name, callback: callback }

  # Internal method only. Called by Android part for triggering events on the bus
  raise: (name, data) ->
    if data instanceof Array or data instanceof Object or (typeof data == "string" or data instanceof String)
      # Data are already parsed, nothing to do
      parsedData = data
    else
      parsedData = JSON.parse data
    for e in @subscribers
      e.callback(name, parsedData) if e.name == name

  @getDefault: ->
    unless Drekkar.default?
      Drekkar.default = new Drekkar("default")
      Drekkar.default.post "DrekkarInit"
    Drekkar.default

  @get: (name) ->
    for b in Drekkar.buses
      if b.getName() == name
        return b

    b = new Drekkar name
    Drekkar.buses.push b
    b.post "DrekkarInit"
    return b