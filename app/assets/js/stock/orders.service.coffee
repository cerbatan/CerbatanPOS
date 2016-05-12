define(['./module']
  (module) ->
    'use strict'
    (->
      class OrderItem
        constructor: (product) ->
          @product = () -> product
          @itemId = product.id
          @qty = 1
          @orderCost = product.cost
          @fractionsNewPrices = ({id: f.id, newPrice: f.price} for f in (product.fractions.filter (x) -> x.qty != 1))
          @newRetailPrice = product.retailPrice
          @newPrice = product.price

        totalCost: =>
          @qty*@orderCost

      class Order
        constructor: () ->
          @id = null
          @supplier = null
          @invoiceNumber = null
          @items = []

        newItem: (p) ->
          new OrderItem(p)

        addItem: (i) ->
          look = (a, el, idx=0) ->
            if ( idx >= a.length )
              -1
            else
              if ( a[idx].itemId == el.itemId ) then idx else look(a, el, ++idx)

          index = look(@items, i)
          if ( index >= 0 )
            currentItem = @items[index]
            i.qty += currentItem.qty
            @items.splice(index, 1)

          @items.unshift(i)

        removeItem: (i) ->
          @items.splice(@items.indexOf(i), 1)




      OrdersService = () ->
        init = =>

        createOrder = () =>
          new Order()

        init()

        return {
          newOrder: createOrder

#          flushTagsCache: ->
#            $cacheFactory.get('$http').remove playRoutes.controllers.products.Products.tags().url
        }

      module.factory('ordersService', [OrdersService]))()

    return
)