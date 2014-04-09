loginApp.controller("LoginController", function($scope, $http) {
    $scope.formData = {};

    $scope.login = function() {
        $http.head('/')
            .success(function(data, status, headers, config) {

                var csrfToken =  headers("X-CSRF-TOKEN");

                $http.post('/index.html', {
                    'username': $scope.formData.username,
                    'password': $scope.formData.password
                }, {
                    headers: {
                        'X-CSRF-TOKEN': csrfToken,
                        'X-REQUESTED-WITH': 'XMLHttpRequest'
                    }
                })
                    .success(function (data, status) {
                        console.log("Success!!!", data, status);
                    })
                    .error(function (data, status) {
                        console.log("Failure!!!!", data, status);
                    });
        });
    }

    $scope.init = function($http) {
        console.log("Login controller init", arguments);

        $.ajax({
            type: 'HEAD',
            url: '/',
            complete: function (xhr, status) {
                if (xhr) {
                    $scope.formData._csrf = xhr.getResponseHeader("X-CSRF-TOKEN");
                }
            }
        })

    }
});