define ['angular', './module'],
  (ng, app) ->
    (->
      ProductsContaionerDirective  = ->
        restrict: 'C'
        scope:
          products: '=products'

        templateUrl: 'views/templates/products-container.html'
        link: (scope, element, attrs) ->


      app.directive 'productsContainer', ProductsContaionerDirective

    )()