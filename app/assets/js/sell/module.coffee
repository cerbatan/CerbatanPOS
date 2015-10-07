define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app.sell',
      [
        'ngSanitize'
        'ui.select'
        'play.routing'
        'app'
      ]
)