package employeedetail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class GetEmployeeLambdaFunctionHandler implements RequestStreamHandler, RequestHandler<Object, Object>{


	private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME = "Employee";
    private Regions REGION = Regions.US_WEST_2;
    private AmazonDynamoDBClient client;
    List<Employee> response;
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void handleRequest(InputStream input, OutputStream outputStream, Context context) throws JsonProcessingException, IOException {
      
       this.initDynamoDbClient();
      
       final ObjectMapper objectMapper = new ObjectMapper();
       JsonNode json = objectMapper.readTree(input);
       String id = json.path("params").path("querystring").path("id").asText();
       
       DynamoDBMapper mapper = new DynamoDBMapper(client);
       Employee itemObj = mapper.load(Employee.class, Integer.parseInt(id), "Active");
       try {
		outputStream.write(itemObj.toString().getBytes(Charset.forName("UTF-8")));
       } catch (IOException e) {
		// TODO Auto-generated catch block
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