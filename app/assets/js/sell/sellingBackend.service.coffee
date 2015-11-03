define(['./module']
  (module) ->
    'use strict'
    (->
      SellingBackend = (playRoutes, $cacheFactory) ->
        return {
          listProducts: (filter) ->
            playRoutes.controllers.sell.PointOfSale.getListedProducts(filter).get()

          getProduct: (productId) ->
            playRoutes.controllers.products.Products.getProduct(productId).get()

#          flushTagsCache: ->
#            $cacheFactory.get('$http').remove playRoutes.controllers.products.Products.tags().url
        }

      module.factory('sellingBackend', ['playRoutes', '$cacheFactory', SellingBackend])
    )()

    return
)