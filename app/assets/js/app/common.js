require(['jquery', 'angular'], function ($, angular) {
    angular.module('app.common', [])
        .directive('money', function () {
            'use strict';

            var NUMBER_REGEXP = /^\s*(\-|\+)?(\d+|(\d*(\.\d*)))\s*$/;

            function link(scope, el, attrs, ngModelCtrl) {
                var min = parseFloat(attrs.min || 0);
                var precision = parseFloat(attrs.precision || 2);
                var lastValidValue;

                function round(num) {
                    var d = Math.pow(10, precision);
                    return Math.round(num * d) / d;
                }

                function formatPrecision(value) {
                    return parseFloat(value).toFixed(precision);
                }

                function formatViewValue(value) {
                    return ngModelCtrl.$isEmpty(value) ? '' : '' + value;
                }


                ngModelCtrl.$parsers.push(function (value) {
                    if (angular.isUndefined(value)) {
                        value = '';
                    }


                    if (attrs.interpret && attrs.interpret === "true") {
                        try {
                            var evaluatedValue = eval(value.replace(/([\+\-\*]{1})\s*(\d+|(?:\d*(?:\.\d*)))%/g, "*(1$1$2/100)"));
                            ngModelCtrl.$setValidity('number', true);
                            return evaluatedValue;
                        } catch (e) {
                            ngModelCtrl.$setValidity('number', false);
                        }
                        return value;
                    } else {
                        // Handle leading decimal point, like ".5"
                        if (value.indexOf('.') === 0) {
                            value = '0' + value;
                        }

                        // Allow "-" inputs only when min < 0
                        if (value.indexOf('-') === 0) {
                            if (min >= 0) {
                                value = null;
                                ngModelCtrl.$setViewValue('');
                                ngModelCtrl.$render();
                            } else if (value === '-') {
                                value = '';
                            }
                        }

                        var empty = ngModelCtrl.$isEmpty(value);
                        if (empty || NUMBER_REGEXP.test(value)) {
                            lastValidValue = (value === '')
                                ? null
                                : (empty ? value : parseFloat(value));
                        } else {
                            // Render the last valid input in the field
                            ngModelCtrl.$setViewValue(formatViewValue(lastValidValue));
                            ngModelCtrl.$render();
                        }

                        ngModelCtrl.$setValidity('number', true);
                        return lastValidValue;
                    }


                });
                ngModelCtrl.$formatters.push(formatViewValue);

                var minValidator = function (value) {
                    if (!ngModelCtrl.$isEmpty(value) && value < min) {
                        ngModelCtrl.$setValidity('min', false);
                        return undefined;
                    } else {
                        ngModelCtrl.$setValidity('min', true);
                        return value;
                    }
                };
                ngModelCtrl.$parsers.push(minValidator);
                ngModelCtrl.$formatters.push(minValidator);

                if (attrs.max) {
                    var max = parseFloat(attrs.max);
                    var maxValidator = function (value) {
                        if (!ngModelCtrl.$isEmpty(value) && value > max) {
                            ngModelCtrl.$setValidity('max', false);
                            return undefined;
                        } else {
                            ngModelCtrl.$setValidity('max', true);
                            return value;
                        }
                    };

                    ngModelCtrl.$parsers.push(maxValidator);
                    ngModelCtrl.$formatters.push(maxValidator);
                }

                // Round off
                if (precision > -1) {
                    ngModelCtrl.$parsers.push(function (value) {
                        return value ? round(value) : value;
                    });
                    ngModelCtrl.$formatters.push(function (value) {
                        return value ? formatPrecision(value) : value;
                    });
                }

                el.bind('blur', function () {
                    var value = ngModelCtrl.$modelValue;
                    if (value) {
                        ngModelCtrl.$viewValue = formatPrecision(value);
                        ngModelCtrl.$render();
                    }
                });
            }

            return {
                restrict: 'A',
                require: 'ngModel',
                link: link
            };
        })

        .directive('focusHere', ['$timeout', function ($timeout) {
            return {
                link: function (scope, element, attrs) {
                    $timeout(function () {
                        element[0].focus();
                    });
                }
            };
        }])

        .directive("checkbox", function () {
            return {
                scope: {},
                require: "ngModel",
                restrict: "E",
                replace: "true",
                template: "<button type=\"button\" ng-style=\"stylebtn\" class=\"btn btn-default\" ng-class=\"{'btn-xs': size==='default', 'btn-sm': size==='large', 'btn-lg': size==='largest', 'checked': checked===true}\">" +
                "<span ng-style=\"styleicon\" class=\"glyphicon\" ng-class=\"{'glyphicon-ok': checked===true}\"></span>" +
                "</button>",
                link: function (scope, elem, attrs, modelCtrl) {
                    scope.size = "default";
                    // Default Button Styling
                    scope.stylebtn = {};
                    // Default Checkmark Styling
                    scope.styleicon = {"width": "10px", "left": "-1px"};
                    // If size is undefined, Checkbox has normal size (Bootstrap 'xs')
                    if (attrs.large !== undefined) {
                        scope.size = "large";
                        scope.stylebtn = {"padding-top": "2px", "padding-bottom": "2px", "height": "30px"};
                        scope.styleicon = {"width": "8px", "left": "-5px", "font-size": "17px"};
                    }
                    if (attrs.larger !== undefined) {
                        scope.size = "larger";
                        scope.stylebtn = {"padding-top": "2px", "padding-bottom": "2px", "height": "34px"};
                        scope.styleicon = {"width": "8px", "left": "-8px", "font-size": "22px"};
                    }
                    if (attrs.largest !== undefined) {
                        scope.size = "largest";
                        scope.stylebtn = {"padding-top": "2px", "padding-bottom": "2px", "height": "45px"};
                        scope.styleicon = {"width": "11px", "left": "-11px", "font-size": "30px"};
                    }

                    var trueValue = true;
                    var falseValue = false;

                    // If defined set true value
                    if (attrs.ngTrueValue !== undefined) {
                        trueValue = attrs.ngTrueValue;
                    }
                    // If defined set false value
                    if (attrs.ngFalseValue !== undefined) {
                        falseValue = attrs.ngFalseValue;
                    }

                    // Check if name attribute is set and if so add it to the DOM element
                    if (scope.name !== undefined) {
                        elem.name = scope.name;
                    }

                    // Update element when model changes
                    scope.$watch(function () {
                        if (modelCtrl.$modelValue === trueValue || modelCtrl.$modelValue === true) {
                            modelCtrl.$setViewValue(trueValue);
                        } else {
                            modelCtrl.$setViewValue(falseValue);
                        }
                        return modelCtrl.$modelValue;
                    }, function (newVal, oldVal) {
                        scope.checked = modelCtrl.$modelValue === trueValue;
                    }, true);

                    // On click swap value and trigger onChange function
                    elem.bind("click", function () {
                        scope.$apply(function () {
                            if (modelCtrl.$modelValue === falseValue) {
                                modelCtrl.$setViewValue(trueValue);
                            } else {
                                modelCtrl.$setViewValue(falseValue);
                            }
                        });
                    });
                }
            };
        });
});