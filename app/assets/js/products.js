'use strict';

require(['jquery', 'angular'], function ($, angular) {
    angular.module('app.products', ['ngTagsInput', 'ui.select', 'app.common'])
        //.config(['$routeProvider', function ($routeProvider) {
        //
        //}])
        .controller('ProductsCtrl', ['$scope', '$location', function ($scope, $location) {
            $scope.goToAddProduct = function(){
                $location.path( '/product/new' );
            };
        }]);
});
