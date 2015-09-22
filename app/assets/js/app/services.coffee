define ['./module'],
  (app) ->
    (->
      Notifier = (toastr) ->
        logIt = (message, type) ->
          toastr[type](message)

        return {
        log: (message) ->
          logIt(message, 'info')
          return

        logWarning: (message) ->
          logIt(message, 'warning')
          return

        logSuccess: (message) ->
          logIt(message, 'success')
          return

        logError: (message) ->
          logIt(message, 'error')
          return
        }

      app.factory('notifier', ['toastr', Notifier])
    )()