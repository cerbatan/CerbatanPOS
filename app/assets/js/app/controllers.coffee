define ['jquery', './module'],
  ($, app) ->
    AppCtrl = ($scope, $rootScope, $route, $document) ->
      $window = $(window)

      $scope.main =
        brand: 'Cerbatán POS'
        name: 'Lisa Doe'

      $scope.admin =
        layout: 'wide'
        menu: 'vertical'
        fixedHeader: true
        fixedSidebar: true

      $scope.$watch 'admin',
        (newVal, oldVal) ->
          if newVal.menu == 'horizontal' and oldVal.menu == 'vertical'
            $rootScope.$broadcast 'nav:reset'
            return
          if newVal.fixedHeader == false and newVal.fixedSidebar == true
            if oldVal.fixedHeader == false and oldVal.fixedSidebar == false
              $scope.admin.fixedHeader = true
              $scope.admin.fixedSidebar = true
            if oldVal.fixedHeader == true and oldVal.fixedSidebar == true
              $scope.admin.fixedHeader = false
              $scope.admin.fixedSidebar = false
            return
          if newVal.fixedSidebar == true
            $scope.admin.fixedHeader = true
          if newVal.fixedHeader == false
            $scope.admin.fixedSidebar = false
          return
        , true

      $scope.color =
        primary: '#1BB7A0'
        success: '#94B758'
        info: '#56BDF1'
        infoAlt: '#7F6EC7'
        warning: '#F3C536'
        danger: '#FA7B58'

      $rootScope.$on '$routeChangeSuccess',
        (event, currentRoute, previousRoute) ->
          $document.scrollTo 0, 0

    AppCtrl
      .$inject = ['$scope', '$rootScope', '$route', '$document']
    app.controller 'AppCtrl', AppCtrl


    HeaderCtrl = ->

    app.controller 'HeaderCtrl', HeaderCtrl


    NavCtrl = ($scope, taskStorage, filterFilter) ->
      tasks = $scope.tasks = taskStorage.get()
      $scope.taskRemainingCount = filterFilter(tasks, completed: false).length
      $scope.$on 'taskRemaining:changed',
        (event, count) ->
          $scope.taskRemainingCount = count

    NavCtrl
      .$inject = ['$scope', 'taskStorage', 'filterFilter']
    app.controller 'NavCtrl', NavCtrl


    DashboardCtrl = ->

    app.controller 'DashboardCtrl', DashboardCtrl
