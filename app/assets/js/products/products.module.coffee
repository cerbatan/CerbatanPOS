'use strict'

require(['jquery', 'angular']
  ($, angular) ->
    angular
    .module('app.products', [
        'ngSanitize'
        'ngTagsInput'
        'ui.select'
        'play.routing'
        'app.common'
      ])


    return
)