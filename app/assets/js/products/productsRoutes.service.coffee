define(['./module']
  (module) ->
    'use strict'
    (->
      ProductsRoutes = ($location) ->
        return {
          showProduct: (productId) ->
            $location.path("/product/#{productId}").search({})

          editProduct: (productId) ->
            $location.path("/product/edit/#{productId}").search({})

          addProduct: () ->
            $location.path("/product/new").search({})

          showBriefs: (filter, page) ->
            $location.path "/products"
            $location.search("q", filter) if filter?
            $location.search("p", page) if page?
            $location.replace()
        }

      module.factory('productsRoutes', ['$location', ProductsRoutes])
    )()

    return
)