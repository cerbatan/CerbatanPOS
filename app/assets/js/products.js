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

    function Fraction(name, qty, price, sku) {
        this.id = null;
        this.qty = qty;
        this.name = name;
        this.price = price;
        this.sku = sku;
    }

    function Product() {
        this.id = null;
        this.sku = null;
        this.name = null;
        this.brand = null;

        /**
         * @type {Tag[]}
         */
        this.tags = [];
        this.cost = 0;
        this.markup = 0;
        this.price = 0;
        this.tax = null;
        this.retailPrice = 0;
        this.trackStock = true;
        this.stockCount = 0;
        this.alertStockLowLevel = false;
        this.stockAlertLevel = 0;

        /**
         * @type {Fraction[]}
         */
        this.fractions = [];
    }

    angular.module('app.products', ['ngSanitize', 'ngTagsInput', 'ui.select', 'play.routing', 'app.common'])
        .factory('ProductsSrv', function () {

            return {
                getBrands: function (filter) {

                }
            };
        })

        .controller('ProductsCtrl', ['$scope', '$location', function ($scope, $location) {
            $scope.goToAddProduct = function () {
                $location.path('/product/new');
            };
        }])

        .controller('NewProductCtrl', ['$scope', '$filter', '$cacheFactory', '$log', '$modal', 'playRoutes', 'localize', function ($scope, $filter, $cacheFactory, $log, $modal, routes, localize) {
            function showAddBrandDialog(brandName, showInput) {
                var modalInstance = $modal.open({
                    templateUrl: "addBrandModal.html",
                    controller: 'NewBrandModalCtrl',
                    resolve: {
                        newBrandName: function () {
                            return brandName;
                        },
                        showInput: function () {
                            return showInput === true;
                        }
                    }
                });

                return modalInstance;
            }

            function showAddTaxDialog() {
                return $modal.open({
                    templateUrl: "addTaxModal.html",
                    controller: 'AddTaxModalCtrl'
                });
            }

            $scope.product = new Product();

            var lastSearch = " ";
            $scope.brands = [];
            $scope.tags = [];
            getTaxes();

            $scope.refreshBrands = function (filter) {
                if ((lastSearch.length === 0 && filter.length > 0) || filter.toLowerCase().indexOf(lastSearch.toLocaleLowerCase()) !== 0) {
                    lastSearch = filter;
                    routes.controllers.products.Products.brands(filter).get().success(function (brands) {
                        if (brands.length === 0) {
                            brands.push(new Brand(filter));
                        }

                        $scope.brands = brands;
                    })
                } else if (this.$select.items.length === 0 || this.$select.items[0].id == null) {
                    if (this.$select.items.length > 1 && this.$select.items[0].id == null) {
                        $scope.brands.shift();
                    } else if ($scope.brands.length > 0 && $scope.brands[0].id == null)
                        $scope.brands[0].name = filter;
                    else
                        $scope.brands.unshift(new Brand(filter));
                }
            };

            $scope.brandSelected = function (select) {
                if (select.search) {
                    if (!select.selected || select.selected.id == null || select.selected.name.toLowerCase().indexOf(select.search.toLocaleLowerCase()) === -1) {
                        var newBrandName = select.search;
                        showAddBrandDialog(newBrandName).result.then(
                            function () {
                                $log.info("Adding Brand");
                                routes.controllers.products.Products.addBrand().put({name: newBrandName})
                                    .success(function (brand) {
                                        $scope.product.brand = brand;
                                    })
                                    .error(function () {
                                        $log.warning("Failed brand creation");
                                    });
                            },
                            function () {
                                select.selected = null;
                            }
                        );
                    }
                }

                select.search = '';
            };

            $scope.addBrand = function () {
                showAddBrandDialog("", true).result.then(
                    function (newBrandName) {
                        $log.info("Adding Brand");
                        routes.controllers.products.Products.addBrand().put({name: newBrandName})
                            .success(function (brand) {
                                $scope.product.brand = brand;
                            })
                            .error(function () {
                                $log.warning("Failed brand creation");
                            });
                    },
                    function () {

                    }
                );
            };

            $scope.loadTags = function ($query) {
                return routes.controllers.products.Products.tags().get({cache: true}).then(function (response) {
                    return $filter('filter')(response.data, $query);
                });
            };

            $scope.handleAddingTag = function ($tag) {
                if ($tag.id == undefined || $tag.id == null) {
                    $log.info("Adding Tag");
                    routes.controllers.products.Products.addTag().put({name: $tag.name})
                        .success(function (tag) {
                            $scope.product.tags.pop();
                            $scope.product.tags.push(tag);
                            $cacheFactory.get('$http').remove(routes.controllers.products.Products.tags().url);
                        })
                        .error(function () {
                            $scope.product.tags.pop();
                            $log.warning("Failed tag creation");
                        });
                }
            };

            var priceModifiedInternally = false;
            var retailPriceModifiedInternally = false;
            var markupModifiedInternally = false;

            function updatePrice() {
                priceModifiedInternally = true;
                $scope.product.price = $scope.product.cost * (1.0 + $scope.product.markup / 100.0);
            }

            function updateRetailPrice() {
                if ($scope.product.tax != null) {
                    $scope.product.retailPrice = $scope.product.price * (1.0 + $scope.product.tax.percentage / 100.0);
                } else {
                    $scope.product.retailPrice = $scope.product.price;
                }

                retailPriceModifiedInternally = true;
            }

            $scope.$watch('product.cost', function () {
                updatePrice();
                updateRetailPrice();
            });


            $scope.$watch('product.markup', function () {
                if (!markupModifiedInternally) {
                    updatePrice();
                    updateRetailPrice();
                }

                markupModifiedInternally = false;
            });

            $scope.$watch('product.price', function () {
                if (!priceModifiedInternally) {
                    markupModifiedInternally = true;
                    $scope.product.markup = ($scope.product.price / $scope.product.cost - 1.0) * 100.0;
                    updateRetailPrice();
                }

                priceModifiedInternally = false;
            });

            $scope.$watch('product.tax', function () {
                updateRetailPrice();
            });

            $scope.$watch('product.retailPrice', function () {
                if (!retailPriceModifiedInternally) {
                    priceModifiedInternally = true;
                    markupModifiedInternally = true;

                    if ($scope.product.tax != null) {
                        $scope.product.price = $scope.product.retailPrice / (1.0 + $scope.product.tax.percentage / 100.0);
                    }else{
                        $scope.product.price = $scope.product.retailPrice;
                    }

                    $scope.product.markup = ($scope.product.price / $scope.product.cost - 1.0) * 100.0;
                }


                if ( $scope.product.fractions.length > 0 ){
                    $scope.product.fractions[0].price = $scope.product.retailPrice;
                }

                retailPriceModifiedInternally = false;
            });

            $scope.$watch('product.sku', function () {
                if ( $scope.product.fractions.length > 0 ){
                    $scope.product.fractions[0].sku = $scope.product.sku;
                }
            });

            function getTaxes() {
                routes.controllers.products.Products.taxes().get().success(function (taxes) {
                    $scope.taxes = taxes;
                    $scope.taxes.push(new Tax("--- " + localize.getLocalizedString("Add") + " ---", 0.0));
                });
            }

            $scope.taxSelected = function (select) {
                if (select.selected.id == null) {
                    showAddTaxDialog().result.then(
                        function (newTax) {
                            $log.info("Adding Tax");
                            routes.controllers.products.Products.addTax().put(newTax)
                                .success(function (tax) {
                                    $scope.product.tax = tax;
                                    getTaxes();
                                })
                                .error(function () {
                                    $log.warning("Failed tax creation");
                                });
                        },
                        function () {
                            select.selected = null;
                        }
                    );
                }
            };

            $scope.addFraction = function(){
                if ( $scope.product.fractions.length == 0 ){
                    $scope.product.fractions.push(new Fraction("Regular", 1, $scope.product.retailPrice, $scope.product.sku));
                }

                $scope.product.fractions.push(new Fraction(null, null, null));
            };

            $scope.removeFraction =  function(index){
              if ( index == 1 &&  $scope.product.fractions.length <= 2 ){
                  $scope.product.fractions = [];
              } else {
                  $scope.product.fractions.splice(index, 1);
              }
            };

        }])

        .controller('NewBrandModalCtrl', ['$scope', '$log', '$modalInstance', 'newBrandName', 'showInput', function ($scope, $log, $modalInstance, newBrandName, showInput) {
            $scope.newBrandName = newBrandName;
            $scope.showInput = showInput;
            $scope.disableSave = showInput;
            $scope.inputChange = function () {
                $scope.disableSave = ($scope.newBrandName.length <= 1);
            };
            $scope.ok = function () {
                $modalInstance.close($scope.newBrandName);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };
        }])

        .controller('AddTaxModalCtrl', ['$scope', '$log', '$modalInstance', function ($scope, $log, $modalInstance) {
            $scope.newTax = new Tax("", 0.0);
            $scope.disableSave = true;
            $scope.inputChange = function () {
                $scope.disableSave = ($scope.newTax.name.length <= 1);
            };
            $scope.ok = function () {
                $modalInstance.close($scope.newTax);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss();
            };
        }]);
});
