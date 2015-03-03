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
 *
 * Example:
 *
 *  var routes = [
 *      {
 *        name: 'Show readable version',
 *        pattern: 'versions/version/readable',
 *        action: versionModule
 *      },
 *      {
 *        name: 'Versions list',
 *        pattern: '/versions',
 *        action: versionsListModule
 *      }
 *  ];
 *
 *  var appRouter = new Router(routes);
 *  appRouter.route();
 *
 */

function Router(routes) {
    // Note that we're using incomplete patterns, so atm the order is very important.
    // See TODO below about matching algorithm.

    this.route = function route() {
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
    };
}

module.exports = Router;