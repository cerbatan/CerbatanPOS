define(['angular', './module']
  (ng, app) ->
    (->
      'use strict'
      MoneyDirective = () ->
        link = (scope, el, attrs, ngModelCtrl) ->
          min = parseFloat(attrs.min or 0)
          precision = parseFloat(attrs.precision or 2)
          lastValidValue = undefined

          round = (num) ->
            d = 10 ** precision
            Math.round(num * d) / d

          formatPrecision = (value) ->
            parseFloat(value).toFixed precision

          formatViewValue = (value) ->
            if ngModelCtrl.$isEmpty(value) then '' else '' + value

          ngModelCtrl.$parsers.push (value) ->
            value = '' if ng.isUndefined(value)

            if attrs.interpret and attrs.interpret == 'true'
              try
                evaluatedValue = eval(value.replace(/([\+\-\*]{1})\s*(\d+|(?:\d*(?:\.\d*)))%/g, '*(1$1$2/100)'))
                ngModelCtrl.$setValidity('number', true)
                return evaluatedValue
              catch e
                ngModelCtrl.$setValidity 'number', false
              value

            else
# Handle leading decimal point, like ".5"
              value = '0' + value if value.indexOf('.') == 0

              # Allow "-" inputs only when min < 0
              if value.indexOf('-') == 0
                if min >= 0
                  value = null
                  ngModelCtrl.$setViewValue ''
                  ngModelCtrl.$render()
                else if value == '-'
                  value = ''

              empty = ngModelCtrl.$isEmpty(value)
              if empty or NUMBER_REGEXP.test(value)
                lastValidValue = (if value == '' then null else if empty then value else parseFloat(value))
              else
# Render the last valid input in the field
                ngModelCtrl.$setViewValue formatViewValue(lastValidValue)
                ngModelCtrl.$render()

              ngModelCtrl.$setValidity 'number', true
              lastValidValue

          ngModelCtrl.$formatters.push formatViewValue

          minValidator = (value) ->
            if !ngModelCtrl.$isEmpty(value) and value < min
              ngModelCtrl.$setValidity 'min', false
              undefined
            else
              ngModelCtrl.$setValidity 'min', true
              value

          ngModelCtrl.$parsers.push minValidator
          ngModelCtrl.$formatters.push minValidator

          if attrs.max
            max = parseFloat(attrs.max)

            maxValidator = (value) ->
              if !ngModelCtrl.$isEmpty(value) and value > max
                ngModelCtrl.$setValidity 'max', false
                undefined
              else
                ngModelCtrl.$setValidity 'max', true
                value

            ngModelCtrl.$parsers.push maxValidator
            ngModelCtrl.$formatters.push maxValidator

          # Round off
          if precision > -1
            ngModelCtrl.$parsers.push (value) ->
              if value then round(value) else value
            ngModelCtrl.$formatters.push (value) ->
              if value then formatPrecision(value) else value

          el.bind 'blur', ->
            value = ngModelCtrl.$modelValue
            if value
              ngModelCtrl.$viewValue = formatPrecision(value)
              ngModelCtrl.$render()
            return


        NUMBER_REGEXP = /^\s*(\-|\+)?(\d+|(\d*(\.\d*)))\s*$/
        directive =
          restrict: 'A'
          require: 'ngModel'
          link: link

        return directive

      app.directive('money', MoneyDirective)

      return
    )()

    (->
      'use strict'
      FocusHere = ($timeout, $parse) ->
        link = (scope, element, attrs) ->
          attrValue = (if attrs.focusHere.length == 0 then 'true' else attrs.focusHere)
          model = $parse(attrValue)
          hasSelect = true if attrs.focusSelect?
          focusDelay = if attrs.focusDelay? and attrs.focusDelay.length > 0 then scope.$eval(attrs.focusDelay) else 0

          applyIt = ->
              element[0].focus()
              if hasSelect == true
                $timeout ->
                  element[0].select()
              return


          scope.$watch model, (value) ->
            if value == true
              $timeout applyIt, focusDelay
            return
          return

        directive =
          link: link

        return directive

      app.directive('focusHere', ['$timeout', '$parse',FocusHere])
    )()

    (->
      'use strict'
      SelectHere = ($timeout, $parse) ->
        link = (scope, element, attrs) ->
          attrValue = (if attrs.selectHere.length == 0 then 'true' else attrs.selectHere)
          model = $parse(attrValue)
          scope.$watch model, (value) ->
            if value == true
              $timeout ->
                element[0].select()
                return
            return
          return

        directive =
          link: link

        return directive

      app.directive('selectHere', ['$timeout', '$parse',SelectHere])
    )()

    (->
      'use strict'
      KeyTrap = ($document) ->
        link = (scope, element, attrs) ->
          keyDownHandler = (e) -> scope.$apply(scope.keyTrap({event: e}))

          $document.bind 'keydown', keyDownHandler
          element.on '$destroy',
            ->
              $document.unbind 'keydown', keyDownHandler

          return

        directive =
          restrict: 'A'
          scope:
            keyTrap: '&'
          link: link

        return directive

      app.directive('keyTrap', ['$document',KeyTrap])
    )()

    (->
      'use strict'
      SelectOnFocus = ($document) ->
        link = (scope, element, attrs) ->
          focusedElement = null
          onFocusHandler = () ->
            if focusedElement != @
              element[0].select()
              focusedElement = @

          onBlurHandler = () ->
            focusedElement = null

          element.on "click", onFocusHandler
          element.on "blur", onBlurHandler

          return

        directive =
          restrict: 'A'
          scope:
            keyTrap: '&'
          link: link

        return directive

      app.directive('selectOnFocus', ['$document',SelectOnFocus])
    )()

    (->
      CheckBox = () ->
        link = (scope, elem, attrs, modelCtrl) ->
          scope.size = 'default'
          # Default Button Styling
          scope.stylebtn = {}
          # Default Checkmark Styling
          scope.styleicon =
            'width': '10px'
            'left': '-1px'
          # If size is undefined, Checkbox has normal size (Bootstrap 'xs')
          if attrs.large != undefined
            scope.size = 'large'
            scope.stylebtn =
              'padding-top': '2px'
              'padding-bottom': '2px'
              'height': '30px'
            scope.styleicon =
              'width': '8px'
              'left': '-5px'
              'font-size': '17px'
          if attrs.larger != undefined
            scope.size = 'larger'
            scope.stylebtn =
              'padding-top': '2px'
              'padding-bottom': '2px'
              'height': '34px'
            scope.styleicon =
              'width': '8px'
              'left': '-8px'
              'font-size': '22px'
          if attrs.largest != undefined
            scope.size = 'largest'
            scope.stylebtn =
              'padding-top': '2px'
              'padding-bottom': '2px'
              'height': '45px'
            scope.styleicon =
              'width': '11px'
              'left': '-11px'
              'font-size': '30px'
          trueValue = true
          falseValue = false
          # If defined set true value
          trueValue = attrs.ngTrueValue if attrs.ngTrueValue != undefined

          # If defined set false value
          falseValue = attrs.ngFalseValue if attrs.ngFalseValue != undefined

          # Check if name attribute is set and if so add it to the DOM element
          elem.name = scope.name if scope.name != undefined

          # Update element when model changes
          scope.$watch(
            ->
              if modelCtrl.$modelValue == trueValue or modelCtrl.$modelValue == true
                modelCtrl.$setViewValue trueValue
              else
                modelCtrl.$setViewValue falseValue
              modelCtrl.$modelValue
            (newVal, oldVal) ->
              scope.checked = (modelCtrl.$modelValue == trueValue)
              return
            true
          )
          # On click swap value and trigger onChange function
          elem.bind 'click', ->
            scope.$apply ->
              if modelCtrl.$modelValue == falseValue
                modelCtrl.$setViewValue trueValue
              else
                modelCtrl.$setViewValue falseValue
              return
            return
          return

        directive =
          link: link
          scope: {},
          require: "ngModel",
          restrict: "E",
          replace: "true",
          template: "<button type=\"button\" ng-style=\"stylebtn\" class=\"btn btn-default\" ng-class=\"{'btn-xs': size==='default', 'btn-sm': size==='large', 'btn-lg': size==='largest', 'checked': checked===true}\">" +
            "<span ng-style=\"styleicon\" class=\"glyphicon\" ng-class=\"{'glyphicon-ok': checked===true}\"></span>" +
            "</button>"

        return directive

      app.directive("checkbox", CheckBox)
    )()

    (->
      GoBackDirective = () ->
        GoBackController = ($scope, $element, $window) ->
          $element.on 'click', ->
            $window.history.back()

        GoBackController
        .$inject = ['$scope', '$element', '$window']

        directive =
          restrict: "A",
          controller: GoBackController

      app.directive("goBack", GoBackDirective)
    )()


    (->
      ImgDirective  = ->
        restrict: 'A'
        link: (scope, ele, attrs) ->
          Holder.run images: ele[0]

      app.directive 'imgHolder', ImgDirective

    )()

    (->
      CustomPageDirective  = ->
        CustomPageController = ($scope, $element, $location) ->
          path = ->
            $location.path()

          addBg = (path) ->
            $element.removeClass 'body-wide body-lock'
            switch path
              when '/404', '/pages/404', '/pages/500', '/pages/signin', '/pages/signup', '/pages/forgot-password'
                return $element.addClass('body-wide')
              when '/pages/lock-screen'
                return $element.addClass('body-wide body-lock')
            return

          addBg $location.path()
          $scope.$watch path, (newVal, oldVal) ->
            if newVal == oldVal
              return
            addBg $location.path()

        CustomPageController.$inject = ['$scope', '$element', '$location']

        directive =
          restrict: "A",
          controller: CustomPageController

      app.directive 'customPage', CustomPageDirective
    )()



    return
)