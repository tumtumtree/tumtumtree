angular.module('mc.core.ui.simpleObjectEditor', []).directive 'simpleObjectEditor',  [-> {
    restrict: 'E'
    replace: true
    scope:
      object:             '='
      hints:              '=?'
      title:              '@?'
      valueTitle:         '@?'
      keyPlaceholder:     '@?'
      valuePlaceholder:   '@?'
    templateUrl: 'modelcatalogue/core/ui/simpleObjectEditor.html'

    controller: ['$scope', ($scope) ->
      # default values
      $scope.editableProperties = []

      $scope.lastAddedRow = 0

      $scope.removeProperty = (index) ->
          current = $scope.editableProperties[index]
          unique  = $scope.isKeyUnique(current.key)
          $scope.editableProperties.splice(index, 1)

          if unique
            delete $scope.object[current.key]
          else
            for property in $scope.editableProperties
              if property.key == current.key
                $scope.object[property.key] = property.value
          if $scope.editableProperties.length == 0
            $scope.editableProperties.push key: ''

      $scope.addProperty = (index, property = {key: '', value: ''}) ->
        newProperty = angular.copy(property)
        delete newProperty.originalKey
        newIndex = index + 1
        $scope.lastAddedRow = newIndex
        $scope.editableProperties.splice(newIndex, 0, newProperty)

      $scope.keyChanged = (property) ->
        delete $scope.object[property.originalKey]
        return if not property.key
        property.originalKey = property.key
        $scope.object[property.key] = if property.value then property.value else null

      $scope.valueChanged = (property) ->
        return if not property.key
        $scope.object[property.key] = if property.value then property.value else null

      $scope.isKeyUnique = (key) ->
        return false if not key and $scope.editableProperties.length > 1
        firstFound = false
        for property in $scope.editableProperties
          if property.key == key
            return false if firstFound
            firstFound = true
        return true

      remove = (arr, item) ->
        arr.splice($.inArray(item, arr), 1 )

      onObjectOrHintsChanged = (object, hints) ->
        editableProperties = []
        currentHints = angular.copy(hints ? [])

        for key, value of object when key and !(angular.isFunction(value) or angular.isObject(value))
          editableProperties.push key: key, value: value, originalKey: key
          remove currentHints, key

        for hint in currentHints by -1
          editableProperties.unshift key: hint

        editableProperties.push key: '' if editableProperties.length == 0 and (not hints or hints.length == 0)

        $scope.editableProperties = editableProperties


      onObjectOrHintsChanged($scope.object, $scope.hints ? [])

      $scope.addNewRowOnTab = ($event, index, last)->
        $scope.addProperty(index, {key: '', value: ''}) if $event.keyCode == 9 and last

      $scope.$watch 'object', (newObject) ->
        onObjectOrHintsChanged(newObject, $scope.hints ? [])

      $scope.$watch 'hints', (newHints) ->
        onObjectOrHintsChanged($scope.object, newHints)

    ]

  }
]