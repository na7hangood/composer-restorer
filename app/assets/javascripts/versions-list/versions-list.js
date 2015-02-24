// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var toArray = require("to-array");
var handlebars = require('handlebars');

var helpers = require('../helpers');
var restorerService = require('../restorer-service');

// Set environment specific constants.
var COMPOSER_URL = helpers.getComposerUrl();

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
        restorerService.restore(dataset.archivedVersionPath, dataset.contentId, success, failure);
    });

    // Show modal.
    window.location.hash = '#modal-text';
}


function enabledIfAllChecked() {
    var modalEl = document.getElementById('restore-modal');
    var restoreBtn = document.getElementById('restore-btn' );

    var allChecked = toArray(modalEl.querySelectorAll('[data-component-restore-criteria]')).every(function(el) {
        return el.checked;
    });

    restoreBtn.disabled = !allChecked;
}

// It ain't pretty exposing it on `window`, but let's keep it simple for now.
// Separating things into a single page app and an API would enable an equally
// simple but nice solution.
function exposeModal() {
    window.modal = modal;
}

module.exports = exposeModal;