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
                warName: 'ROOT',
                deliverables: ['**'],
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
            loginjs: {
                options: {
                    // define a string to put between each file in the concatenated output
                    separator: ';'
                },
                // the files to concatenate
                src: ['LoginApp/LoginApp.js', 'LoginApp/controllers/*.js'],
                // the location of the resulting JS file
                dest: 'dist/login.js'
            },
            dashboardjs: {
                options: {
                    // define a string to put between each file in the concatenated output
                    separator: ';'
                },
                // the files to concatenate
                src: ['DashboardApp/**/*.js'],
                // the location of the resulting JS file
                dest: 'dist/dashboard.js'
            },
            logincss: {
                src: ['LoginApp/styles/**/*.css'],
                dest: 'generated/login.css'
            },
            dashboardcss: {
                src: ['DashboardApp/styles/**/*.css'],
                dest: 'generated/dashboard.css'
            }
        },
        ngmin: {
            login: {
                expand: false,
                src: ['dist/login.js'],
                dest: 'generated/login.js'
            },
            dashboard: {
                expand: false,
                src: ['dist/dashboard.js'],
                dest: 'generated/dashboard.js'
            }
        },
        uglify: {
            login: {
                files: {
                    'dist/login.min.js': ['generated/login.js']
                }
            },
            dashboard: {
                files: {
                    'dist/dashboard.min.js': ['generated/dashboard.js']
                }
            }
        },
        cssmin: {
            login: {
                src: 'generated/login.css',
                dest: 'dist/login.min.css'
            },
            dashboard: {
                src: 'generated/dashboard.css',
                dest: 'dist/dashboard.min.css'
            }
        }
    });

    grunt.loadNpmTasks('grunt-maven');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-ngmin');
    grunt.loadNpmTasks('grunt-contrib-cssmin');

    grunt.registerTask('default',
        ['mavenPrepare',
            'jshint',
            'concat:loginjs',
            'ngmin:login',
            'concat:dashboardjs',
            'ngmin:dashboard',
            'uglify:login',
            'uglify:dashboard',
            'concat:logincss',
            'cssmin:login',
            'concat:dashboardcss',
            'cssmin:dashboard',
            'mavenDist']);
};