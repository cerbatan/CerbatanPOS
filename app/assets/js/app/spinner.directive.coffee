define ['jquery', './module'],
  ($, app) ->
    (->
      SpinnerDirective  = ($timeout, $interval, $document) ->
        'use strict'
        key_codes =
          left: 37
          right: 39

        setScopeValues = (scope, attrs) ->
          scope.min = if attrs.min? then parseInt(attrs.min) or 0 else 0
          scope.max = if attrs.max? then parseInt(attrs.max) or 1000000 else 1000000
          scope.step = if attrs.step? then parseInt(attrs.step) or 1 else 1
          scope.focused = false

        linkFunction = (scope, element, attrs) ->
          setScopeValues(scope, attrs)

          $body = $document.find('body')
          lastValidVal = scope.val


          scope.change = ->
            lastValidVal = scope.val if scope.val?

          scope.decrement = ->
            scope.val-- if scope.val > scope.min
            lastValidVal = scope.val

          scope.increment = ->
            scope.val++ if scope.val < scope.max
            lastValidVal = scope.val

          scope.checkValue = ->
            if not scope.val?
              scope.val = lastValidVal
            scope.focused = false


          $body.bind 'keydown', (event) ->
            unless !scope.focused
              which = event.which
              if which == key_codes.right
                event.preventDefault()
                scope.increment()
              else if which == key_codes.left
                event.preventDefault()
                scope.decrement()
              scope.$apply()
          return

        return {
          restrict: 'EA'
          scope: {
            val: "=model"
          }
          replace: true
          link: linkFunction
          template:
            '<div class="input-group">' +
              '  <span class="input-group-btn">' +
              '    <button class="btn btn-default" ng-click="decrement()"><i class="fa fa-minus"></i></button>' +
              '  </span>' +
              '  <input focus-here class="form-control spinner" type="number" min="{{min}}" ng-model="val" class="form-control" ng-blur="checkValue()" ng-focus="focused=true" ng-change="change()" maxlength="6">' +
              '  <span class="input-group-btn">' +
              '    <button class="btn btn-success" ng-click="increment()"><i class="fa fa-plus"></i></button>' +
              '  </span>' +
              '</div>'
        }



      app.directive 'spinnerInput', ['$timeout', '$interval', '$document', SpinnerDirective]

    )()