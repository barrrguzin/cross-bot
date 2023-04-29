function sendMessage(params) {
    // Declaring variables
    var response, request = new CurlHttpRequest();

    // Adding the required headers to the request
    request.AddHeader('Content-Type: application/json');

    // Forming the request that will send the message
    response = request.Post(params.URL, JSON.stringify({
        "subject": params.Subject,
        "messages": params.Message
    }));

    // If the response is different from 200 (OK), return an error with the content of the response
    if (request.getStatus() !== 200) {
        throw "API request failed: " + response;
    }
}

var params = JSON.parse(value);
sendMessage(params);