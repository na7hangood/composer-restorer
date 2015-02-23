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

window.getTextFields = getTextFields;

module.exports = function() {
    var versionPath = "https://composer-restorer.local.dev-gutools.co.uk/content/54e735a1e4b043eebbe55d84/versions/version?isLive=false&versionId=5%2F4%2Fe%2F7%2F3%2F5%2F54e735a1e4b043eebbe55d84%2F2015-02-23T11%3A15%3A46.269Z";
    request.get(versionPath, function(error, response) {
        var version = JSON.parse(response.text);

        var textFields = getTextFields(version);

        var versionTemplate = require('./version.handlebars');
        var versionHTML = versionTemplate({textFields: textFields});

        var versionBodyEl = document.getElementById('versionBody');
        versionBodyEl.innerHTML = versionHTML;
    });
};


/**
 * Render version
 */

