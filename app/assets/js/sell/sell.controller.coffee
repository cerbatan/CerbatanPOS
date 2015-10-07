define(['./module']
  (module) ->
    SellCtrl = ($log) ->
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

      cart = [
        {
          name: 'Dicloxacilina  200mg 30'
          retailPrice: 14500
          cost: 12600
          isBookmarked: true
          count: 1
        }
        {
          name: 'Amoxal  200mg 30'
          retailPrice: 14500
          cost: 12600
          isBookmarked: true
          count: 1
        }
      ]
      init = () =>
        @bookmarks = bookmarks
        @cart = cart
        activate()

      activate = () =>

      init()
      return

    SellCtrl
    .$inject = ['$log']

    module.controller('SellCtrl', SellCtrl)
    return
)