define(['./module']
  (module) ->
    'use strict'
    (->
      StockRoutes = ($location) ->
        return {
          receiveOder: () ->
            $location.path("/stock-control/order/receive").search({})
        }

      module.factory('stockRoutes', ['$location', StockRoutes])
    )()

    return
)