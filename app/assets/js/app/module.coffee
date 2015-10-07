define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app',
      [
        'ngRoute'
        'ui.bootstrap'
        'duScroll'
        'toastr'
        'app.localization'
        'app.task'
        'app.sell'
        'app.products'
      ]
)