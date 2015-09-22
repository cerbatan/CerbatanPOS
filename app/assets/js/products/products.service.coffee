define(['./module']
  (module) ->
    'use strict'
    (->
      ProductsService = (playRoutes, $cacheFactory) ->
        return {
          getBrands: (filter) ->
            playRoutes.controllers.products.Products.brands(filter).get()

          addBrand: (brandName) ->
            playRoutes.controllers.products.Products.addBrand().post(name: brandName)

          getTags: (useCache) ->
            playRoutes.controllers.products.Products.tags().get(cache: useCache)

          addTag: (tagName) ->
            playRoutes.controllers.products.Products.addTag().post(name: tagName)

          flushTagsCache: ->
            $cacheFactory.get('$http').remove playRoutes.controllers.products.Products.tags().url

          getTaxes: ->
            playRoutes.controllers.products.Products.taxes().get()

          addTax: (taxName) ->
            playRoutes.controllers.products.Products.addTax().post(taxName)

          addProduct: (product) ->
            playRoutes.controllers.products.Products.addProduct().post(product)

          getProduct: (productId) ->
            playRoutes.controllers.products.Products.getProduct(productId).get()

          getProductsBrief: (filter) ->
            playRoutes.controllers.products.Products.getProductsBrief(filter).get()
        }

      module.factory('productsService', ['playRoutes', '$cacheFactory', ProductsService])
    )()

    return
)