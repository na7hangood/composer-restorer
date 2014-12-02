# Installation

## Set up Pan domain authentication
Follow the [dev-nginx README](https://github.com/guardian/dev-nginx). There is an nginx mapping file in `nginx/`.

## Install client-side dependencies and build JS/CSS
```
$ npm install
```

## Starting the app
```
$ sbt
[composer-restorer] $ run
```

The app will then be accessible locally at: https://composer-restorer.local.dev-gutools.co.uk/

## Developing

Instead of the asset pipeline in Play we're using  more standard frontend technologies:
`npm` for dependency management, CommonJS modules and `browserify` to bundle our JS up into
a file that is then imported by the browser. You'll find the source files in `assets/javascripts`
and the build files in `public/javascripts`.

You'll want to have `watchify` running in the background to trigger the build task when you modify
the JS.

```
npm run watch
```

