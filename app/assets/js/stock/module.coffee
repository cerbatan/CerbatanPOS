define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app.stock',
      [
        'ngSanitize'
        'ngTagsInput'
        'ui.select'
        'play.routing'
        'app'
      ]
)