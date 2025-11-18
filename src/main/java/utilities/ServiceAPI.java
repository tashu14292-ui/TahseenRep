package utilities;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(ServiceAPI.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private JsonNode requestBody;
    private Response response;
    private File file;

    private final String BASE_PATH = System.getProperty("user.dir") + "/testData/";

    // --------------------
    // PREPARE PAYLOAD
    // --------------------
    public void preparePayload(String request, Map<String, String> data) {
        try {
            logger.info("Preparing payload for request: {}", request);

            switch (request.toLowerCase()) {
            case "add place":
                file = new File(BASE_PATH + "addPlace.json");
                break;
            case "put place":
                file = new File(BASE_PATH + "updatePlace.json");
                break;
            case "delete place":
                file = new File(BASE_PATH + "deletePlace.json");
                break;
            default:
                logger.error("Invalid request type: {}", request);
                Assert.fail("Request is invalid: " + request);
            }

            JsonNode json = mapper.readTree(file);

            if (request.equalsIgnoreCase("Add Place")) {

                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("name", data.get("name"));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("language", data.get("language"));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("address", data.get("address"));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("accuracy",
                        Integer.parseInt(data.get("accuracy")));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("phone_number", data.get("phone_number"));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("website", data.get("website"));

                JsonNode location = json.get("location");
                ((com.fasterxml.jackson.databind.node.ObjectNode) location).put("lat",
                        Double.parseDouble(data.get("lat")));
                ((com.fasterxml.jackson.databind.node.ObjectNode) location).put("lng",
                        Double.parseDouble(data.get("lng")));

                String[] typesArray = data.get("types").split(",");
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).putPOJO("types", typesArray);

            } else if (request.equalsIgnoreCase("Put Place")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("address", data.get("address"));
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("place_id", readPlaceId());
            } else if (request.equalsIgnoreCase("Delete Place")) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) json).put("place_id", readPlaceId());
            }

            this.requestBody = json;
            logger.info("Payload prepared successfully.");

        } catch (Exception e) {
            logger.error("Error preparing payload: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to prepare payload", e);
        }
    }

    // --------------------
    // API CALL
    // --------------------
    public void callAPI(String apiName) {
        try {
            logger.info("Calling API: {}", apiName);

            String endpoint;
            switch (apiName) {
            case "AddPlace":
                endpoint = "/maps/api/place/add/json";
                break;
            case "GetPlace":
                endpoint = "/maps/api/place/get/json";
                break;
            case "PutPlace":
                endpoint = "/maps/api/place/update/json";
                break;
            case "DeletePlace":
                endpoint = "/maps/api/place/delete/json";
                break;
            default:
                logger.error("Unknown API: {}", apiName);
                throw new RuntimeException("Unknown API: " + apiName);
            }

            RestAssured.baseURI = "https://rahulshettyacademy.com";

            switch (apiName) {
            case "AddPlace":
                response = RestAssured.given().queryParam("key", "qaclick123")
                        .header("Content-Type", "application/json").body(requestBody.toString()).when().log().all()
                        .post(endpoint).then().log().all().extract().response();
                break;

            case "PutPlace":
                response = RestAssured.given().queryParam("key", "qaclick123")
                        .header("Content-Type", "application/json").body(requestBody.toString()).when().log().all()
                        .put(endpoint).then().log().all().extract().response();
                break;

            case "GetPlace":
                response = RestAssured.given().queryParam("key", "qaclick123")
                        .queryParam("place_id", readPlaceId()).when().log().all().get(endpoint).then().log().all()
                        .extract().response();
                break;

            case "DeletePlace":
                response = RestAssured.given().queryParam("key", "qaclick123")
                        .header("Content-Type", "application/json").body(requestBody.toString()).when().log().all()
                        .delete(endpoint).then().log().all().extract().response();
                break;
            }

            logger.info("API call completed. Status Code: {}", response.getStatusCode());

        } catch (Exception e) {
            logger.error("API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("API call failed", e);
        }
    }

    // --------------------
    // STORE place_id
    // --------------------
    public void storePlaceId() {
        try {
            logger.info("Storing place_id...");

            String placeId = response.jsonPath().getString("place_id");
            String filePath = System.getProperty("user.dir") + "/apiResponseData/place_id.txt";

            Files.write(Paths.get(filePath), placeId.getBytes());

            logger.info("place_id stored successfully: {}", placeId);

        } catch (Exception e) {
            logger.error("Error storing place_id: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store place_id", e);
        }
    }

    // --------------------
    // READ place_id
    // --------------------
    public String readPlaceId() {
        try {
            logger.info("Reading place_id...");

            String filePath = System.getProperty("user.dir") + "/apiResponseData/place_id.txt";

            String placeId = new String(Files.readAllBytes(Paths.get(filePath)));
            logger.info("place_id loaded: {}", placeId);

            return placeId;

        } catch (Exception e) {
            logger.error("Error reading place_id: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read place_id", e);
        }
    }

    // --------------------
    // GETTERS
    // --------------------
    public int getStatusCode() {
        return response.getStatusCode();
    }

    public String getFieldValue(String key) {
        return response.jsonPath().getString(key);
    }
}
