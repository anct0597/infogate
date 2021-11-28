const system = require('system');
const url = system.args[1];

function waitFor(testFx, onReady, timeOutMillis) {
    console.log("Start waiting....")
    var maxTimeOutMillis = timeOutMillis ? timeOutMillis : 3000, //< Default Max Timeout is 3s
        start = new Date().getTime(),
        condition = false,
        interval = setInterval(function () {
            if ((new Date().getTime() - start < maxTimeOutMillis) && !condition) {
                // If not time-out yet and condition not yet fulfilled
                condition = (typeof (testFx) === "string" ? eval(testFx) : testFx()); //< defensive code
            } else {
                if (!condition) {
                    // If condition still not fulfilled (timeout but condition is 'false')
                    console.log("'waitFor()' timeout");
                    phantom.exit(1);
                } else {
                    // Condition fulfilled (timeout and/or condition is 'true')
                    console.log("'waitFor()' finished in " + (new Date().getTime() - start) + "ms.");
                    typeof (onReady) === "string" ? eval(onReady) : onReady(); //< Do what it's supposed to do once the condition is fulfilled
                    clearInterval(interval); //< Stop this interval
                }
            }
        }, 250); //< repeat check every 250ms
}


const page = require('webpage').create();
page.settings.resourceTimeout = 10000;
page.settings.loadImages = true;

page.onResourceRequested = function (requestData, request) {
    if ((/http:\/\/.+?\.css/gi).test(requestData['url']) || requestData.headers['Content-Type'] === 'text/css') {
        console.log('The url of the request is matching. Aborting: ' + requestData['url']);
        request.abort();
    }
};


page.open(url, function (status) {
    // Check for page load success
    if (status !== "success") {
        console.log("Unable to access network");
    } else {
        page.includeJs(
            // Include the https version, you can change this to http if you like.
            'https://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js',
            function () {
                // Wait for image loaded
                waitFor(function () {
                    // Check in the page if a specific element is now visible
                    return page.evaluate(function () {
                        return $('.image__slides div').children.length !== 0;
                    });
                }, function () {
                    console.log(page.content);
                    page.close();
                    phantom.exit();
                }, 5000);
            }
        );
    }
});