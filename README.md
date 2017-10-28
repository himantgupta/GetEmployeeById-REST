# GetEmployeeById-REST
API Gateway Template + JSON parsing for Query String + Lambda + Dynamo DB

This code assumes that the API gateway is configured correctly with the template settings to get the query string from request to Lambda function.
This code will also get an item from dynamo DB based on the query string received in request.
For JSON parsing , jackson is used.

End point to test is  : https://vsdc77v5ph.execute-api.us-west-2.amazonaws.com/prod/getemployee?id=3


