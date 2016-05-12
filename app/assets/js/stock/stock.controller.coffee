define(['./module']
  (module) ->
    'use strict'

    StockController = (stockRoutes) ->
      init = () =>
        @goToReceiveOrder = goToReceiveOrder
        activate()

      goToReceiveOrder = () ->
        stockRoutes.receiveOder()


      activate = =>
        #Get orderlist here

      init()
      return

    StockController
      .$inject = ['stockRoutes']


    module.controller('StockCtrl', StockController)

    return
)