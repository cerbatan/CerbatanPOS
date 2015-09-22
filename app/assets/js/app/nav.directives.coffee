define ['jquery', './module'],
  ($, module) ->
    (->
      # swtich for mini style NAV, realted to 'collapseNav' directive
      ToggleNavCollapsedMinDirective = ($rootScope) ->
        restrict: 'A'
        link: (scope, ele, attrs) ->
          app = $('#app')

          ClickHandler = (e) ->
            if app.hasClass('nav-collapsed-min')
              app.removeClass('nav-collapsed-min')
            else
              app.addClass('nav-collapsed-min')
              $rootScope.$broadcast('nav:reset')

            e.preventDefault()

          ele.on 'click', ClickHandler

      module.directive 'toggleNavCollapsedMin', ['$rootScope', ToggleNavCollapsedMinDirective]

    )()

    (->
      # for accordion/collapse style NAV
      ColapseNavDirective = ->
        LinkFunction = (scope, ele, attrs) ->
          $ele = $(ele)
          $window = $(window)
          $lists = $ele.find('ul').parent('li') # only target li that has sub ul
          $lists.append('<i class="fa fa-caret-down icon-has-ul-h"></i><i class="fa fa-caret-right icon-has-ul"></i>')
          $a = $lists.children('a')
          $listsRest = $ele.children('li').not($lists)
          $aRest = $listsRest.children('a')

          $app = $('#app')
          $nav = $('#nav-container')

          $a.on 'click',
            (event) ->
              # disable click event when Nav is mini style || DESKTOP horizontal nav
              if ( $app.hasClass('nav-collapsed-min') || ($nav.hasClass('nav-horizontal') && $window.width() >= 768) ) then return false

              $this = $(this)
              $parent = $this.parent('li')
              $lists.not( $parent ).removeClass('open').find('ul').slideUp()
              $parent.toggleClass('open').find('ul').slideToggle()

              event.preventDefault()

          $aRest.on 'click',
            (event) ->
              $lists.removeClass('open').find('ul').slideUp()


          # reset NAV, sub Ul should slideUp
          scope.$on 'nav:reset',
            (event) ->
              $lists.removeClass('open').find('ul').slideUp()


          # removeClass('nav-collapsed-min') when size < $screen-sm
          # reset Nav when go from mobile to horizontal Nav
          Timer = undefined
          prevWidth = $window.width()
          updateClass = ->
            currentWidth = $window.width()
            # console.log('prevWidth: ' + prevWidth)
            # console.log('currentWidth: ' + currentWidth)
            if currentWidth < 768 then $app.removeClass('nav-collapsed-min')
            if prevWidth < 768 && currentWidth >= 768 && $nav.hasClass('nav-horizontal')
              # reset NAV, sub Ul should slideUp
              $lists.removeClass('open').find('ul').slideUp()

            prevWidth = currentWidth


          $window.resize () ->
            clearTimeout(t)
            t = setTimeout(updateClass, 300)

        directive =
          restrict: 'A'
          link: LinkFunction

      module.directive 'collapseNav', ColapseNavDirective
    )()

    (->
      # Add 'active' class to li based on url, muli-level supported, jquery free
      HighlightActiveDirective = ->
        Controller = ($scope, $element, $attrs, $location) ->
          path = ->
            $location.path()

          links = $element.find('a')
          highlightActive = (links, path) ->
            path = '#' + path

            angular.forEach(links, (link) ->
              $link = angular.element(link)
              $li = $link.parent('li')
              href = $link.attr('href')

              if ($li.hasClass('active'))
                $li.removeClass('active')
              if path.indexOf(href) is 0
                $li.addClass('active')
            )

          highlightActive(links, $location.path())

          $scope.$watch(path, (newVal, oldVal) ->
            if newVal is oldVal
              return
            highlightActive(links, $location.path())
          )

        Controller.$inject = ['$scope', '$element', '$attrs', '$location']

        directive =
          restrict: "A"
          controller: Controller

      module.directive 'highlightActive', HighlightActiveDirective

    )()

    (->
      # toggle on-canvas for small screen, with CSS
      ToggleOffCanvasDirective = ->
        restrict: 'A'
        link: (scope, ele, attrs) ->
          ele.on 'click', ->
            $('#app').toggleClass('on-canvas')

      module.directive 'toggleOffCanvas', ToggleOffCanvasDirective
    )()
