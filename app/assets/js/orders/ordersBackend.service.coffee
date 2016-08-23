define(['./module']
  (module) ->
    'use strict'
    (->
      OrdersBackend = (playRoutes, $cacheFactory) ->
        return {
          searchProduct: (filter) ->
            playRoutes.controllers.sell.PointOfSale.getListedProducts(filter).get()

          getProduct: (productId) ->
            playRoutes.controllers.products.Products.getProduct(productId).get()

          createOrder: (supplierName) ->
            playRoutes.controllers.orders.Orders.add().post(supplier: supplierName)

          getOrder: (orderId) ->
            playRoutes.controllers.orders.Orders.getOrder(orderId).get()

        }

      module.factory('ordersBackend', ['playRoutes', '$cacheFactory', OrdersBackend])
    )()

    return
)