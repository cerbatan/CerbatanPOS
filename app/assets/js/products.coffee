'use strict'

require(['jquery', 'angular']
   ($, angular) ->
      class Brand
         constructor: (@name) ->
            @id = null


      class Brand
         constructor: (@name) ->
            @id = null


      class Tag
         constructor: (@name, @percentage) ->
            @id = null


      class Fraction
         constructor: (@name, @item, @qty, @price, @sku) ->
            @id = null


      class Product
         constructor: ->
            @id = null
            @sku = null
            @name = null
            @brand = null

            ###*
            # @type {Tag[]}
            ###
            @tags = []

            @cost = 0
            @markup = 0
            @price = 0
            @tax = null
            @retailPrice = 0
            @trackStock = true
            @stockCount = 0
            @alertStockLowLevel = false
            @stockAlertLevel = 0

            ###*
            # @type {Fraction[]}
            ###
            @fractions = []

      angular
      .module('app.products', [
            'ngSanitize'
            'ngTagsInput'
            'ui.select'
            'play.routing'
            'app.common'
         ])


      angular
      .module('app.products')
      .controller('ProductsCtrl', [$location, ProductsController])

      angular
      .module('app.products')
      .controller('NewProductCtrl', [
            '$scope'
            '$filter'
            '$cacheFactory'
            '$log'
            '$modal'
            'playRoutes'
            '$location'
            'localize'
            NewProductController
         ])


      ProductsController = ($location) ->
         init = () ->
            @goToAddProduct = goToAddProduct
            return

         goToAddProduct = () =>
            $location.path '/product/new'
            return

         init()
         return


      NewProductController = ($filter, $cacheFactory, $log, $modal, routes, $location, localize) ->

         init = () ->
            @product = new Product()
            lastSearch = " ";
            @brands = []
            @tags = []
            @saving = false
            getTaxes()
            return

         showAddBrandDialog = (brandName, showInput) ->
            $modal.open
               templateUrl: "addBrandModal.html"
               controller: 'NewBrandModalCtrl'
               resolve:
                  newBrandName: () -> brandName
               showInput: () -> showInput is true

         showAddTaxDialog = () ->
            $modal.open
               templateUrl: "addTaxModal.html"
               controller: 'AddTaxModalCtrl'

         refreshBrands = (filter) ->
            if (lastSearch.length == 0 and filter.length > 0) or filter.toLowerCase().indexOf(lastSearch.toLocaleLowerCase()) != 0
               lastSearch = filter
               routes.controllers.products.Products.brands(filter).get().success (brands) ->
                  if brands.length == 0
                     brands.push new Brand(filter)
                  $scope.brands = brands
                  return
            else if @$select.items.length == 0 or @$select.items[0].id == null
               if @$select.items.length > 1 and @$select.items[0].id == null
                  $scope.brands.shift()
               else if $scope.brands.length > 0 and $scope.brands[0].id == null
                  $scope.brands[0].name = filter
               else
                  $scope.brands.unshift new Brand(filter)
            return


         return
)