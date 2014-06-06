/*
 * Copyright 2014 Allan Ditzel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

dashboardApp.controller("DashboardController", function($scope, $location, currentUserFactory, eventMessageFactory, $http) {
    $scope.currentUser = currentUserFactory.getCurrentUser();
    $scope.alertMessages = [];

    $scope.$on('$routeChangeStart', function(scope, next, current) {
        var requiredRole = next.$$route.hasRole;
        if (requiredRole && !$scope.currentUser.hasRole(requiredRole)) {
            $location.path('/');
        }
    });

    $scope.$on('operationNotAllowedEvent', function(event) {
        var message = eventMessageFactory.getMessageForEvent(event);
        var hasValue = false;
        if (message) {
            for (var i = 0; i < $scope.alertMessages.length; i++) {
                if ($scope.alertMessages[i] === message) {
                    hasValue = true;
                    break;
                }
            }
            if (!hasValue) {
                $scope.alertMessage = $scope.alertMessages.push(message);
            }
        }
    });

    $scope.logout = function() {
        $http({method: 'POST', url: '/logout'}).
            success(function(data, status, headers, config) {
                window.location = "/index.html";
            }).
            error(function(data, status, headers, config) {
                window.location = "/index.html";
            });
    };

    $scope.closeAlert = function(index) {
        $scope.alertMessages.splice(index, 1);
    };

    $scope.test403Response = function() {
        $http.get("/api/test/generate403");
    };

    $scope.test401Response = function() {
        $http.get("/api/test/generate401");
    };
});