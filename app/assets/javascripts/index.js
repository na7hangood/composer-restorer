// Bundled by browserify and put in 'public/'. We don't use the Play Asset pipeline.

var request = require('superagent');

var apiUrl = 'https://composer.local.dev-gutools.co.uk/api';
var apiContentUrl = apiUrl + '/content/54747d607d84173b978e38ec';

function restore(versionId, isLive) {
    //var archivedVersionPath = window.location.href + '?isLive=' + isLive + '&versionId=' + versionId;
    request.get(apiContentUrl)
        .withCredentials()
        .set('Content-Type', 'text/plain')
        .send("some text")
        .end(function(error, response) {
            console.log('Response ok:', response.ok);
            console.log('Response text:', response.text);
            console.log('Error? :', error);

            request.post(apiContentUrl)
                .withCredentials()
                .set('Content-Type', 'text/plain')
                .send("some text")
                .end(function(error, response) {
                    console.log('Response ok:', response.ok);
                    console.log('Response text:', response.text);
                    console.log('Error? :', error);
                });
        });
}

// It ain't pretty exposing it on `window`, but let's keep it simple for now.
// Separating things into a single page app and an API would enable an equally
// simple but nice solution.
window.onRestoreBtnClick = restore;
