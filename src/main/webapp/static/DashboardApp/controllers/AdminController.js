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

dashboardApp.controller("AdminController", function($scope, $http) {
    $scope.allUsers = {};

    $scope.getAllUsers = function() {
        // temporarily specifying full url until this is abstracted into a system config factory
        $http({method: 'GET', url: 'https://localhost:8443/api/user'}).success(function(result) {
            $scope.allUsers = result;
        }).error(function() {
            console.log("Something went wrong", arguments);
        });
    };

    $scope.getAllUsers();
});