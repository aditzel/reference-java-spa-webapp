loginApp.controller("LoginController", function($scope, $http) {
    $scope.formData = {};

    $scope.login = function() {
        $http.post('index.html', $scope.formData);
    }

    $scope.init = function() {
        console.log("Login controller init");

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