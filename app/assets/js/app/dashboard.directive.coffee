define ['jquery', './module'],
  ($, app) ->
    (->
      DashboardDirective  = ->
        restrict: 'C'
        link: (scope, element, attrs) ->
          $(element).parents().addClass('fill-height')

          element.on '$destroy',
            ->
              $(element).parents().removeClass('fill-height') unless $(element).parent().children().hasClass("page-dashboard")


      app.directive 'pageDashboard', DashboardDirective

    )()