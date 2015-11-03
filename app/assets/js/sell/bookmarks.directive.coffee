define ['angular', './module'],
  (ng, app) ->
    (->
      BookmarksDirective  = ->
        restrict: 'C'
        scope:
          products: '=products'

        templateUrl: 'views/templates/bookmarks.html'
        link: (scope, element, attrs) ->


      app.directive 'bookmarks', BookmarksDirective

    )()