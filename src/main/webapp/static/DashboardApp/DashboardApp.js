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
        console.log(currentUser);
        angular.bootstrap(document, ['DashboardApp']);
    });
});

var dashboardApp = angular.module('DashboardApp', ['ngRoute', 'ui.bootstrap', 'spring-security-csrf-token-interceptor'])
    .run(function(currentUserFactory) {
        currentUserFactory.setCurrentUser(currentUser);
    })
    .config(function ($routeProvider) {
        $routeProvider
            .when('/',
            {
                controller: 'DashboardController',
                templateUrl: '/static/DashboardApp/partials/home.partial.html'
            }
        )
            .when('/admin',
            {
                controller: 'AdminController',
                templateUrl: '/static/DashboardApp/partials/admin.partial.html',
                hasRole: 'administrator'
            }
        )
            .otherwise(
            {
                redirectTo: '/'
            }
        );
    })
    .config(function($provide, $httpProvider) {
        $httpProvider.interceptors.push('httpSecurityInterceptor');
    })
    .factory('httpSecurityInterceptor', function($q, $rootScope) {
        return {
            response: function(response) {
                return response || $q.when(response);
            },
            responseError: function(response) {
                if (response.status === 401) {
                    $.post('/logout');
                    window.location = "/";
                }
                if (response.status === 403) {
                    $rootScope.$broadcast('operationNotAllowedEvent');
                }

                return $q.reject(response);
            }
        };
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
                        if (currentUser.roles[i].toLowerCase() === requiredRole) {
                            hasRole = true;
                            break;
                        }
                    }
                    return hasRole;
                };
            },
            getCurrentUser: function() {
                return currentUser;
            }
        };
    })
    .factory('eventMessageFactory', function() {
        var messages = {
            operationNotAllowedEvent: 'You are not allowed to perform that operation.'
        };

        return {
            getMessageForEvent: function(event) {
                return messages[event.name];
            }
        };
    });
