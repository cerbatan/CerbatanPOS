define(['./module']
  (module) ->
    SellCtrl = ($log, backend, shoppingListsService, $modal) ->
      bookmarks = [
        {
          name: 'Dicloxacilina  200mg 30'
          retailPrice: 14500
        }
        {
          name: 'Second Product'
          retailPrice: 5600
        }
        {
          name: 'Second Product'
          retailPrice: 5600
          fraction:
            name: "Sobre"
        }
        {
          name: 'Second Product'
          retailPrice: 5600
        }
        {
          name: 'Second Product'
          retailPrice: 5600
          fraction:
            name: "Sobre"
        }
        {
          name: 'Second Product'
          retailPrice: 5600
        }
        {
          name: 'Second Product'
          retailPrice: 5600
          fraction:
            name: "Sobre"
        }
      ]
      lastInsertedProduct = null

      init = () =>
        @selectedProduct = null

        @bookmarks = bookmarks
        @shoppingList = shoppingList
        @deleteProduct = deleteProduct
        @bookmarkProduct = bookmarkProduct
        @showProduct = showProduct
        @price = price
        @taxes = taxes
        @totalPrice = totalPrice
        @searchProducts = searchProducts
        @selectSearchInput = false
        @productSelected = productSelected
        @searchEnterPressed = searchEnterPressed
        @searchModified = searchModified
        @charge = charge
        activate()

      activate = () =>

      bookmarkProduct = (product) =>
        $log.info "Product bookmarked: #{product.name}"

      showProduct = (product) =>
        $log.info "Show Product: #{product.name}"

      shoppingList = () ->
        shoppingListsService.currentShoppingList().products()

      price  = () ->
        shoppingListsService.currentShoppingList().price()

      taxes = () ->
        shoppingListsService.currentShoppingList().taxes()

      totalPrice  = () ->
        shoppingListsService.currentShoppingList().totalPrice()

      searchProducts = (filter) ->
        backend.listProducts(filter).then(
          (response) ->
            response.data
        )

      productSelected = ($item, $model, $label) ->
        $log.info "Product selected #{$label}, #{$item.sku}, #{@selectedProduct}"
        addProductToList($item)
        @selectSearchInput = true

      deleteProduct = (product) =>
        shoppingListsService.currentShoppingList().remove(product)
        $log.info "Product deleted: #{product.name}"

      insertIntoShoppingList = (p) =>
        shoppingListsService.currentShoppingList().insert(p)
        lastInsertedProduct = p

      addProductToList = (product) ->
        backend.getProduct(product.id).then(
          (response) ->
            soldProduct = response.data

            if soldProduct.fractions? and soldProduct.fractions.length > 0
              showFractionsSelectionDialog(soldProduct.fractions).result.then(
                (selectedFraction) =>
                  soldProduct.fractions = [selectedFraction]
                  insertIntoShoppingList(soldProduct)
                ->
                  $log.warn 'Rejected'
              )
            else
              insertIntoShoppingList(soldProduct)
        )


      showFractionsSelectionDialog = (fractions) ->
        $modal.open
          templateUrl: "selectFractionModal.html"
          controller: 'SelectFractionCtrl'
          controllerAs: 'selectFractionCtrl'
          size: 'md'
          resolve:
            fractions: () -> fractions

      searchModified = () =>
        lastInsertedProduct = null
        @selectSearchInput=false


      searchEnterPressed = () =>
        insertIntoShoppingList(lastInsertedProduct) unless lastInsertedProduct == null
        $log.info "Enter pressed #{@selectedProduct.name}"

      charge = () =>
        list = shoppingListsService.currentShoppingList()
        unless list.isEmpty()
          showRegisterPaymentDialog(list.totalPrice()).result.then(
            (paymentDescription) =>
              $log.info 'Payment Registered'

              backend.register({items: list.getListBrief(), details: paymentDescription}).then(
                (response) ->
                  $log.info response.data
              )
            ->
              $log.info 'Payment Cancelled'
          )


      showRegisterPaymentDialog = (payment) ->
        $modal.open
          templateUrl: "registerPaymentModal.html"
          controller: 'RegisterPaymentCtrl'
          controllerAs: 'paymentCtrl'
          size: 'md'
          resolve:
            payment: () -> payment

      init()
      return

    SellCtrl
    .$inject = ['$log', 'sellingBackend', 'shoppingListsService', '$uibModal']


    SelectFractionCtrl = ($modalInstance, fractions) ->
      init = () =>
        @fractions = fractions
        @selected = selected
        @cancel = cancel
        @activeIndex = 0
        @keyDown = keyDown

      selected  = ($index) =>
        $modalInstance.close @fractions[$index]

      cancel = ->
        $modalInstance.dismiss()

      keyDown = (event) =>
        switch event.keyCode
          when 40 then arrowDownPressed()
          when 38 then arrowUpPressed()
          when 13 then enterPressed()
          when 27 then cancel()
        event.stopPropagation()
        event.preventDefault()

      arrowDownPressed = =>
        @activeIndex++ if @activeIndex < (@fractions.length - 1)

      arrowUpPressed = =>
        @activeIndex-- if @activeIndex > 0

      enterPressed = =>
        selected(@activeIndex)

      init()
      return

    SelectFractionCtrl
    .$inject = ['$uibModalInstance', 'fractions']


    RegisterPaymentCtrl = ($modalInstance, payment) ->
      init = () =>
        @payment = payment
        @tendered = payment
        @change = change
        @ok = ok
        @cancel = cancel

      change = =>
        change = @tendered - payment
        if change >= 0 then change else 0

      ok  =  =>
        $modalInstance.close @tendered

      cancel = ->
        $modalInstance.dismiss()

      init()
      return

    RegisterPaymentCtrl
    .$inject = ['$uibModalInstance', 'payment']


    module.controller('SellCtrl', SellCtrl)
    module.controller('SelectFractionCtrl', SelectFractionCtrl)
    module.controller('SelectFractionCtrl', SelectFractionCtrl)
    module.controller('RegisterPaymentCtrl', RegisterPaymentCtrl)
    return
)