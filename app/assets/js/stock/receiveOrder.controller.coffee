define(['./module']
  (module) ->
    'use strict'

    ReceiveOrderController = ($log, $modal, ordersService, stockRoutes, backend) ->
      init = () =>
        @addItem = addItem
        @adding = false

        @order = null;
        @itemToAdd = null

        @searchProduct = searchProduct
        @productSelected = productSelected
        @selectedProduct = null
        @selectSearchInput = false
        @addCurrentItem = addCurrentItem
        @removeOrderItem = removeOrderItem
        @editCurrentItem = editCurrentItem
        @itemToAddCostDiff = itemToAddCostDiff

        activate()

      addItem = () =>
        @adding = true
        @selectSearchInput = true
        @itemToAdd = null

      searchProduct = (filter) ->
        backend.searchProduct(filter).then(
          (response) ->
            response.data
        )

      productSelected = ($item, $model, $label) =>
        $log.info "Product selected #{$label}, #{$item.sku}, #{@selectedProduct}"
#        addProductToList($item)
        backend.getProduct(@selectedProduct.id).then(
          (response) =>
            addedProduct = response.data
            @itemToAdd = @order.newItem(addedProduct)
        )
        @selectSearchInput = true

      addCurrentItem = =>
        @order.addItem @itemToAdd
        @itemToAdd = null
        @adding = false
        @selectSearchInput = false
        @selectedProduct = null

      editCurrentItem = =>
        showEditItemDialog(@itemToAdd).result.then(
          =>
            $log.info 'Finish Editing Item'
          ->
        )

      itemToAddCostDiff = =>
        if @itemToAdd? then @itemToAdd.orderCost - @itemToAdd.product().cost else 0

      removeOrderItem = (i) =>
        @order.removeItem(i)

      showEditItemDialog = (item) ->
        $modal.open
          templateUrl: "updateProductModal.html"
          controller: 'EditOrderItemDialogCtrl'
          controllerAs: 'modalCtrl'
          resolve:
            orderItem: () -> item

      activate = =>
        #Get orderlist here
        @order = ordersService.newOrder()

      init()
      return

    ReceiveOrderController
      .$inject = ['$log','$uibModal', 'ordersService', 'stockRoutes', 'ordersBackend']


    EditOrderItemDialogCtrl = ($modalInstance, orderItem) ->
      init = () =>
        @item = orderItem
        @product = orderItem.product()
        @ok = ok
        @cancel = cancel
        @orderCost = @item.orderCost
        @newPrice = @item.newPrice
        @newRetailPrice = @item.newRetailPrice
        @fractionsNewPrices = @item.fractionsNewPrices.map (f) -> { name: fractionName(f.id), id: f.id, newPrice: f.newPrice, qty: fractionQty(f.id) }

        @orderCostUpdated = orderCostUpdated
        @priceUpdated = priceUpdated
        @retailPriceUpdated = retailPriceUpdated
        @markupUpdated = markupUpdated
        @costDifference = costDifference
        updateProductMarkup()

      ok = =>
        persistItemDetails()
        $modalInstance.close()

      cancel = ->
        $modalInstance.dismiss()

      fractionName = (fractionId)=>
        fraction = (@product.fractions.filter (f) -> f.id == fractionId)[0]
        fraction.name

      fractionQty = (fractionId) =>
        fraction = (@product.fractions.filter (f) -> f.id == fractionId)[0]
        fraction.qty

      costDifference = =>
        @orderCost - @product.cost

      updateProductMarkup = =>
        @markup = if @orderCost > 0 then (@newPrice / @orderCost - 1.0) * 100.0 else 0.0

      orderCostUpdated = (isValid)->
        if isValid
          updateProductMarkup()

      priceUpdated = =>
        updateProductMarkup()
        updateRetailPrice()
        updateFractions()

      retailPriceUpdated = ()=>
        if @product.tax?
          @newPrice = @newRetailPrice/(1.0+ @product.tax.percentage / 100.0)
        else
          @newPrice = @newRetailPrice
        updateProductMarkup()
        updateFractions()

      updateRetailPrice = =>
        if @product.tax?
          @newRetailPrice = @newPrice * (1.0 + @product.tax.percentage / 100.0)
        else
          @newRetailPrice = @newPrice

      updateFractions = () =>
        for fraction in @fractionsNewPrices
          do (fraction) =>
            p = @newRetailPrice/fraction.qty
            fraction.newPrice = p if fraction.newPrice < p
            return

      markupUpdated = () =>
        @newPrice = @orderCost * (1.0 + @markup / 100.0)
        updateRetailPrice()
        updateFractions()


      persistItemDetails = () =>
        @item.orderCost = @orderCost
        @item.newPrice = @newPrice
        @item.newRetailPrice = @newRetailPrice
        @item.fractionsNewPrices = ({id: f.id, newPrice: f.newPrice} for f in @fractionsNewPrices)



      init()
      return

    EditOrderItemDialogCtrl
    .$inject = ['$uibModalInstance', 'orderItem']


    module.controller('ReceiveOrderCtrl', ReceiveOrderController)
    module.controller('EditOrderItemDialogCtrl', EditOrderItemDialogCtrl)

    return
)