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

dashboardApp.controller("DashboardController", function($scope, $location, currentUserFactory, eventMessageFactory) {
    $scope.currentUser = currentUserFactory.getCurrentUser();
    $scope.alertMessage = null;

    $scope.$on('$routeChangeStart', function(scope, next, current) {
        var requiredRole = next.$$route.hasRole;
        if (requiredRole && !$scope.currentUser.hasRole(requiredRole)) {
            $location.path('/');
        }
    });

    $scope.$on('operationNotAllowedEvent', function(event) {
        $scope.alertMessage = eventMessageFactory.getMessageForEvent(event) || null;
    });
});