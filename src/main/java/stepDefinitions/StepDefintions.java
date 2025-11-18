package stepDefinitions;

import java.util.Map;

import org.testng.Assert;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import utilities.ServiceAPI;

public class StepDefintions {

	ServiceAPI api = new ServiceAPI();
	String request;

	@And("User requests for {string} using the below data:")
	public void payload_Creation(String request, DataTable table) {
		Map<String, String> data = table.asMaps().get(0);
		api.preparePayload(request, data);
	}

	@And("User calls {string} API using {string} method")
	public void user_calls_api(String apiName, String method) {
		api.callAPI(apiName);
		// store ID only after Add Place
		if (apiName.equalsIgnoreCase("AddPlace")) {
			api.storePlaceId();
		}
	}

	@And("API response status code should be {int}")
	public void validate_status_code(int code) {
		Assert.assertEquals(api.getStatusCode(), code);
	}

	@And("Response field {string} should be {string}")
	public void validate_response_field(String key, String value) {
		Assert.assertEquals(api.getFieldValue(key), value);
	}
}
