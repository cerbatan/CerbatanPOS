define(['./module']
  (module) ->
    'use strict'
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
      constructor: (@name, @item, @qty, @price) ->
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

    ProductsController = (productsRoutes, productsBackend, $route) ->

      init = () =>
        @totalProducts = 0
        @products = []
        @goToAddProduct = goToAddProduct
        @showProduct = showProduct
        @pageChange = pageChange
        @queryChanged = queryChanged
        activate()

      goToAddProduct = () ->
        productsRoutes.addProduct()

      activate = =>
        @queryFilter = $route.current.params.q
        page = $route.current.params.p

        productsBackend.getProductsBrief(@queryFilter, page).then(
          (response) =>
            @totalProducts = response.data.total
            @products = response.data.briefs
        )
        @currentPage = if page? then page else 1

      showProduct = (productId) ->
        productsRoutes.showProduct(productId)

      queryChanged = () =>
        productsRoutes.showBriefs(@queryFilter, null)

      pageChange = () =>
        productsRoutes.showBriefs(@queryFilter, @currentPage)

      init()
      return

    ProductsController
      .$inject = ['productsRoutes', 'productsBackend', '$route']


    EditProductController = ($filter, $log, $modal, productsBackend, productsRoutes, localize, notifier, preparedTaxes, preparedProduct) ->
      lastSearch = " "
      submitOperation = null
      successMsg = "success"
      errorMsg = "error"

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
        @addFraction = addFraction
        @removeFraction = removeFraction
        @saveProduct = saveProduct
        @fractionProportionChanged = fractionProportionChanged
        @fractionPriceChanged = fractionPriceChanged

        activate()

      activate = =>
        populateTaxes(preparedTaxes.data)
        if preparedProduct?
          @product = preparedProduct.data
          submitOperation = productsBackend.updateProduct
          successMsg = "Product Updated"
          errorMsg = "Failed to update product"

        else
          @product = new Product()
          submitOperation = productsBackend.addProduct
          successMsg = "Product Added"
          errorMsg = "Failed to create product.<br/>This SKU may already exist."

        updateProductMarkup()

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
          controllerAs: 'modalCtrl'

      refreshBrands = ($select) =>
        filter = $select.search
        if (lastSearch.length == 0 and filter.length > 0) or filter.toLowerCase().indexOf(lastSearch.toLocaleLowerCase()) != 0
          lastSearch = filter
          productsBackend.getBrands(filter).then(
            (response) =>
              brands = response.data
              if brands.length == 0
                brands.push new Brand(filter)
              @brands = brands
          )

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
            showAddBrandDialog(newBrandName).result.then(
              =>
                $log.info 'Adding Brand'
                productsBackend.addBrand(newBrandName).then(
                  (response) =>
                    @product.brand = response.data
                  () ->
                    $log.warning 'Failed brand creation'
                )
              ->
                $select.selected = null
            )
        $select.search = ''

      addBrand = =>
        showAddBrandDialog('', true).result.then(
          (newBrandName) =>
            $log.info 'Adding Brand'
            productsBackend.addBrand(newBrandName).then(
              (response) =>
                @product.brand = response.data
              ->
                $log.warning 'Failed brand creation'
            )
          ->
        )

      loadTags = ($query) ->
        productsBackend.getTags(true).then (response) ->
          $filter('filter') response.data, $query

      handleAddingTag = ($tag) =>
        if $tag.id == undefined or $tag.id == null
          $log.info 'Adding Tag'
          productsBackend.addTag($tag.name).then(
            (response) =>
              tag = response.data
              @product.tags.pop()
              @product.tags.push tag
              productsBackend.flushTagsCache()
            =>
              @product.tags.pop()
              $log.warning 'Failed tag creation'
          )


      getTaxes = () =>
        productsBackend.getTaxes().then(
          (response) ->
            populateTaxes(response.data)
        )

      populateTaxes = (taxes) =>
        @taxes = taxes
        @taxes.push(new Tax("--- " + localize.getLocalizedString("Add") + " ---", 0.0))

      updatePrice = =>
        @product.price = @product.cost * (1.0 + @product.markup / 100.0)

      updateRetailPrice = =>
        if @product.tax?
          @product.retailPrice = @product.price * (1.0 + @product.tax.percentage / 100.0)
        else
          @product.retailPrice = @product.price
        refreshFractions()


      productCostUpdated = (isValid)->
        if isValid
          updatePrice()
          updateRetailPrice()

      productMarkupUpdated = ->
        updatePrice()
        updateRetailPrice()

      productPriceUpdated = =>
        updateProductMarkup()
        updateRetailPrice()

      updateProductMarkup = =>
        @product.markup = if @product.cost > 0 then (@product.price / @product.cost - 1.0) * 100.0 else 0.0

      taxSelected = (select) =>
        if select.selected.id == null
          showAddTaxDialog().result.then(
            (newTax) =>
              $log.info 'Adding Tax'
              productsBackend.addTax(newTax).then(
                (response) =>
                  @product.tax = response.data
                  taxChanged()
                  getTaxes()
                ->
                  $log.warning 'Failed tax creation'
              )
            ->
              select.selected = null
          )


      taxChanged = updateRetailPrice

      retailPriceUpdated = =>
        if @product.tax?
          @product.price = @product.retailPrice / (1.0 + @product.tax.percentage / 100.0)
        else
          @product.price = @product.retailPrice

        updateProductMarkup()
        refreshFractions()

      refreshFractions = =>
        for f in @product.fractions
          if f.price?
            f.price = @product.retailPrice/f.qty if f.price < @product.retailPrice/f.qty
          else
            f.price = @product.retailPrice/f.qty if f.qty?


      addFraction = =>
        if @product.fractions.length == 0
          @product.fractions.push(new Fraction("Regular", @product.id, 1, @product.retailPrice))

        @product.fractions.push(new Fraction(null, @product.id, null, null, null))

      removeFraction = (index) =>
        if  index == 1 and  @product.fractions.length <= 2
          @product.fractions = []
        else
          @product.fractions.splice(index, 1)

      saveProduct = (isValid) =>
        if isValid
          @saving = true
          submitOperation(@product).then(
            (response) =>
              productId = response.data
              notifier.logSuccess localize.getLocalizedString(successMsg)
              productsRoutes.showProduct(productId)
              @saving = false
            (errorResponse) =>
              $log.warn("Failed product creation")
              @saving = false
              notifier.logError localize.getLocalizedString(errorMsg)
          )

      fractionProportionChanged = (index) =>
        fraction = @product.fractions[index]
        fraction.price = @product.retailPrice/fraction.qty

      fractionPriceChanged = (index, validatorField) =>
        fraction = @product.fractions[index]
        validatorField.$setValidity("fractionPriceGtRetailPrice", @product.retailPrice/fraction.qty <= fraction.price)

      init()
      return

    EditProductController
      .$inject = ['$filter','$log', '$uibModal', 'productsBackend', 'productsRoutes', 'localize', 'notifier', 'preparedTaxes', 'preparedProduct' ]


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
      .$inject = ['$uibModalInstance', 'newBrandName', 'showInput']


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
      .$inject = ['$uibModalInstance']


    DetailProductCtrl = ($log, productsRoutes, preparedProduct) ->
      init = () =>
        @tagsList = tagsList
        @edit = edit
        activate()

      activate = () =>
        @product = preparedProduct.data

      tagsList = () =>
        if @product? then (tag.name for tag in @product.tags).join ', ' else null

      edit = () =>
        productsRoutes.editProduct(@product.id)

      init()
      return

    DetailProductCtrl
      .$inject = ['$log', 'productsRoutes', 'preparedProduct']

    module.controller('ProductsCtrl', ProductsController)

    module.controller('EditProductCtrl', EditProductController)

    module.controller('NewBrandModalCtrl', NewBrandModalCtrl)

    module.controller('AddTaxModalCtrl', AddTaxModalCtrl)

    module.controller('DetailProductCtrl', DetailProductCtrl)

    return
)