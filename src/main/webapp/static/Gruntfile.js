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
        },
        concat: {
            options: {
                // define a string to put between each file in the concatenated output
                separator: ';'
            },
            dist: {
                // the files to concatenate
                src: ['DashboardApp/**.js', 'LoginApp/**.js'],
                // the location of the resulting JS file
                dest: 'dist/<%= pkg.name %>.js'
            }
        }
    });

    grunt.loadNpmTasks('grunt-maven');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');

    grunt.registerTask('default', ['mavenPrepare', 'jshint', 'concat', 'mavenDist']);

};