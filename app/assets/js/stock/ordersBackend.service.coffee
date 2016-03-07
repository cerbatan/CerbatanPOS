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
        }

      module.factory('ordersBackend', ['playRoutes', '$cacheFactory', OrdersBackend])
    )()

    return
)