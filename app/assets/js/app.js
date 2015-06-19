'use strict';

//requirejs.config({
//    paths: {
//        "jquery.scrollTo": webjars.path("jquery.scrollTo", "jquery.scrollTo")
//    },
//    shim: {
//        "jquery.scrollTo": []
//    }
//});


require([
    'jquery',
    'angular',
    'angular-route',
    'angular-animate',
    'angular-scroll',
    'ui-bootstrap-tpls',
    'angular-tags-input',
    'angular-ui-select',

    'common',
    'main',
    'directives',
    'localize',
    'nav',
    'products',
    'task/task'
], function ($) {
    angular.module('app',
        [
            'ngRoute',
            'ui.bootstrap',
            'duScroll',

            'app.controllers',
            'app.directives',
            'app.localization',
            'app.nav',
            'app.task',
            'app.products'
        ]
    )
        .config(['$routeProvider', function ($routeProvider) {

            var routes = [
                'sell',
                'products', 'products/stock-control', 'ui/icons', 'ui/grids', 'ui/widgets', 'ui/components', 'ui/timeline', 'ui/nested-lists', 'ui/pricing-tables', 'ui/maps',
                'tables/static', 'tables/dynamic', 'tables/responsive',
                'forms/elements', 'forms/layouts', 'forms/validation', 'forms/wizard',
                'charts/charts', 'charts/flot', 'charts/morris',
                'pages/404', 'pages/500', 'pages/blank', 'pages/forgot-password', 'pages/invoice', 'pages/lock-screen', 'pages/profile', 'pages/signin', 'pages/signup',
                'mail/compose', 'mail/inbox', 'mail/single',
                'tasks/tasks'
            ];

            function setRoutes(route) {
                var url = '/' + route;
                var config = {templateUrl: 'views/' + route};

                $routeProvider.when(url, config);
                return $routeProvider;
            }

            //routes.forEach(function (route) {
            //    setRoutes(route)
            //});

            $routeProvider
                .when('/', {redirectTo: '/sell'})
                .when('/sell', {templateUrl: 'sell'})
                .when('/products', {templateUrl: 'products', controller: 'ProductsCtrl'})
                .when('/product/new', {templateUrl: 'product/new', controller: 'ProductsCtrl'})
                .when('/404', {templateUrl: 'views/pages/404.html'});
            //.otherwise({redirectTo: '/404'})
        }]);

    $(document).ready(function () {
        angular.bootstrap(document.body, ['app']);
    });
});