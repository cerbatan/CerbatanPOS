define(['./module']
  (module) ->
    'use strict'
    (->
      class SoldItem
        constructor: (product) ->
          @listedProduct = -> product
          @id = product.id
          @name = product.name
          @count = 1

          if product.fractions? and product.fractions.length > 0
            @retailPrice = product.fractions[0].price
            @fractionName = product.fractions[0].name
            @fractionId = product.fractions[0].id
          else
            @retailPrice = @listedRetailPrice()
            @fractionName = null
            @fractionId = null

        price : () -> @retailPrice / (1 + @listedTax() / 100)

        taxes: () -> @retailPrice - @price()

        listedPrice: () -> @listedProduct().price

        listedTax: () ->
          if @listedProduct().tax? then @listedProduct().tax.percentage else 0.0

        listedRetailPrice: () ->  if @listedProduct().fractions? and @listedProduct().fractions.length > 0 then @listedProduct().fractions[0].price else @listedProduct().retailPrice

        sameAs: (o) ->
          if @id == o.id
            if @fractionId != null
              o.fractions? && o.fractions.length > 0 && o.fractions[0].id == @fractionId
            else
              (not o.fractions?) or o.fractions.length == 0
          else
            false


      class ShoppingList
        productsList = []

        constructor: (list) ->
          productsList = if list? then list else []

        products: () ->
          productsList

        price: () ->
          productsList.reduce ((t, c) -> t + (c.count * c.price()) ), 0

        taxes: () ->
          productsList.reduce ((t, c) -> t + c.count * c.taxes() ), 0

        totalPrice: () ->
          productsList.reduce ((t, c) -> t + c.count * c.retailPrice), 0

        insert: (p) ->
          updated = false
          for product in productsList
            if product.sameAs(p)
              product.count++
              updated = true
              break

          productsList.push(new SoldItem(p)) unless updated

        remove: (p) ->
          productIndex = productsList.indexOf(p)
          productsList.splice(productIndex, 1) if productIndex isnt -1

        isEmpty: ->
          productsList.length == 0

      ShoppingListsService = () ->
        init = =>
          @currentList = new ShoppingList()

        getCurrent = () =>
          @currentList

        init()

        return {
        currentShoppingList: getCurrent

#          flushTagsCache: ->
#            $cacheFactory.get('$http').remove playRoutes.controllers.products.Products.tags().url
        }

      module.factory('shoppingListsService', [ShoppingListsService]))()

    return
)