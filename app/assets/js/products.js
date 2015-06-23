'use strict';

require(['jquery', 'angular'], function ($, angular) {
    function Brand(name) {
        this.id = null;
        this.name = name;
    }

    function Tag(name) {
        this.id;
        this.name = name;
    }

    function Tax(name, percentage) {
        this.id = null;
        this.name = name;
        this.percentage = percentage;
    }

    function Fraction(name, sku){
        this.id = null;
        this.name = name;
        this.sku = sku;
    }

    function Product() {
        this.id = null;
        this.sku = null;
        this.name = null;
        this.brand = new Brand();
        this.tags = [];
        this.cost = null;
        this.markup = null;
        this.price = null;
        this.tax = new Tax();
        this.retailPrice = null;
        this.trackStock = true;
        this.stockCount = null;
        this.alertStockLowLevel = false;
        this.stockAlertLevel = null;
        this.fractions = [];
    }

    angular.module('app.products', ['ngSanitize', 'ngTagsInput', 'ui.select', 'play.routing', 'app.common'])
        //.config(['$routeProvider', function ($routeProvider) {
        //
        //}])
        .factory('ProductsSrv', function () {

            return {
                getBrands : function(filter){

                }
            };
        })
        .controller('ProductsCtrl', ['$scope', '$location', function ($scope, $location) {
            $scope.goToAddProduct = function () {
                $location.path('/product/new');
            };
        }])
        .controller('NewProductCtrl', ['$scope', 'playRoutes', function($scope, routes){
            $scope.product = new Product();

            $scope.refreshBrands = function(filter){
              routes.controllers.products.Products.brands(filter).get().success(function(brands){
                $scope.brands = brands;
              })
            };
        }]);
});
