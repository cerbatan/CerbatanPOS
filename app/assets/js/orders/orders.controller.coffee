define(['./module']
  (module) ->
    'use strict'

    OrdersController = (ordersRoutes, backend, $modal, $log) ->
      init = () =>
        @newOrder = newOrder
        activate()

      showNewOrderDialog = () ->
        $modal.open
          templateUrl: "newOrderModal.html"
          controller: 'AddOrderModalCtrl'
          controllerAs: 'modalCtrl'
          size: 'sm'

      newOrder = () ->
        showNewOrderDialog().result.then(
          (supplier) =>
            $log.info "Adding order with supplier #{supplier}"
            backend.createOrder(supplier).then(
              (response) =>
                orderId = response.data
                ordersRoutes.editOrder(orderId)
              ->
                $log.warning 'Failed creating order'
            )
        )

      activate = =>
        #Get orderlist here

      init()
      return

    OrdersController
      .$inject = ['ordersRoutes', 'ordersBackend', '$uibModal', '$log']


    AddOrderModalCtrl = ($modalInstance, $filter) ->
      init = () =>
        @supplier = null
        @ok = ok
        @cancel = cancel

      ok = =>
        $modalInstance.close @supplier

      cancel = ->
        $modalInstance.dismiss()

      init()
      return

    AddOrderModalCtrl
      .$inject = ['$uibModalInstance', '$filter']

    module.controller('OrdersCtrl', OrdersController)
    module.controller('AddOrderModalCtrl', AddOrderModalCtrl)

    return
)