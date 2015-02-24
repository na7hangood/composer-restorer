var request = require('superagent');
var handlebars = require('handlebars');

var versionTemplate = require('./version.handlebars');


/**
 * Helpers
 */

function getJSONVersionPath() {
    return document.getElementById('versionBody').dataset.jsonUrl;
}

function extractTextField(element) {
    return element.fields.text;
}

function isTextElement(element) {
    return element.elementType === 'text';
}


/**
 * Version model
 */

function getTextFields(version) {
    return version.preview.blocks[0].elements
        .filter(isTextElement).map(extractTextField);
}


/**
 * Render version
 */

function renderVersion() {
    var versionPath = getJSONVersionPath();
    request.get(versionPath, function(error, response) {
        var version = JSON.parse(response.text);

        var textFields = getTextFields(version);

        var versionHTML = versionTemplate({textFields: textFields});

        var versionBodyEl = document.getElementById('versionBody');
        versionBodyEl.innerHTML = versionHTML;
    });
};

module.exports = renderVersion;
