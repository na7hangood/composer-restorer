// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var request = require('superagent');
var toArray = require("to-array");
var handlebars = require('handlebars');

var showVersionModule = require('./version/show-version.js');
var versionsListModule = require('./versions-list/versions-list.js')




/**
 *
 * Router
 *
 * Simply routes a URL pattern to a function.
 *
 * Enables us to keep using server-side routing for now and then simply load
 * page-specific JS with the help of this router.
 *
 * It picks the FIRST matching route and ignores any subsequent matches.
 */

// Note that we're using incomplete patterns, so atm the order is very important.
// See TODO below about matching algorithm.
var routes = [
    {
        name: 'Show readable version',
        pattern: 'versions/version/readable',
        action: showVersionModule
    },
    {
        name: 'Versions list',
        pattern: '/versions',
        action: function() {}
    }
];

// Uses Array.prototype.some to only find the first matching route.
routes.some(function(route) {
    // TODO: Use more proper router. Just a very simple matching algorithm atm.
    if (window.location.pathname.indexOf(route.pattern) !== -1) {
        console.log('ROUTER:: Matched "' + route.name + '"');
        route.action();

        return true;
    } else {
        return false;
    }
});
