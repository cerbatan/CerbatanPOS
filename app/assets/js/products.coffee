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

    class Tax
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

    ProductsController = ($location) ->
      init = () =>
        @goToAddProduct = goToAddProduct

      goToAddProduct = () ->
        $location.path '/product/new'

      init()
      return

    ProductsController
      .$inject = ['$location']


    NewProductController = ($filter, $cacheFactory, $log, $modal, routes, $location, localize) ->
      lastSearch = " "

      init = () =>
        @product = new Product()
        @brands = []
        @tags = []
        @saving = false

        @refreshBrands = refreshBrands
        @brandSelected = brandSelected
        @addBrand = addBrand
        @loadTags = loadTags
        @handleAddingTag = handleAddingTag
        @productCostUpdated = productCostUpdated
        @productMarkupUpdated = productMarkupUpdated
        @productPriceUpdated = productPriceUpdated
        @taxSelected = taxSelected
        @retailPriceUpdated = retailPriceUpdated
        @taxChanged = taxChanged
        @productSkuUpdated = productSkuUpdated
        @addFraction = addFraction
        @removeFraction = removeFraction
        @submitProduct = submitProduct
        @fractionProportionChanged = fractionProportionChanged
        @fractionPriceChanged = fractionPriceChanged

        activate()

      activate = =>
        getTaxes()

      showAddBrandDialog = (brandName, showInput) ->
        $modal.open
          templateUrl: "addBrandModal.html"
          controller: 'NewBrandModalCtrl'
          controllerAs: 'modalCtrl'
          resolve:
            newBrandName: () -> brandName
            showInput: () -> showInput is true

      showAddTaxDialog = () ->
        $modal.open
          templateUrl: "addTaxModal.html"
          controller: 'AddTaxModalCtrl'

      refreshBrands = ($select) =>
        filter = $select.search
        if (lastSearch.length == 0 and filter.length > 0) or filter.toLowerCase().indexOf(lastSearch.toLocaleLowerCase()) != 0
          lastSearch = filter
          routes.controllers.products.Products.brands(filter).get().success (brands) =>
            if brands.length == 0
              brands.push new Brand(filter)
            @brands = brands

        else if $select.items.length == 0 or $select.items[0].id == null
          if $select.items.length > 1 and $select.items[0].id == null
            @brands.shift()
          else if @brands.length > 0 and @brands[0].id == null
            @brands[0].name = filter
          else
            @brands.unshift new Brand(filter)

      brandSelected = ($select) =>
        if $select.search
          if !$select.selected or $select.selected.id == null or $select.selected.name.toLowerCase().indexOf($select.search.toLocaleLowerCase()) == -1
            newBrandName = $select.search
            showAddBrandDialog(newBrandName).result.then =>
              $log.info 'Adding Brand'
              routes.controllers.products.Products.addBrand().put(name: newBrandName).success (brand) =>
                @product.brand = brand
              .error ->
                $log.warning 'Failed brand creation'
            , ->
              $select.selected = null
        $select.search = ''

      addBrand = =>
        showAddBrandDialog('', true).result.then (newBrandName) =>
          $log.info 'Adding Brand'
          routes.controllers.products.Products.addBrand().put(name: newBrandName).success (brand) =>
            @product.brand = brand
          .error ->
            $log.warning 'Failed brand creation'
        , ->

      loadTags = ($query) ->
        routes.controllers.products.Products.tags().get(cache: true).then (response) ->
          $filter('filter') response.data, $query

      handleAddingTag = ($tag) =>
        if $tag.id == undefined or $tag.id == null
          $log.info 'Adding Tag'
          routes.controllers.products.Products.addTag().put(name: $tag.name).success (tag) =>
            @product.tags.pop()
            @product.tags.push tag
            $cacheFactory.get('$http').remove routes.controllers.products.Products.tags().url
          .error =>
            @product.tags.pop()
            $log.warning 'Failed tag creation'


      getTaxes = () =>
        routes.controllers.products.Products.taxes().get().success (taxes) =>
          @taxes = taxes
          @taxes.push(new Tax("--- " + localize.getLocalizedString("Add") + " ---", 0.0))

      updatePrice = =>
        @product.price = @product.cost * (1.0 + @product.markup / 100.0)

      updateRetailPrice = =>
        if @product.tax?
          @product.retailPrice = @product.price * (1.0 + @product.tax.percentage / 100.0)
        else
          @product.retailPrice = @product.price

      productCostUpdated = (isValid)->
        if isValid
          updatePrice()
          updateRetailPrice()

      productMarkupUpdated = ->
        updatePrice()
        updateRetailPrice()

      productPriceUpdated = =>
        @product.markup = (@product.price / @product.cost - 1.0) * 100.0
        updateRetailPrice()

      taxSelected = (select) =>
        if select.selected.id == null
          showAddTaxDialog().result.then (newTax) =>
            $log.info 'Adding Tax'
            routes.controllers.products.Products.addTax().put(newTax)
              .success (tax) =>
                @product.tax = tax
                getTaxes()
              .error ->
                $log.warning 'Failed tax creation'
          , ->
            select.selected = null

      taxChanged = updateRetailPrice

      retailPriceUpdated = =>
        if @product.tax?
          @product.price = @product.retailPrice / (1.0 + @product.tax.percentage / 100.0)
        else
          @product.price = @product.retailPrice

        @product.markup = (@product.price / @product.cost - 1.0) * 100.0
        if @product.fractions.length > 0 then @product.fractions[0].price = @product.retailPrice


      productSkuUpdated = =>
        @product.fractions[0].sku = @product.sku unless @product.fractions.length == 0

      addFraction = =>
        if @product.fractions.length == 0
          @product.fractions.push(new Fraction("Regular", @product.id, 1, @product.retailPrice, @product.sku))

        @product.fractions.push(new Fraction(null, @product.id, null, null, null))

      removeFraction = (index) =>
        if  index == 1 and  @product.fractions.length <= 2
          @product.fractions = []
        else
          @product.fractions.splice(index, 1)

      submitProduct = (isValid) =>
        if isValid
          @saving = true
          routes.controllers.products.Products.addProduct()
          .put($scope.product)
            .success (productId) =>
              $log.info("Product added " + productId);
              $location.path('/product/' + productId);
              @saving = false;
            .error () ->
              $log.warning("Failed product creation");

      fractionProportionChanged = (index) =>
        fraction = @product.fractions[index]
        fraction.price = @product.retailPrice*fraction.qty

      fractionPriceChanged = (index, validatorField) =>
        fraction = @product.fractions[index]
        validatorField.$setValidity("fractionPriceGtRetailPrice", @product.retailPrice*fraction.qty <= fraction.price)

      init()
      return

    NewProductController
      .$inject = ['$filter', '$cacheFactory', '$log', '$modal', 'playRoutes', '$location', 'localize' ]


    NewBrandModalCtrl = ($modalInstance, newBrandName, showInput) ->
      init = () =>
        @newBrandName = newBrandName
        @disableSave = @showInput = showInput
        @inputChange = inputChange
        @ok = ok
        @cancel = cancel

      inputChange = =>
        @disableSave = (@newBrandName.length <= 1)

      ok = =>
        $modalInstance.close @newBrandName

      cancel = ->
        $modalInstance.dismiss()

      init()
      return

    NewBrandModalCtrl
      .$inject = ['$modalInstance', 'newBrandName', 'showInput']


    AddTaxModalCtrl = ($modalInstance) ->
      init = () =>
        @newTax = new Tax("", 0.0)
        @disableSave = true
        @inputChange = inputChange
        @ok = ok
        @cancel = cancel

      inputChange = =>
        @disableSave = @newTax.name.length <= 1

      ok = =>
        $modalInstance.close @newTax

      cancel = ->
        $modalInstance.dismiss()

      init()
      return

    AddTaxModalCtrl
      .$inject = ['$modalInstance']


    DetailProductCtrl = ($log, routes, $routeParams, localize) ->
      init = () =>
        @tagsList = tagsList
        activate()

      activate = () =>
        routes.controllers.products.Products.getProduct($routeParams.id).get()
          .success (product) =>
            @product = product

      tagsList = () =>
        if @product? then (tag.name for tag in @product.tags).join ', ' else null

      init()
      return

    DetailProductCtrl
      .$inject = ['$log', 'playRoutes', '$routeParams', 'localize']

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
    .controller('ProductsCtrl', ProductsController)

    angular
    .module('app.products')
    .controller('NewProductCtrl', NewProductController)

    angular
    .module('app.products')
    .controller('NewBrandModalCtrl', NewBrandModalCtrl)

    angular
    .module('app.products')
    .controller('AddTaxModalCtrl', AddTaxModalCtrl)

    angular
    .module('app.products')
    .controller('DetailProductCtrl', DetailProductCtrl)
)