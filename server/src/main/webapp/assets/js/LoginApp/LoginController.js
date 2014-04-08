loginApp.controller("LoginController", function($scope, $http) {
    $scope.formData = {};

    $scope.login = function() {
        var formKeyVals = "_csrf=" + $scope.formData["_csrf"] + "&" +
                          "username=" + $scope.formData["username"] + "&" +
                          "password=" + $scope.formData["password"] + "\n";
        console.log(formKeyVals);
        $http.post('index.html', formKeyVals);
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