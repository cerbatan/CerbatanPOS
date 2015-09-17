require(['jquery', 'angular']
  ($, angular) ->
    config = (toastrConfig) ->
      angular.extend toastrConfig,
        closeButton: true
        allowHtml: true
        positionClass: "toast-bottom-left"
        timeOut: "4000"
        target: "div.main-container"

    angular
    .module('app.common', ['toastr'])
    .config(config)

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
            value = '' if angular.isUndefined(value)

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

      angular
      .module('app.common')
      .directive('money', MoneyDirective)

      return
    )()

    (->
      'use strict'
      FocusHere = ($timeout, $parse) ->
        link = (scope, element, attrs) ->
          attrValue = (if attrs.focusHere.length == 0 then 'true' else attrs.focusHere)
          model = $parse(attrValue)
          scope.$watch model, (value) ->
            console.log 'value=', value
            if value == true
              $timeout ->
                element[0].focus()
                return
            return
          return

        directive =
          link: link

        return directive

      angular
      .module('app.common')
      .directive('focusHere', ['$timeout', '$parse',FocusHere])
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

      angular
      .module('app.common')
      .directive("checkbox", CheckBox)
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

        return directive

      angular
      .module('app.common')
      .directive("goBack", GoBackDirective)
    )()

    (->
      Notifier = (toastr) ->
        # toastr setting.
        toastrOptions =
          closeButton: true
          positionClass: "toast-bottom-right"
          timeOut: "3000"

        logIt = (message, type) ->
          toastr[type](message)

        return {
          log: (message) ->
            logIt(message, 'info')
            return

          logWarning: (message) ->
            logIt(message, 'warning')
            return

          logSuccess: (message) ->
            logIt(message, 'success')
            return

          logError: (message) ->
            logIt(message, 'error')
            return
        }

      angular
      .module('app.common')
      .factory('notifier', ['toastr', Notifier])
    )()

    return
)