const system = require('system');
const url = system.args[1];

const page = require('webpage').create();
page.settings.loadImages = false;
page.settings.resourceTimeout = 10000;

page.onResourceRequested = function(requestData, request) {
    if ((/http:\/\/.+?\.css/gi).test(requestData['url']) || requestData.headers['Content-Type'] === 'text/css') {
        console.log('The url of the request is matching. Aborting: ' + requestData['url']);
        request.abort();
    }
};

page.open(url, function (status) {
    if (status !== 'success') {
        console.log("HTTP request failed!");
    } else {
        console.log(page.content);
    }

    page.close();
    phantom.exit();
});