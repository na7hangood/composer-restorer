// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var request = require('superagent');
var toArray = require("to-array");

var API_URL = 'https://composer.local.dev-gutools.co.uk/api';

// TODO: How deal with live? (Don't want to update the live version)

function rawContentEndpoint(contentId) {
    return API_URL + '/content/Raw/:contentId'.replace(':contentId', contentId);
}

function restore(archivedVersionPath) {
    function updateContent(snapshot) {
        request.put(rawContentEndpoint('546f5e057d840e9e8565e25f'))
            .withCredentials()
            .send(snapshot)
            .end(function(error, response) {
                console.log('Response ok:', response.ok);
                console.log('Response text:', response.text);
                console.log('Error? :', error);
        });
    }

    //var archivedVersionPath = window.location.href + '?isLive=' + isLive + '&versionId=' + versionId;
    request.get(archivedVersionPath, function(error, response) {
        var snapshot = response.text;
        updateContent(snapshot)
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

