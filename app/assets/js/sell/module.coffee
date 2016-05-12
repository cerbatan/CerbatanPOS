define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app.sell',
      [
        'ngAnimate'
        'ui.bootstrap'
        'ngSanitize'
        'ui.select'
        'play.routing'
        'app'
      ]
)