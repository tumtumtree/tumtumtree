angular.module('mc.util.ui.bs.actionButtonDropdown', ['mc.util.ui.actionButtonDropdown']).run [ '$templateCache', ($templateCache) ->
  $templateCache.put 'modelcatalogue/util/ui/actionButtonDropdown.html', '''
      <span class="btn-group" dropdown ng-class="{'pull-right': action.expandToLeft}">
          <button id="{{action.id}}Btn"         ng-if="!action.abstract" ng-disabled="action.disabled || action.children.length == 0" title="{{action.label}}" type="button" class="btn" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size" ng-click="action.run()"><span ng-show="action.icon" ng-class="action.icon"></span><span ng-hide="iconOnly"> {{action.label}}</span></button>
          <button id="{{action.id}}BtnDropdown" ng-if="!action.abstract" ng-disabled="action.disabled || action.children.length == 0" title="{{action.label}}" type="button" class="btn dropdown-toggle" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size">
            <span class="caret"></span>
            <span class="sr-only">Split button!</span>
          </button>
          <button id="{{action.id}}Btn" ng-if="action.abstract" ng-disabled="action.disabled || action.children.length == 0" type="button" class="btn dropdown-toggle" ng-class="'btn-' + (noColors ? 'default' : action.type) + ' btn-' + size">
            <span ng-show="action.icon" ng-class="action.icon"></span></span><span ng-hide="iconOnly"> {{action.label}}</span> <span class="caret"></span>
          </button>

          <ul id="{{action.id}}BtnItems" class="dropdown-menu" role="menu">
            <li ng-repeat="childAction in action.children track by $index" ng-class="{'dropdown-header': childAction.heading, 'active': childAction.active, 'disabled': childAction.disabled}">
              <a id="{{childAction.id}}Btn" ng-hide="childAction.heading" ng-click="childAction.run()" ng-class="{'disabled': childAction.disabled}"><span ng-show="childAction.icon" ng-class="childAction.icon"></span></span><span> {{childAction.label}}</span></a>
              <span ng-show="childAction.heading">{{childAction.label}}</span>
            </li>
          </ul>
      </span>
    '''
]