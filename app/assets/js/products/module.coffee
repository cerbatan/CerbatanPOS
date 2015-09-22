define(['angular']
  (ng) ->
    'use strict'
    ng.module 'app.products',
      [
        'ngSanitize'
        'ngTagsInput'
        'ui.select'
        'play.routing'
        'app'
      ]
)