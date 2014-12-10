// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var request = require('superagent');
var toArray = require("to-array");

/* TODO: Have a think about how to change this according to the environment we're in.
   There are various libraries/patterns for Browserify. */
var API_URL = 'https://composer.local.dev-gutools.co.uk/api';

var rawContentEndpoint = API_URL + '/content/restorer';

function restore(archivedVersionPath) {
    function updateContent(snapshot) {
        request.put(rawContentEndpoint)
            .withCredentials()
            .set('Content-Type', 'application/json;charset=utf-8') // we need to set this
            .send(snapshot)
            .end(function(error, response) {
                console.log('Response ok:', response.ok);
                console.log('Response text:', response.text);
                console.log('Error? :', error);
        });
    }

    request.get(archivedVersionPath, function(error, response) {
        var snapshot = response.text;

        // To test restoring you can overwrite a local piece of content, e.g by doing:
        //  snapshot = JSON.parse(snapshot);
        //  snapshot.id = 'local-composer-content-id-here';
        //  snapshot = JSON.stringify(snapshot);

        updateContent(snapshot);
    });

}



/**
*  Restore confirmation
*  ====================
* */

var modalTemplate = require('./modal.handlebars');

function modal(archivedVersionPath) {
    // Replace #modal HTML with template.
    var html = modalTemplate({archivedVersionPath: archivedVersionPath});
    var modalEl = document.getElementById('restore-modal');

    modalEl.innerHTML = html;

    // Set up listener on criteria.
    toArray(document.querySelectorAll('[data-component-restore-criteria]')).forEach(function(checkbox) {
        checkbox.addEventListener('click', enabledIfAllChecked)
    });

    // Set up listener on #restore-btn.
    document.getElementById('restore-btn').addEventListener('click', function(e) {
        restore(e.target.dataset.archivedVersionPath);
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

