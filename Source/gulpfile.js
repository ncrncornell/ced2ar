// Load Gulp
//From: https://github.com/scotch-io/gulp-and-less-starter-kit/blob/master/gulpfile.js
var gulp = require('gulp'),
	gutil = require('gulp-util');
	plugins = require('gulp-load-plugins')()

	webroot = "src/main/webapp/"
	cssbuild = webroot + "styles/"
	scriptbuild = webroot + "scripts/";

// Start Watching: Run "gulp"
gulp.task('default', ['watch']);

// Minify jQuery Plugins: Run manually with: "gulp squish-jquery"
gulp.task('squish-jquery', function() {
  return gulp.src('assets/js/libs/**/*.js')
    .pipe(plugins.uglify())
    .pipe(plugins.concat('jquery.plugins.min.js'))
    .pipe(gulp.dest('build'));
});


// Minify Custom JS: Run manually with: "gulp build-js"
gulp.task('build-js', function() {
  return gulp.src(webroot+'pretty-scripts/**/*.js')
    .pipe(plugins.jshint())
    .pipe(plugins.jshint.reporter('jshint-stylish'))
    .pipe(plugins.uglify())
    //.pipe(plugins.concat('scripts.min.js'))
    .pipe(gulp.dest(scriptbuild));
});


// Less to CSS: Run manually with: "gulp build-css"
gulp.task('build-css', function() {
    return gulp.src(webroot+'less/**/*.less')
        .pipe(plugins.plumber())
        .pipe(plugins.less())
        .on('error', function (err) {
            gutil.log(err);
            this.emit('end');
        })
        .pipe(plugins.autoprefixer(
            {
                browsers: [
                    '> 1%',
                    'last 2 versions',
                    'firefox >= 4',
                    'safari 7',
                    'safari 8',
                    'IE 8',
                    'IE 9',
                    'IE 10',
                    'IE 11'
                ],
                cascade: false
            }
        ))
        .pipe(plugins.cssmin())
        .pipe(gulp.dest(cssbuild)).on('error', gutil.log);
});

// Default task
gulp.task('watch', function() {
    //gulp.watch('assets/js/libs/**/*.js', ['squish-jquery']);
    gulp.watch('assets/js/*.js', ['build-js']);
    gulp.watch(webroot+'less/**/*.less', ['build-css']);
});
