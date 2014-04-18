var dashboardApp = angular.module('DashboardApp', ['ngRoute']);

dashboardApp.config(function($routeProvider) {
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
});

