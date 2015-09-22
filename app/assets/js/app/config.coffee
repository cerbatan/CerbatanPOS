define(['./module', 'angular']
  (app, ng) ->
    'use strict'
    config = ($routeProvider, toastrConfig) ->
      $routeProvider
        .when '/',
          redirectTo: '/sell'

        .when '/sell',
          templateUrl: 'sell'

        .when '/products',
          templateUrl: 'views/products'
          controller: 'ProductsCtrl'
          controllerAs: 'ctrl'

        .when '/product/new',
          templateUrl: 'views/product/new'
          controller: 'NewProductCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedTaxes: [
              'productsService'
              (productsService) ->
                productsService.getTaxes()
            ]

        .when '/product/:id',
          templateUrl: 'views/product/detail'
          controller: 'DetailProductCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedProduct: [
              '$route'
              'productsService'
              ($route, productsService) ->
                productsService.getProduct($route.current.params.id)
            ]

        .when '/404',
          templateUrl: 'views/pages/404.html'

      ng.extend toastrConfig,
        closeButton: true
        allowHtml: true
        positionClass: "toast-bottom-left"
        timeOut: "4000"
        target: "div.main-container"

      return

    app.config ['$routeProvider', 'toastrConfig', config]
)