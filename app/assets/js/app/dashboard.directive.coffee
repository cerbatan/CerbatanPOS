define ['jquery', './module'],
  ($, app) ->
    (->
      DashboardDirective  = ->
        restrict: 'C'
        link: (scope, element, attrs) ->
          $(element).parents().addClass('fill-height')

          element.on '$destroy',
            ->
              $(element).parents().removeClass('fill-height')


      app.directive 'pageDashboard', DashboardDirective

    )()