var request = require('superagent');
var handlebars = require('handlebars');
var helpers = require('./helpers');


/**
 * Version model
 */

function isTextElement(element) {
    return element.elementType === 'text';
}


function getTextFields(version) {
    function extractTextField(element) {
        return element.fields.text;
    }

    return version.preview.blocks[0].elements
        .filter(isTextElement).map(extractTextField);
}

/**
 * Helpers
 */

function getJSONVersionPath() {
    return document.getElementById('versionBody').dataset.jsonUrl;
}

/**
 * Render version
 */

function renderVersion() {
    var versionPath = getJSONVersionPath();
    request.get(versionPath, function(error, response) {
        var version = JSON.parse(response.text);

        var textFields = getTextFields(version);

        var versionTemplate = require('./version.handlebars');
        var versionHTML = versionTemplate({textFields: textFields});

        var versionBodyEl = document.getElementById('versionBody');
        versionBodyEl.innerHTML = versionHTML;
    });
};

module.exports = renderVersion;
