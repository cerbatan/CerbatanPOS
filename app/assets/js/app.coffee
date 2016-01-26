require(
  [
    'requirejs-domready'
    'angular'
    'angular-toastr'
    'angular-sanitize'
    'angular-route'
    'angular-animate'
    'angular-scroll'
    'angular-strap-tpl'
    'ui-bootstrap-tpls'
    'angular-tags-input'
    'angular-ui-select'
    'app/playRoutes'
    'app/index'
    'localize'
    'products/index'
    'sell/index'
    'stock/index'
    'task/task'
  ]
  (domReady, ng) ->
    'use strict'

    domReady ->
      ng.bootstrap document.body, [ 'app' ]
      return
    return
)