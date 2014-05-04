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

loginApp.controller("LoginController", function($scope, $http) {
    $scope.formData = {};
    $scope.processingLogin = false;

    $scope.login = function() {
        $scope.processingLogin = true;
            $http.head('/')
            .success(function(data, status, headers, config) {

                var csrfToken =  headers("X-CSRF-TOKEN");

                $http.post('/index.html', 'username='+$scope.formData.username+'&password='+$scope.formData.password, {
                    headers: {
                        'X-CSRF-TOKEN': csrfToken,
                        'X-REQUESTED-WITH': 'XMLHttpRequest',
                        'CONTENT-TYPE': 'application/x-www-form-urlencoded'
                    }
                })
                .success(function (data, status) {
                    window.location.href = '/home.html';
                })
                .error(function (data, status) {
                    if (status == 401) {
                        $scope.errorMessage = "Incorrect username or password.";
                    } else {
                        $scope.errorMessage = "Error attempting to log in. Please try again later.";
                    }
                        $scope.processingLogin = false;
                    });
        });
    }
});