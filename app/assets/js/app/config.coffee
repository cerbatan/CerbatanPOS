define(['./module', 'angular']
  (app, ng) ->
    'use strict'
    config = ($routeProvider, toastrConfig) ->
      $routeProvider
        .when '/',
          redirectTo: '/sell'

        .when '/sell',
          templateUrl: 'views/sell'
          controller: "SellCtrl"
          controllerAs: "ctrl"

        .when '/products',
          templateUrl: 'views/products'
          controller: 'ProductsCtrl'
          controllerAs: 'ctrl'

        .when '/product/new',
          templateUrl: 'views/product/new'
          controller: 'EditProductCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedTaxes: [
              'productsBackend'
              (productsService) ->
                productsService.getTaxes()
            ]
            preparedProduct: -> null

        .when '/product/:id',
          templateUrl: 'views/product/detail'
          controller: 'DetailProductCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedProduct: [
              '$route'
              'productsBackend'
              ($route, productsService) ->
                productsService.getProduct($route.current.params.id)
            ]

        .when '/product/edit/:id',
          templateUrl: 'views/product/edit'
          controller: 'EditProductCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedTaxes: [
              'productsBackend'
              (productsService) ->
                productsService.getTaxes()
            ]
            preparedProduct: [
              '$route'
              'productsBackend'
              ($route, productsService) ->
                productsService.getProduct($route.current.params.id)
            ]

        .when '/order/:id',
          templateUrl: 'views/order/edit'
          controller: 'EditOrderCtrl'
          controllerAs: 'ctrl'
          resolve:
            preparedOrder: [
              '$route'
              'ordersBackend'
              ($route, ordersBackend) ->
                ordersBackend.getOrder($route.current.params.id)
            ]

        .when '/orders',
          templateUrl: 'views/orders'
          controller: 'OrdersCtrl'
          controllerAs: 'ctrl'

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