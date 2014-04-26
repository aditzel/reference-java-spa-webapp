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

var dashboardApp = angular.module('DashboardApp', ['ngRoute'])

    .config(function ($routeProvider) {
        $routeProvider
            .when('/',
            {
                controller: 'DashboardController',
                templateUrl: '/assets/js/DashboardApp/partials/index.html'
            }
        )
            .when('/admin',
            {
                controller: 'AdminController',
                templateUrl: '/assets/js/DashboardApp/partials/admin.html'
            }
        )
            .otherwise(
            {
                redirectTo: '/'
            }
        );
    })
    .factory('currentUserFactory', function ($q, $http) {

        var factory = {};

        factory.getCurrentUser = function() {
            var deferred = $q.defer();

            $http.get('/api/user/current')
                .then(function (response) {
                    deferred.resolve(response);
                });

            return deferred.promise;
        };

        return factory;
    });

