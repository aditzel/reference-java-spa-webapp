loginApp.controller("LoginController", function($scope, $http) {
    $scope.formData = {};

    $scope.login = function() {
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
                });
        });
    }
});