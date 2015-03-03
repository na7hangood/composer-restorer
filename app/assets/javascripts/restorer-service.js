var request = require('superagent');
var helpers = require('./helpers');

// Get the archived version from the Restorer API.
function getArchivedVersion(archivedVersionPath, cb) {
    request.get(archivedVersionPath, function(error, response) {
        cb(error, response);
    });
}

// Restore an archived version. Puts the version into the flexible-content API.
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

    getArchivedVersion(archivedVersionPath, function(error, response) {
        var snapshot = response.text;

        updateContent(snapshot);
    });
}

module.exports = {
    getArchivedVersion: getArchivedVersion,
    restore: restore
};