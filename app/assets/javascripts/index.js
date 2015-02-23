// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var request = require('superagent');
var toArray = require("to-array");
var handlebars = require('handlebars');

var helpers = require('./helpers');

var showVersionModule = require('./show-version.js');


// Set environment specific constants.
var COMPOSER_URL = helpers.getComposerUrl();
var API_URL = helpers.getApiUrl();

function restore(archivedVersionPath, contentId, success, failure) {
    function updateContent(snapshot) {
        request.put(helpers.restoreContentUrlFor(contentId))
            .withCredentials()
            .set('Content-Type', 'application/json;charset=utf-8') // we need to set this
            .send(snapshot)
            .end(function(error, response) {
                if (response.ok) {
                    success(contentId);
                } else {
                    failure(contentId);
                }
        });
    }

    request.get(archivedVersionPath, function(error, response) {
        var snapshot = response.text;

        // To test restoring you can overwrite a local piece of content, e.g by doing:
        //snapshot = JSON.parse(snapshot);
        //snapshot.id = 'id-of-local-content-here';
        //snapshot = JSON.stringify(snapshot);

        updateContent(snapshot);
    });

}



/**
*  Restore confirmation
*  ====================
* */

var modalTemplate = require('./modal.handlebars');
var successNotification = document.getElementById('notification-success');
var failureNotification = document.getElementById('notification-failure');

function success(contentId) {
    failureNotification.classList.add('hidden');
    successNotification.classList.remove('hidden');

    var rawSuccessTemplate = 'Successfully restored snapshot. <a target="_blank" href="{{composerUrl}}/content/{{contentId}}">Open in Composer</a>';
    var successTemplate = handlebars.compile(rawSuccessTemplate);

    successNotification.innerHTML = successTemplate({composerUrl: COMPOSER_URL, contentId: contentId});
    window.location.hash = '#!';
}

function failure(contentId) {
    successNotification.classList.add('hidden');
    failureNotification.classList.remove('hidden');

    failureNotification.innerHTML = 'There was an error restoring the snapshot.';
    window.location.hash = '#!';
}

function modal(archivedVersionPath, contentId) {
    // Replace #modal HTML with template.
    var html = modalTemplate({archivedVersionPath: archivedVersionPath, contentId: contentId});
    var modalEl = document.getElementById('restore-modal');

    modalEl.innerHTML = html;

    // Set up listener on criteria.
    toArray(document.querySelectorAll('[data-component-restore-criteria]')).forEach(function(checkbox) {
        checkbox.addEventListener('click', enabledIfAllChecked);
    });

    // Set up listener on #restore-btn.
    document.getElementById('restore-btn').addEventListener('click', function(e) {
        var dataset = e.target.dataset;
        restore(dataset.archivedVersionPath, dataset.contentId, success, failure);
    });

    // Show modal.
    window.location.hash = '#modal-text';
}

// It ain't pretty exposing it on `window`, but let's keep it simple for now.
// Separating things into a single page app and an API would enable an equally
// simple but nice solution.
window.modal = modal;

function enabledIfAllChecked() {
    var modalEl = document.getElementById('restore-modal');
    var restoreBtn = document.getElementById('restore-btn' );

    var allChecked = toArray(modalEl.querySelectorAll('[data-component-restore-criteria]')).every(function(el) {
        return el.checked;
    });

    restoreBtn.disabled = !allChecked;
}


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

var routes = [
    {
        name: 'Show readable version',
        pattern: 'versions/version/readable',
        action: showVersionModule
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
