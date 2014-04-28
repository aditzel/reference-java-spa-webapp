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

/**
 * We fetch the current user from the application prior to the angular app starting up. Then we manually bootstrap
 * the application.
 */
var currentUser;
angular.element(document).ready(function() {
    $.get('/api/user/current', function(data) {
        currentUser = data;
        angular.bootstrap(document, ['DashboardApp']);
    });
});

var dashboardApp = angular.module('DashboardApp', ['ngRoute'])
    .run(function(currentUserFactory) {
        currentUserFactory.setCurrentUser(currentUser);
    })
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
                templateUrl: '/assets/js/DashboardApp/partials/admin.html',
                hasRole: 'administrator'
            }
        )
            .otherwise(
            {
                redirectTo: '/'
            }
        );
    })
    .factory('currentUserFactory', function () {
        var currentUser;

        return {
            setCurrentUser: function(user) {
                currentUser = user;
                // add functionality we'll use throughout the app
                currentUser.hasRole = function(requiredRole) {
                    var hasRole = false;

                    for (var i = 0 ; i < currentUser.roles.length; i++) {
                        if (currentUser.roles[i] === requiredRole) {
                            hasRole = true;
                            break;
                        }
                    }
                    return hasRole;
                }
            },
            getCurrentUser: function() {
                return currentUser;
            }
        }
    });

