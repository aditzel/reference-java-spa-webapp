module.exports = function (grunt) {

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        gruntMavenProperties: grunt.file.readJSON('grunt-maven.json'),
        mavenPrepare: {
            options: {
                resources: ['**']
            },
            dev: {}
        },
        mavenDist: {
            options: {
                warName: '<%= gruntMavenProperties.warName %>',
                deliverables: ['**', '!non-deliverable.js'],
                gruntDistDir: 'dist'
            },
            dev: {}
        },
        watch: {
            files: ["<%= gruntMavenProperties.filesToWatch %>"],
            tasks: ['default']
        },
        jshint: {
            files: ["LoginApp/*.js", "DashboardApp/*.js"]
        }
    });

    grunt.loadNpmTasks('grunt-maven');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-jshint');

    grunt.registerTask('default', ['mavenPrepare', 'jshint', 'mavenDist']);

};