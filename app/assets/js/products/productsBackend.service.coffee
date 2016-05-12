define(['./module']
  (module) ->
    'use strict'
    (->
      ProductsBackend = (playRoutes, $cacheFactory) ->
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
            playRoutes.controllers.products.Products.saveProduct().post(product)

          updateProduct: (product) ->
            playRoutes.controllers.products.Products.updateProduct().put(product)

          getProduct: (productId) ->
            playRoutes.controllers.products.Products.getProduct(productId).get()

          getProductsBrief: (filter, page) ->
            playRoutes.controllers.products.Products.getProductsBrief(filter, page).get()
        }

      module.factory('productsBackend', ['playRoutes', '$cacheFactory', ProductsBackend])
    )()

    return
)