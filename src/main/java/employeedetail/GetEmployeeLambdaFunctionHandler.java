package employeedetail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;

public class GetEmployeeLambdaFunctionHandler implements RequestStreamHandler, RequestHandler<Object, Object>{

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "Employee";
    private Regions REGION = Regions.US_WEST_2;
    private AmazonDynamoDBClient client;
   
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleRequest(InputStream input, OutputStream outputStream, Context context) throws JsonProcessingException, IOException {
      
       this.initDynamoDbClient();
      
       final ObjectMapper objectMapper = new ObjectMapper();
       JsonNode json = objectMapper.readTree(input);
       String id = json.path("params").path("querystring").path("id").asText();
       
       DynamoDBMapper mapper = new DynamoDBMapper(client);
       
       Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
       eav.put(":val1", new AttributeValue().withN(id));
       eav.put(":val2", new AttributeValue().withS("Active"));

       DynamoDBQueryExpression<Employee> queryExpression = new DynamoDBQueryExpression<Employee>()
           .withKeyConditionExpression("employeeId = :val1").withFilterExpression("employeeStatus = :val2").withExpressionAttributeValues(eav);
       PaginatedQueryList<Employee> itemObj = mapper.query(Employee.class, queryExpression);
       
      try {
    	   if (itemObj !=null && itemObj.size()>0)
    		   outputStream.write(new ResponseMessage(itemObj.get(0).toString()).toString().getBytes(Charset.forName("UTF-8")));
    	   else
    		   outputStream.write(new ResponseMessage("The employee is Inactive or does not exist in database.").toString().getBytes(Charset.forName("UTF-8")));
       } catch (IOException e) {
		e.printStackTrace();
       }     
       
    }
    
    // -----------------------INITIATE DB CLIENT ----------------------------------------
    public void initDynamoDbClient() {
        client = new AmazonDynamoDBClient();
        client.setRegion(com.amazonaws.regions.Region.getRegion(REGION));
        this.dynamoDb = new DynamoDB(client);
    }

	@Override
	public Object handleRequest(Object arg0, Context arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
