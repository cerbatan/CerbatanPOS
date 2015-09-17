'use strict'


require(
  [
    'jquery'
    'angular'
    'angular-sanitize'
    'angular-route'
    'angular-animate'
    'angular-scroll'
    'ui-bootstrap-tpls'
    'angular-tags-input'
    'angular-ui-select'
    'angular-toastr'
    'app/routes'
    'app/common'
    'main'
    'directives'
    'localize'
    'nav'
    'products/products.module'
    'products/products.controller'
    'products/products.service'
    'task/task'
  ]
  ($, angular) ->
    angular.module('app', [
      'ngRoute'
      'ui.bootstrap'
      'duScroll'
      'app.common'
      'app.controllers'
      'app.directives'
      'app.localization'
      'app.nav'
      'app.task'
      'app.products'
    ])

    config = ($routeProvider, productsService) ->
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
          preparedTaxes: (productsService) ->
            productsService.getTaxes()

      .when '/product/:id',
        templateUrl: 'views/product/detail'
        controller: 'DetailProductCtrl'
        controllerAs: 'ctrl'

      .when '/404',
        templateUrl: 'views/pages/404.html'

      return

    angular.module('app')
    .config ['$routeProvider', 'productsService', config]

    $(document).ready ->
      angular.bootstrap document.body, ['app']
      return

    return
)