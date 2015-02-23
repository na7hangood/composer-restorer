// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var Router = require('./router.js');

// Components
var versionModule = require('./version/version.js');
var versionsListModule = require('./versions-list/versions-list.js')

// Set up routing
var routes = [
    {
        name: 'Show readable version',
        pattern: 'versions/version/readable',
        action: versionModule
    },
    {
        name: 'Versions list',
        pattern: '/versions',
        action: versionsListModule
    }
];

var appRouter = new Router(routes);
appRouter.route();
