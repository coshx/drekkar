module.exports = function (grunt) {
    'use strict';

    var version = '0.1.0';

    // Project configuration
    grunt.initConfig({
        coffee: {
            dist: {
                options: {
                    bare: true,
                    sourceMap: false
                },
                files: {
                    '../drekkar/src/main/res/raw/drekkar.js': 'drekkar.coffee'
                }
            }
        },
        coffeelint: {
            dist: {
                options: {
                    configFile: 'coffeelint-config.json'
                },
                files: {
                    src: [ 'drekkar.coffee' ]
                }
            }
        },
        uglify: {
            dist: {
                options: {
                    mangle: false,
                    compression: true,
                    preserveComments: false,
                    banner: '/** Drekkar ' + version + ' - https://github.com/coshx/drekkar */\n'
                },
                files: {
                    '../drekkar/src/main/res/raw/drekkar.min.js': '../drekkar/src/main/res/raw/drekkar.js'
                }
            }
        },
        watch: {
            coffee: {
                options: {
                    atBegin: true,
                    interrupt: true
                },
                tasks: ['coffee'],
                files: '*.coffee'
            }
        }
    });

    // These plugins provide necessary tasks
    grunt.loadNpmTasks('grunt-contrib-coffee');
    grunt.loadNpmTasks('grunt-coffeelint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');

    // Default task
    grunt.registerTask('default', ['coffeelint', 'coffee']);
    grunt.registerTask('release', ['default', 'uglify']);
};
