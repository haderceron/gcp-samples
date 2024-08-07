package webhookendpoint;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.logging.Logger;


/**
 * This class represents a webhook endpoint that processes incoming HTTP requests.
 */
public class WebhookEndpointTest implements HttpFunction {

  private static final Logger logger = Logger.getLogger(WebhookEndpointTest.class.getName());

  /**
   * This method handles incoming HTTP requests to the webhook endpoint.
   *
   * @param request  The incoming HTTP request.
   * @param response The HTTP response to be sent back.
   * @throws Exception If an error occurs during processing.
   */
  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    BufferedWriter writer = response.getWriter();
    // Extract the payload from the request body
    String payload = extractPayload(request);
    logger.info("Payload: "+payload);

    // Extract the message data from the payload
    String messageData = getMessageData(payload);
    // Decode the base64-encoded message data
    String decodedData = decodeBase64Data(messageData);

    logger.info("Decoded Data: "+decodedData);
    // Send an ACK response
    writer.write("ACK");
  }

  /**
   * Extracts the payload from the HTTP request body.
   *
   * @param request The incoming HTTP request.
   * @return The payload as a string.
   * @throws IOException If an error occurs while reading the request body.
   */
  private String extractPayload(HttpRequest request) throws IOException{
     // Get a reader for the request body
     BufferedReader reader = request.getReader();
     StringBuilder bodyBuilder = new StringBuilder();
     String line;

     // Read the body line by line
     while ((line = reader.readLine()) != null) {
       bodyBuilder.append(line);
     }
 
     // Return the body as a string
     return bodyBuilder.toString();
  }

  /**
   * Extracts the message data from the payload.
   *
   * @param payload The payload as a string.
   * @return The message data as a string.
   */
  private String getMessageData(String payload){
    Gson gson = new Gson();
    // Parse the payload as JSON
    JsonElement element = gson.fromJson(payload, JsonElement.class);
    JsonObject jsonObject = element.getAsJsonObject();
    // Extract the message data
    return jsonObject.get("message").getAsJsonObject().get("data").getAsString();
  } 

  /**
   * Decodes the base64-encoded data.
   *
   * @param base64Data The base64-encoded data as a string.
   * @return The decoded data as a string.
   */
  private String decodeBase64Data(String base64Data){
    byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Data);
    return new String(decodedBytes);
  }

}
