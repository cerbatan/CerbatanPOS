define(['./module']
  (module) ->
    'use strict'
    (->
      OrdersRoutes = ($location) ->
        return {
          receiveOder: () ->
            $location.path("/stock-control/order/editOrder").search({})

          editOrder: (orderId) ->
            $location.path("/order/#{orderId}").search({})
        }

      module.factory('ordersRoutes', ['$location', OrdersRoutes])
    )()

    return
)