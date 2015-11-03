define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app.sell',
      [
        'ngAnimate'
        'ui.bootstrap'
        'mgcrea.ngStrap.popover'
        'ngSanitize'
        'ui.select'
        'play.routing'
        'app'
      ]
)